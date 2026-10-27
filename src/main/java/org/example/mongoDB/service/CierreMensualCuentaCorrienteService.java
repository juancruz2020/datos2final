
package org.example.mongoDB.service;

import org.example.consola.TablaConsola;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import org.example.conecciones.MongoSingleton;
import org.example.logging.AppLog;
import org.example.mongoDB.dao.CierreMensualCuentaCorrienteMongoDAO;
import org.example.mongoDB.dao.CuentaCorrienteMongoDAO;
import org.example.mongoDB.model.CierreMensualCuentaCorriente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

public class CierreMensualCuentaCorrienteService {

    private final CierreMensualCuentaCorrienteMongoDAO cierreDAO;
    private final CuentaCorrienteMongoDAO cuentaDAO;

    private final MongoCollection<Document> movimientos;
    private final MongoCollection<Document> aperturas;

    // =========================================
    // CONSTRUCTOR
    // =========================================

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

        if (cuentaId == null || !ObjectId.isValid(cuentaId)) {
            throw new IllegalArgumentException(
                    "El ID de la cuenta corriente no es válido."
            );
        }

        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException(
                    "El mes debe estar entre 1 y 12."
            );
        }

        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin = inicio.plusMonths(1);

        LocalDate hoy = LocalDate.now(ZoneOffset.UTC);

        if (fin.isAfter(hoy)) {
            throw new IllegalArgumentException(
                    "No se puede cerrar un mes que todavía no terminó."
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

                // =========================================
                // VERIFICAR CUENTA
                // =========================================

                Document cuenta = cuentaDAO.buscarPorId(
                        session,
                        cuentaId
                );

                if (cuenta == null) {
                    throw new IllegalArgumentException(
                            "La cuenta corriente no existe."
                    );
                }

                // =========================================
                // OBTENER PRIMER PERIODO DE LA CUENTA
                // =========================================

                Document apertura = buscarPrimeraApertura(
                        session,
                        cuentaId
                );

                if (apertura == null) {
                    throw new IllegalArgumentException(
                            "La cuenta no tiene saldo de apertura."
                    );
                }

                LocalDate primerPeriodo = LocalDate.of(
                        apertura.getInteger("anio"),
                        apertura.getInteger("mes"),
                        1
                );

                if (inicio.isBefore(primerPeriodo)) {
                    throw new IllegalArgumentException(
                            "No se puede cerrar un período anterior a la apertura de la cuenta."
                    );
                }

                // =========================================
                // EVITAR CIERRES DUPLICADOS
                // =========================================

                if (cierreDAO.buscarPorPeriodo(
                        session,
                        cuentaId,
                        anio,
                        mes
                ) != null) {

                    throw new IllegalArgumentException(
                            "Este período ya fue cerrado."
                    );
                }

                // =========================================
                // CALCULAR SALDO INICIAL
                // =========================================

                BigDecimal saldoInicial =
                        obtenerSaldoInicial(
                                session,
                                cuentaId,
                                inicio,
                                primerPeriodo,
                                apertura
                        );

                // =========================================
                // OBTENER MOVIMIENTOS DEL MES
                // =========================================

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
                ).into(new ArrayList<>());

                BigDecimal totalDebitos = BigDecimal.ZERO;
                BigDecimal totalCreditos = BigDecimal.ZERO;

                for (Document movimiento : lista) {

                    Decimal128 importeDecimal =
                            movimiento.get("importe", Decimal128.class);

                    if (importeDecimal == null) {
                        throw new IllegalStateException(
                                "Se encontró un movimiento sin importe válido."
                        );
                    }

                    BigDecimal importe =
                            importeDecimal.bigDecimalValue();

                    String tipo = movimiento.getString("tipo");

                    if ("DEBITO".equals(tipo)) {

                        totalDebitos = totalDebitos.add(importe);

                    } else if ("CREDITO".equals(tipo)) {

                        totalCreditos = totalCreditos.add(importe);

                    } else {

                        throw new IllegalStateException(
                                "Tipo de movimiento no reconocido: " + tipo
                        );
                    }
                }

                // =========================================
                // CALCULAR SALDO FINAL
                // =========================================

                BigDecimal saldoFinal = saldoInicial
                        .add(totalCreditos)
                        .subtract(totalDebitos);

                // =========================================
                // GUARDAR CIERRE MENSUAL
                // =========================================

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
                AppLog.info(CierreMensualCuentaCorrienteService.class,
                        "Cierre mensual de cuenta corriente confirmado.");

                return cierre;

            } catch (Exception e) {

                if (session.hasActiveTransaction()) {
                    session.abortTransaction();
                    AppLog.warning(CierreMensualCuentaCorrienteService.class,
                            "Cierre mensual revertido por un error.");
                }

                throw e;
            }
        }
    }

    // =========================================
    // BUSCAR PRIMERA APERTURA DE UNA CUENTA
    // =========================================

    private Document buscarPrimeraApertura(
            ClientSession session,
            String cuentaId
    ) {

        return aperturas.find(
                session,
                eq(
                        "cuenta_corriente_id",
                        new ObjectId(cuentaId)
                )
        )
        .sort(
                new Document("anio", 1)
                        .append("mes", 1)
        )
        .first();
    }

    // =========================================
    // OBTENER SALDO INICIAL
    // =========================================

    private BigDecimal obtenerSaldoInicial(
            ClientSession session,
            String cuentaId,
            LocalDate periodo,
            LocalDate primerPeriodo,
            Document apertura
    ) {

        // Para el primer mes utilizamos el saldo de apertura.
        if (periodo.equals(primerPeriodo)) {

            Decimal128 saldoApertura =
                    apertura.get("saldo_inicial", Decimal128.class);

            if (saldoApertura == null) {
                throw new IllegalStateException(
                        "La apertura no tiene un saldo inicial válido."
                );
            }

            return saldoApertura.bigDecimalValue();
        }

        // Para los siguientes meses utilizamos el
        // saldo final del cierre anterior.
        LocalDate anterior = periodo.minusMonths(1);

        Document cierreAnterior = cierreDAO.buscarPorPeriodo(
                session,
                cuentaId,
                anterior.getYear(),
                anterior.getMonthValue()
        );

        if (cierreAnterior == null) {
            throw new IllegalArgumentException(
                    "Primero debe cerrarse el período anterior: "
                            + anterior.getMonthValue()
                            + "/"
                            + anterior.getYear()
            );
        }

        Decimal128 saldoAnterior =
                cierreAnterior.get("saldo_final", Decimal128.class);

        if (saldoAnterior == null) {
            throw new IllegalStateException(
                    "El cierre anterior no tiene saldo final válido."
            );
        }

        return saldoAnterior.bigDecimalValue();
    }

    // =========================================
    // CONSULTAR CIERRES DE UNA CUENTA
    // =========================================

    public List<Document> consultarCierres(String cuentaId) {
        return TablaConsola.mostrar("CierreMensualCuentaCorrienteService.consultarCierres", cierreDAO.buscarPorCuenta(cuentaId));
    }


}
