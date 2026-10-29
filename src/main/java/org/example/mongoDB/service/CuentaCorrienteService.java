
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
    private final PermisosService permisosService;
    private final MongoCollection<Document> aperturas;

    public CuentaCorrienteService() {

        cuentaDAO = new CuentaCorrienteMongoDAO();
        movimientoDAO = new MovimientoCuentaCorrienteMongoDAO();
        cierreDAO = new CierreMensualCuentaCorrienteMongoDAO();
        permisosService = new PermisosService();

        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        aperturas = database.getCollection(
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
    // CREAR CUENTAS
    // ==========================================

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

    public CuentaCorriente crearCuentaProveedor(
            ClientSession session,
            String proveedorId
    ) {

        validarId(proveedorId, "proveedor");

        if (session == null || !session.hasActiveTransaction()) {
            throw new IllegalArgumentException(
                    "La creación de cuenta del proveedor necesita una transacción activa."
            );
        }

        if (cuentaDAO.buscarPorProveedor(proveedorId) != null) {
            throw new IllegalArgumentException(
                    "Este proveedor ya tiene una cuenta corriente."
            );
        }

        CuentaCorriente cuenta = new CuentaCorriente(
                null,
                null,
                proveedorId,
                BigDecimal.ZERO,
                new Date()
        );

        crearCuenta(session, cuenta);
        return cuenta;
    }

    private void crearCuenta(
            ClientSession session,
            CuentaCorriente cuenta
    ) {

        cuentaDAO.agregar(session, cuenta);
        registrarApertura(session, cuenta.getId(), BigDecimal.ZERO);
    }

    // ==========================================
    // APERTURA INICIAL
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
                .append("saldo_inicial", new Decimal128(saldoInicial));

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
    // CONSULTAR CUENTAS
    // ==========================================

    public Document buscarPorCliente(String clienteId) {

        validarId(clienteId, "cliente");

        return TablaConsola.mostrar(
                "CuentaCorrienteService.buscarPorCliente",
                cuentaDAO.buscarPorCliente(clienteId)
        );
    }

    public Document buscarPorProveedor(String proveedorId) {

        validarId(proveedorId, "proveedor");

        return TablaConsola.mostrar(
                "CuentaCorrienteService.buscarPorProveedor",
                cuentaDAO.buscarPorProveedor(proveedorId)
        );
    }

    public List<Document> listarCuentas() {

        return TablaConsola.mostrar(
                "CuentaCorrienteService.listarCuentas",
                cuentaDAO.listarTodos()
        );
    }

    public List<Document> listarCuentasParaUsuario(
            String usuarioId
    ) {

        if (!permisosService.puedeAccederVista(
                usuarioId,
                "CUENTAS_CORRIENTES"
        )) {
            throw new SecurityException(
                    "No tenés permisos para consultar cuentas corrientes."
            );
        }

        if (permisosService.esProveedor(usuarioId)) {

            String proveedorId =
                    permisosService.obtenerProveedorId(usuarioId);

            Document cuenta =
                    cuentaDAO.buscarPorProveedor(proveedorId);

            return cuenta == null ? List.of() : List.of(cuenta);
        }

        permisosService.exigirAdminUOperador(usuarioId);

        return listarCuentas();
    }

    // ==========================================
    // DÉBITOS Y CRÉDITOS DE CLIENTES
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

        if (primeraApertura == null) {

            List<Document> movimientos =
                    movimientoDAO.buscarPorCuenta(cuentaId);

            if (!movimientos.isEmpty()) {
                throw new IllegalStateException(
                        "La cuenta tiene movimientos históricos pero "
                                + "no tiene apertura. Se necesita revisar su historial."
                );
            }

            insertarApertura(
                    session,
                    cuentaId,
                    mesActual,
                    saldoActual
            );

            return;
        }

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
    // MOVIMIENTOS DE CLIENTES
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

                AppLog.info(
                        CuentaCorrienteService.class,
                        "Movimiento de cuenta corriente confirmado."
                );

            } catch (RuntimeException e) {

                if (session.hasActiveTransaction()) {
                    session.abortTransaction();

                    AppLog.warning(
                            CuentaCorrienteService.class,
                            "Movimiento de cuenta corriente revertido."
                    );
                }

                throw e;
            }
        }
    }

    // ==========================================
    // DESCONTAR PAGO DE FACTURA
    // SE CONSERVA LA LÓGICA ORIGINAL
    // ==========================================

    public void descontarPagoFactura(
            ClientSession session,
            String clienteId,
            String pagoId,
            BigDecimal importe
    ) {

        if (session == null || !session.hasActiveTransaction()) {
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

        BigDecimal saldoActual = obtenerSaldo(cuentaActual);

        asegurarApertura(
                session,
                cuentaId,
                saldoActual
        );

        if (saldoActual.compareTo(importe) < 0) {
            throw new IllegalArgumentException(
                    "Saldo insuficiente.\n"
                            + "Disponible: $"
                            + saldoActual.toPlainString()
                            + "\nImporte del pago: $"
                            + importe.toPlainString()
            );
        }

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

        movimientoDAO.agregar(session, movimiento);

        cuentaDAO.actualizarSaldo(
                session,
                cuentaId,
                saldoActual.subtract(importe)
        );

        // La transacción la confirma PagoFacturaService.
    }

    // ==========================================
    // CONSULTAR HISTORIAL DE CLIENTE
    // ==========================================

    public List<Document> consultarHistorial(
            String clienteId
    ) {

        Document cuenta = obtenerCuenta(clienteId);

        String cuentaId =
                cuenta.getObjectId("_id").toHexString();

        return TablaConsola.mostrar(
                "CuentaCorrienteService.consultarHistorial",
                movimientoDAO.buscarPorCuenta(cuentaId)
        );
    }

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

    // ==========================================
    // ELIMINAR APERTURAS
    // ==========================================

    public void eliminarAperturas(
            ClientSession session,
            String cuentaId
    ) {

        aperturas.deleteMany(
                session,
                eq("cuenta_corriente_id", new ObjectId(cuentaId))
        );
    }


        public void validarPeriodoCuenta(
                ClientSession session,
                String cuentaId
        ) {

        validarId(cuentaId, "cuenta corriente");

        if (session == null || !session.hasActiveTransaction()) {
                throw new IllegalArgumentException(
                        "Se necesita una transacción activa."
                );
        }

        Document cuenta = cuentaDAO.buscarPorId(
                session,
                cuentaId
        );

        if (cuenta == null) {
                throw new IllegalArgumentException(
                        "No se encontró la cuenta corriente."
                );
        }

        asegurarApertura(
                session,
                cuentaId,
                obtenerSaldo(cuenta)
        );
        }

}
