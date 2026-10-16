
package org.example.mongoDB.service;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.dao.CierreMensualCuentaCorrienteMongoDAO;
import org.example.mongoDB.dao.CuentaCorrienteMongoDAO;
import org.example.mongoDB.model.CierreMensualCuentaCorriente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

public class CierreMensualCuentaCorrienteService {

    private final CierreMensualCuentaCorrienteMongoDAO cierreDAO;
    private final CuentaCorrienteMongoDAO cuentaDAO;

    private final MongoCollection<Document> movimientos;
    private final MongoCollection<Document> aperturas;

    public CierreMensualCuentaCorrienteService() {

        cierreDAO = new CierreMensualCuentaCorrienteMongoDAO();
        cuentaDAO = new CuentaCorrienteMongoDAO();

        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        movimientos = database.getCollection(
                "movimientos_cuenta_corriente"
        );

        aperturas = database.getCollection(
                "aperturas_cuenta_corriente"
        );

        cierreDAO.crearIndiceUnico();
    }

    // =========================================
    // CERRAR MES DE UNA CUENTA
    // =========================================

    public CierreMensualCuentaCorriente cerrarMes(
            String cuentaId,
            int anio,
            int mes
    ) {

        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException(
                    "El mes debe estar entre 1 y 12."
            );
        }

        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin = inicio.plusMonths(1);

        if (!fin.isBefore(LocalDate.now()) &&
                !fin.isEqual(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "No se puede cerrar un mes que todavía no terminó."
            );
        }

        if (inicio.isBefore(LocalDate.of(2026, 10, 1))) {
            throw new IllegalArgumentException(
                    "El primer período contable es octubre de 2026."
            );
        }

        Date fechaInicio = Date.from(
                inicio.atStartOfDay()
                        .toInstant(ZoneOffset.UTC)
        );

        Date fechaFin = Date.from(
                fin.atStartOfDay()
                        .toInstant(ZoneOffset.UTC)
        );

        try (ClientSession session =
                     MongoSingleton.getInstance().startSession()) {

            session.startTransaction();

            try {

                if (cierreDAO.buscarPorPeriodo(
                        session, cuentaId, anio, mes
                ) != null) {
                    throw new IllegalArgumentException(
                            "Este período ya fue cerrado."
                    );
                }

                Document cuenta = cuentaDAO.buscarPorId(
                        session, cuentaId
                );

                if (cuenta == null) {
                    throw new IllegalArgumentException(
                            "La cuenta corriente no existe."
                    );
                }

                BigDecimal saldoInicial =
                        obtenerSaldoInicial(
                                session, cuentaId, inicio
                        );

                BigDecimal totalDebitos = BigDecimal.ZERO;
                BigDecimal totalCreditos = BigDecimal.ZERO;

                List<Document> lista = movimientos.find(
                        session,
                        and(
                                eq(
                                        "cuenta_corriente_id",
                                        new ObjectId(cuentaId)
                                ),
                                gte("fecha", fechaInicio),
                                lt("fecha", fechaFin)
                        )
                ).into(new java.util.ArrayList<>());

                for (Document movimiento : lista) {

                    BigDecimal importe = movimiento
                            .get("importe", Decimal128.class)
                            .bigDecimalValue();

                    String tipo = movimiento.getString("tipo");

                    if ("DEBITO".equals(tipo)) {
                        totalDebitos = totalDebitos.add(importe);
                    } else if ("CREDITO".equals(tipo)) {
                        totalCreditos = totalCreditos.add(importe);
                    }
                }

                BigDecimal saldoFinal = saldoInicial
                        .add(totalDebitos)
                        .subtract(totalCreditos);

                CierreMensualCuentaCorriente cierre =
                        new CierreMensualCuentaCorriente(
                                null,
                                cuentaId,
                                anio,
                                mes,
                                saldoInicial,
                                totalDebitos,
                                totalCreditos,
                                saldoFinal,
                                new Date()
                        );

                cierreDAO.agregar(session, cierre);

                session.commitTransaction();

                return cierre;

            } catch (Exception e) {

                if (session.hasActiveTransaction()) {
                    session.abortTransaction();
                }

                throw e;
            }
        }
    }

    // =========================================
    // OBTENER SALDO INICIAL
    // =========================================

    private BigDecimal obtenerSaldoInicial(
            ClientSession session,
            String cuentaId,
            LocalDate periodo
    ) {

        LocalDate primerPeriodo = LocalDate.of(2026, 10, 1);

        if (periodo.equals(primerPeriodo)) {

            Document apertura = aperturas.find(
                    session,
                    and(
                            eq(
                                    "cuenta_corriente_id",
                                    new ObjectId(cuentaId)
                            ),
                            eq("anio", 2026),
                            eq("mes", 10)
                    )
            ).first();

            if (apertura == null) {
                throw new IllegalArgumentException(
                        "La cuenta no tiene saldo de apertura."
                );
            }

            return apertura
                    .get("saldo_inicial", Decimal128.class)
                    .bigDecimalValue();
        }

        LocalDate anterior = periodo.minusMonths(1);

        Document cierreAnterior = cierreDAO.buscarPorPeriodo(
                session,
                cuentaId,
                anterior.getYear(),
                anterior.getMonthValue()
        );

        if (cierreAnterior == null) {
            throw new IllegalArgumentException(
                    "Primero debe cerrarse el mes anterior."
            );
        }

        return cierreAnterior
                .get("saldo_final", Decimal128.class)
                .bigDecimalValue();
    }

    // =========================================
    // CONSULTAR CIERRES DE UNA CUENTA
    // =========================================

    public List<Document> consultarCierres(String cuentaId) {
        return cierreDAO.buscarPorCuenta(cuentaId);
    }

    // =========================================
    // LISTAR TODOS LOS CIERRES
    // =========================================

    public List<Document> listarCierres() {
        return cierreDAO.listarTodos();
    }
}
