
package org.example.mongoDB.service;

import org.example.consola.TablaConsola;

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
import org.example.logging.AppLog;
import org.example.mongoDB.dao.CuentaCorrienteMongoDAO;
import org.example.mongoDB.dao.MovimientoCuentaCorrienteMongoDAO;
import org.example.mongoDB.dao.CierreMensualCuentaCorrienteMongoDAO;
import org.example.mongoDB.model.CuentaCorriente;
import org.example.mongoDB.model.MovimientoCuentaCorriente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

public class CuentaCorrienteService {

    private static final ZoneId ZONA =
            ZoneId.of("America/Argentina/Buenos_Aires");

    private final CuentaCorrienteMongoDAO cuentaDAO;
    private final MovimientoCuentaCorrienteMongoDAO movimientoDAO;
    private final CierreMensualCuentaCorrienteMongoDAO cierreDAO;

    private final MongoCollection<Document> aperturas;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

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

    // ==========================================
    // CREAR CUENTA CORRIENTE
    // ==========================================

    public CuentaCorriente crearCuenta(String clienteId) {

        validarId(clienteId, "cliente");

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

        try (ClientSession session = MongoSingleton
                .getInstance()
                .startSession()) {

            session.startTransaction();

            try {
                crearCuenta(session, cuenta);

                session.commitTransaction();
                AppLog.info(CuentaCorrienteService.class,
                        "Creación de cuenta corriente confirmada.");
            } catch (RuntimeException e) {
                if (session.hasActiveTransaction()) {
                    session.abortTransaction();
                    AppLog.warning(CuentaCorrienteService.class,
                            "Creación de cuenta corriente revertida.");
                }
                throw e;
            }
        }

        return cuenta;
    }

    public CuentaCorriente crearCuenta(
            ClientSession session,
            String clienteId
    ) {
        validarId(clienteId, "cliente");

        if (session == null || !session.hasActiveTransaction()) {
            throw new IllegalArgumentException(
                    "La creación de cuenta necesita una transacción activa."
            );
        }

        CuentaCorriente cuenta = new CuentaCorriente(
                null,
                clienteId,
                BigDecimal.ZERO,
                new Date()
        );

        crearCuenta(session, cuenta);
        return cuenta;
    }

    private void crearCuenta(ClientSession session, CuentaCorriente cuenta) {
        cuentaDAO.agregar(session, cuenta);

        // La apertura define el saldo histórico de la cuenta.
        registrarApertura(session, cuenta.getId(), BigDecimal.ZERO);
    }

    // ==========================================
    // REGISTRAR APERTURA INICIAL
    // ==========================================

    private void registrarApertura(
            ClientSession session,
            String cuentaId,
            BigDecimal saldoInicial
    ) {

        validarId(cuentaId, "cuenta corriente");

        if (saldoInicial == null
                || saldoInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El saldo inicial no puede ser negativo."
            );
        }

        YearMonth periodo = YearMonth.now(ZONA);

        ObjectId idCuenta = new ObjectId(cuentaId);

        Document apertura = new Document(
                "cuenta_corriente_id", idCuenta
        )
                .append("anio", periodo.getYear())
                .append("mes", periodo.getMonthValue())
                .append(
                        "saldo_inicial",
                        new Decimal128(saldoInicial)
                );

        aperturas.updateOne(
                session,
                and(
                        eq("cuenta_corriente_id", idCuenta),
                        eq("anio", periodo.getYear()),
                        eq("mes", periodo.getMonthValue())
                ),
                new Document("$setOnInsert", apertura),
                new UpdateOptions().upsert(true)
        );
    }

    // ==========================================
    // BUSCAR CUENTA POR CLIENTE
    // ==========================================

    public Document buscarPorCliente(String clienteId) {

        validarId(clienteId, "cliente");

        return TablaConsola.mostrar("CuentaCorrienteService.buscarPorCliente", cuentaDAO.buscarPorCliente(clienteId));
    }

    // ==========================================
    // LISTAR CUENTAS
    // ==========================================

    public List<Document> listarCuentas() {
        return TablaConsola.mostrar("CuentaCorrienteService.listarCuentas", cuentaDAO.listarTodos());
    }

    // ==========================================
    // CONSULTAR SALDO
    // ==========================================

    public BigDecimal consultarSaldo(String clienteId) {

        Document cuenta = buscarPorCliente(clienteId);

        if (cuenta == null) {
            throw new IllegalArgumentException(
                    "El cliente no tiene una cuenta corriente."
            );
        }

        return TablaConsola.mostrar("CuentaCorrienteService.consultarSaldo", obtenerSaldo(cuenta));
    }

    // ==========================================
    // REGISTRAR DÉBITO
    // ==========================================

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

    // ==========================================
    // REGISTRAR CRÉDITO
    // ==========================================

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

    // ==========================================
    // ASEGURAR APERTURA AUTOMÁTICA
    // ==========================================

    private void asegurarApertura(
            ClientSession session,
            String cuentaId,
            BigDecimal saldoActual
    ) {

        ObjectId idCuenta = new ObjectId(cuentaId);

        YearMonth mesActual = YearMonth.now(ZONA);

        Document primeraApertura = aperturas.find(
                session,
                eq("cuenta_corriente_id", idCuenta)
        )
                .sort(
                        new Document("anio", 1)
                                .append("mes", 1)
                )
                .first();

        // ======================================
        // CUENTA SIN NINGUNA APERTURA
        // ======================================

        if (primeraApertura == null) {

            // Si existen movimientos históricos,
            // no inventamos una apertura inicial.
            List<Document> movimientos =
                    movimientoDAO.buscarPorCuenta(cuentaId);

            if (!movimientos.isEmpty()) {
                throw new IllegalStateException(
                        "La cuenta tiene movimientos históricos "
                                + "pero no tiene apertura. "
                                + "Se necesita revisar su historial."
                );
            }

            // Recuperamos una cuenta de prueba
            // sin movimientos y sin apertura.
            // Conservamos su saldo actual.
            insertarApertura(
                    session,
                    cuentaId,
                    mesActual,
                    saldoActual
            );

            return;
        }

        // ======================================
        // VALIDAR CIERRES ANTERIORES
        // ======================================

        YearMonth primerPeriodo = YearMonth.of(
                primeraApertura.getInteger("anio"),
                primeraApertura.getInteger("mes")
        );

        YearMonth periodo = primerPeriodo;

        while (periodo.isBefore(mesActual)) {

            Document cierre = cierreDAO.buscarPorPeriodo(
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

        // ======================================
        // APERTURA DEL MES ACTUAL
        // ======================================

        insertarApertura(
                session,
                cuentaId,
                mesActual,
                saldoActual
        );
    }

    // ==========================================
    // INSERTAR APERTURA SIN DUPLICAR
    // ==========================================

    private void insertarApertura(
            ClientSession session,
            String cuentaId,
            YearMonth periodo,
            BigDecimal saldoInicial
    ) {

        ObjectId idCuenta = new ObjectId(cuentaId);

        Document filtro = new Document(
                "cuenta_corriente_id", idCuenta
        )
                .append("anio", periodo.getYear())
                .append("mes", periodo.getMonthValue());

        Document apertura = new Document(
                "cuenta_corriente_id", idCuenta
        )
                .append("anio", periodo.getYear())
                .append("mes", periodo.getMonthValue())
                .append(
                        "saldo_inicial",
                        new Decimal128(saldoInicial)
                );

        aperturas.updateOne(
                session,
                filtro,
                new Document("$setOnInsert", apertura),
                new UpdateOptions().upsert(true)
        );
    }

    // ==========================================
    // REGISTRAR MOVIMIENTO NORMAL
    // ==========================================

    private void registrarMovimiento(
            String clienteId,
            String tipo,
            String referenciaId,
            String descripcion,
            BigDecimal importe
    ) {

        validarImporte(importe);

        if (referenciaId == null || referenciaId.isBlank()) {
            throw new IllegalArgumentException(
                    "La referencia es obligatoria."
            );
        }

        Document cuenta = obtenerCuenta(clienteId);

        String cuentaId =
                cuenta.getObjectId("_id").toHexString();

        try (ClientSession session =
                     MongoSingleton.getInstance().startSession()) {

            session.startTransaction();

            try {

                Document cuentaActual = cuentaDAO.buscarPorId(
                        session,
                        cuentaId
                );

                if (cuentaActual == null) {
                    throw new IllegalStateException(
                            "No se encontró la cuenta corriente."
                    );
                }

                BigDecimal saldoActual =
                        obtenerSaldo(cuentaActual);

                asegurarApertura(
                        session,
                        cuentaId,
                        saldoActual
                );

                BigDecimal nuevoSaldo;

                if ("DEBITO".equals(tipo)) {

                    if (saldoActual.compareTo(importe) < 0) {
                        throw new IllegalArgumentException(
                                "Saldo insuficiente. Disponible: $"
                                        + saldoActual.toPlainString()
                        );
                    }

                    nuevoSaldo = saldoActual.subtract(importe);

                } else if ("CREDITO".equals(tipo)) {

                    nuevoSaldo = saldoActual.add(importe);

                } else {

                    throw new IllegalArgumentException(
                            "Tipo de movimiento no válido."
                    );
                }

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
                AppLog.info(CuentaCorrienteService.class,
                        "Movimiento de cuenta corriente confirmado.");

            } catch (RuntimeException e) {

                if (session.hasActiveTransaction()) {
                    session.abortTransaction();
                    AppLog.warning(CuentaCorrienteService.class,
                            "Movimiento de cuenta corriente revertido.");
                }

                throw e;
            }
        }
    }

    // ==========================================
    // DESCONTAR PAGO DE FACTURA
    // ==========================================

    public void descontarPagoFactura(
            ClientSession session,
            String clienteId,
            String pagoId,
            BigDecimal importe
    ) {

        if (session == null
                || !session.hasActiveTransaction()) {

            throw new IllegalArgumentException(
                    "El pago necesita una transacción activa."
            );
        }

        validarId(clienteId, "cliente");
        validarId(pagoId, "pago");
        validarImporte(importe);

        Document cuenta = obtenerCuenta(clienteId);

        String cuentaId =
                cuenta.getObjectId("_id").toHexString();

        Document cuentaActual = cuentaDAO.buscarPorId(
                session,
                cuentaId
        );

        if (cuentaActual == null) {
            throw new IllegalArgumentException(
                    "No se encontró la cuenta corriente."
            );
        }

        BigDecimal saldoActual =
                obtenerSaldo(cuentaActual);

        // Abrir automáticamente, si corresponde.
        asegurarApertura(
                session,
                cuentaId,
                saldoActual
        );

        // ======================================
        // VALIDAR SALDO SUFICIENTE
        // ======================================

        if (saldoActual.compareTo(importe) < 0) {

            throw new IllegalArgumentException(
                    "Saldo insuficiente.\n"
                            + "Disponible: $"
                            + saldoActual.toPlainString()
                            + "\nImporte del pago: $"
                            + importe.toPlainString()
            );
        }

        // ======================================
        // REGISTRAR DÉBITO
        // ======================================

        MovimientoCuentaCorriente movimiento =
                new MovimientoCuentaCorriente(
                        null,
                        cuentaId,
                        "DEBITO",
                        pagoId,
                        "Pago de factura",
                        importe,
                        new Date()
                );

        movimientoDAO.agregar(
                session,
                movimiento
        );

        // ======================================
        // DESCONTAR SALDO
        // ======================================

        cuentaDAO.actualizarSaldo(
                session,
                cuentaId,
                saldoActual.subtract(importe)
        );

        // La transacción la confirma PagoFacturaService.
    }

    // ==========================================
    // CONSULTAR HISTORIAL
    // ==========================================

    public List<Document> consultarHistorial(
            String clienteId
    ) {

        Document cuenta = obtenerCuenta(clienteId);

        String cuentaId =
                cuenta.getObjectId("_id").toHexString();

        return TablaConsola.mostrar("CuentaCorrienteService.consultarHistorial", movimientoDAO.buscarPorCuenta(cuentaId));
    }

    // ==========================================
    // BUSCAR CUENTA O LANZAR ERROR
    // ==========================================

    private Document obtenerCuenta(String clienteId) {

        Document cuenta = buscarPorCliente(clienteId);

        if (cuenta == null
                || cuenta.getObjectId("_id") == null) {

            throw new IllegalArgumentException(
                    "El cliente no tiene una cuenta corriente."
            );
        }

        return cuenta;
    }

    // ==========================================
    // OBTENER SALDO
    // ==========================================

    private BigDecimal obtenerSaldo(Document cuenta) {

        Decimal128 valor =
                cuenta.get("saldo", Decimal128.class);

        if (valor == null) {
            throw new IllegalStateException(
                    "La cuenta corriente no tiene un saldo válido."
            );
        }

        return valor.bigDecimalValue();
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    private void validarImporte(BigDecimal importe) {

        if (importe == null
                || importe.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El importe debe ser mayor a cero."
            );
        }
    }

    private void validarId(String id, String campo) {

        if (id == null || !ObjectId.isValid(id)) {
            throw new IllegalArgumentException(
                    "El ID de " + campo + " no es válido."
            );
        }
    }
}
