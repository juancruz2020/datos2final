
package org.example.mongoDB.service;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.UpdateOptions;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.dao.CuentaCorrienteMongoDAO;
import org.example.mongoDB.dao.MovimientoCuentaCorrienteMongoDAO;
import org.example.mongoDB.dao.CierreMensualCuentaCorrienteMongoDAO;
import org.example.mongoDB.model.CuentaCorriente;
import org.example.mongoDB.model.MovimientoCuentaCorriente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

public class CuentaCorrienteService {

    private final CuentaCorrienteMongoDAO cuentaDAO;
    private final MovimientoCuentaCorrienteMongoDAO movimientoDAO;
    private final CierreMensualCuentaCorrienteMongoDAO cierreDAO;

    private final MongoCollection<Document> aperturas;

    // =========================================
    // CONSTRUCTOR
    // =========================================

    public CuentaCorrienteService() {

        this.cuentaDAO = new CuentaCorrienteMongoDAO();
        this.movimientoDAO =
                new MovimientoCuentaCorrienteMongoDAO();
        this.cierreDAO =
                new CierreMensualCuentaCorrienteMongoDAO();

        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.aperturas = database.getCollection(
                "aperturas_cuenta_corriente"
        );

        movimientoDAO.crearIndiceUnico();

        aperturas.createIndex(
                Indexes.compoundIndex(
                        Indexes.ascending("cuenta_corriente_id"),
                        Indexes.ascending("anio"),
                        Indexes.ascending("mes")
                ),
                new IndexOptions().unique(true)
        );
    }

    // =========================================
    // CREAR CUENTA CORRIENTE
    // =========================================

    public CuentaCorriente crearCuenta(String clienteId) {

        if (clienteId == null || clienteId.isBlank()) {
            throw new IllegalArgumentException(
                    "El cliente es obligatorio."
            );
        }

        if (cuentaDAO.buscarPorCliente(clienteId) != null) {
            throw new IllegalArgumentException(
                    "El cliente ya tiene una cuenta corriente."
            );
        }

        CuentaCorriente cuenta = new CuentaCorriente(
                null,
                clienteId,
                BigDecimal.ZERO,
                new Date()
        );

        cuentaDAO.agregar(cuenta);

        registrarApertura(
                cuenta.getId(),
                BigDecimal.ZERO
        );

        return cuenta;
    }

    // =========================================
    // REGISTRAR APERTURA
    // =========================================

    private void registrarApertura(
            String cuentaId,
            BigDecimal saldoInicial
    ) {

        if (cuentaId == null || !ObjectId.isValid(cuentaId)) {
            throw new IllegalArgumentException(
                    "El ID de la cuenta corriente no es válido."
            );
        }

        if (saldoInicial == null
                || saldoInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El saldo inicial no puede ser negativo."
            );
        }

        LocalDate hoy = LocalDate.now(ZoneOffset.UTC);

        ObjectId idCuenta = new ObjectId(cuentaId);

        Document apertura = new Document(
                "cuenta_corriente_id", idCuenta
        )
                .append("anio", hoy.getYear())
                .append("mes", hoy.getMonthValue())
                .append(
                        "saldo_inicial",
                        new Decimal128(saldoInicial)
                );

        aperturas.updateOne(
                and(
                        eq("cuenta_corriente_id", idCuenta),
                        eq("anio", hoy.getYear()),
                        eq("mes", hoy.getMonthValue())
                ),
                new Document("$setOnInsert", apertura),
                new UpdateOptions().upsert(true)
        );
    }

    // =========================================
    // BUSCAR CUENTA POR CLIENTE
    // =========================================

    public Document buscarPorCliente(String clienteId) {

        if (clienteId == null || clienteId.isBlank()) {
            throw new IllegalArgumentException(
                    "El cliente es obligatorio."
            );
        }

        return cuentaDAO.buscarPorCliente(clienteId);
    }

    // =========================================
    // LISTAR CUENTAS
    // =========================================

    public List<Document> listarCuentas() {
        return cuentaDAO.listarTodos();
    }

    // =========================================
    // CONSULTAR SALDO
    // =========================================

    public BigDecimal consultarSaldo(String clienteId) {

        Document cuenta = buscarPorCliente(clienteId);

        if (cuenta == null) {
            throw new IllegalArgumentException(
                    "El cliente no tiene una cuenta corriente."
            );
        }

        return cuenta
                .get("saldo", Decimal128.class)
                .bigDecimalValue();
    }

    // =========================================
    // REGISTRAR DEBITO
    // =========================================

    public void registrarDebito(
            String clienteId,
            String referenciaId,
            String descripcion,
            BigDecimal importe
    ) {

        registrarMovimiento(
                clienteId,
                "DEBITO",
                referenciaId,
                descripcion,
                importe
        );
    }

    // =========================================
    // REGISTRAR CREDITO
    // =========================================

    public void registrarCredito(
            String clienteId,
            String referenciaId,
            String descripcion,
            BigDecimal importe
    ) {

        registrarMovimiento(
                clienteId,
                "CREDITO",
                referenciaId,
                descripcion,
                importe
        );
    }

    // =========================================
    // REGISTRAR MOVIMIENTO CON TRANSACCION
    // =========================================

    private void registrarMovimiento(
            String clienteId,
            String tipo,
            String referenciaId,
            String descripcion,
            BigDecimal importe
    ) {

        if (importe == null
                || importe.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El importe debe ser mayor a cero."
            );
        }

        if (referenciaId == null || referenciaId.isBlank()) {
            throw new IllegalArgumentException(
                    "La referencia es obligatoria."
            );
        }

        Document cuenta = buscarPorCliente(clienteId);

        if (cuenta == null) {
            throw new IllegalArgumentException(
                    "El cliente no tiene una cuenta corriente."
            );
        }

        String cuentaId =
                cuenta.getObjectId("_id").toHexString();

        try (ClientSession session =
                     MongoSingleton.getInstance().startSession()) {

            session.startTransaction();

            try {

                // =========================================
                // OBTENER PRIMER PERIODO DE LA CUENTA
                // =========================================

                Document primeraApertura = aperturas.find(
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

                if (primeraApertura == null) {
                    throw new IllegalStateException(
                            "La cuenta corriente no tiene una apertura registrada."
                    );
                }

                LocalDate primerPeriodo = LocalDate.of(
                        primeraApertura.getInteger("anio"),
                        primeraApertura.getInteger("mes"),
                        1
                );

                LocalDate hoy =
                        LocalDate.now(ZoneOffset.UTC);

                LocalDate inicioMesActual =
                        hoy.withDayOfMonth(1);

                // =========================================
                // VALIDAR CIERRES ANTERIORES
                // =========================================

                LocalDate periodo = primerPeriodo;

                while (periodo.isBefore(inicioMesActual)) {

                    Document cierre =
                            cierreDAO.buscarPorPeriodo(
                                    session,
                                    cuentaId,
                                    periodo.getYear(),
                                    periodo.getMonthValue()
                            );

                    if (cierre == null) {
                        throw new IllegalStateException(
                                "Hay períodos anteriores sin cerrar: "
                                        + periodo.getMonthValue()
                                        + "/"
                                        + periodo.getYear()
                        );
                    }

                    periodo = periodo.plusMonths(1);
                }

                // =========================================
                // CONSULTAR SALDO ACTUAL
                // =========================================

                Document cuentaActual =
                        cuentaDAO.buscarPorId(
                                session,
                                cuentaId
                        );

                if (cuentaActual == null) {
                    throw new IllegalArgumentException(
                            "No se encontró la cuenta corriente."
                    );
                }

                BigDecimal saldoActual = cuentaActual
                        .get("saldo", Decimal128.class)
                        .bigDecimalValue();

                BigDecimal nuevoSaldo;

                // El saldo representa dinero disponible.
                if ("DEBITO".equals(tipo)) {

                    if (saldoActual.compareTo(importe) < 0) {
                        throw new IllegalArgumentException(
                                "Saldo insuficiente para realizar la compra."
                        );
                    }

                    nuevoSaldo =
                            saldoActual.subtract(importe);

                } else if ("CREDITO".equals(tipo)) {

                    nuevoSaldo =
                            saldoActual.add(importe);

                } else {

                    throw new IllegalArgumentException(
                            "Tipo de movimiento no válido."
                    );
                }

                // =========================================
                // CREAR MOVIMIENTO
                // =========================================

                MovimientoCuentaCorriente movimiento =
                        new MovimientoCuentaCorriente(
                                null,
                                cuentaId,
                                tipo,
                                referenciaId,
                                descripcion,
                                importe,
                                new Date()
                        );

                // =========================================
                // GUARDAR MOVIMIENTO Y ACTUALIZAR SALDO
                // =========================================

                movimientoDAO.agregar(
                        session,
                        movimiento
                );

                cuentaDAO.actualizarSaldo(
                        session,
                        cuentaId,
                        nuevoSaldo
                );

                session.commitTransaction();

            } catch (Exception e) {

                if (session.hasActiveTransaction()) {
                    session.abortTransaction();
                }

                throw e;
            }
        }
    }

    // =========================================
    // CONSULTAR HISTORIAL
    // =========================================

    public List<Document> consultarHistorial(
            String clienteId
    ) {

        Document cuenta = buscarPorCliente(clienteId);

        if (cuenta == null) {
            throw new IllegalArgumentException(
                    "El cliente no tiene una cuenta corriente."
            );
        }

        String cuentaId =
                cuenta.getObjectId("_id").toHexString();

        return movimientoDAO.buscarPorCuenta(cuentaId);
    }
}
