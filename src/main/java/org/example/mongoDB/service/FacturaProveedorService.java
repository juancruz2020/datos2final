
package org.example.mongoDB.service;

import com.mongodb.client.ClientSession;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.dao.CuentaCorrienteMongoDAO;
import org.example.mongoDB.dao.FacturaProveedorMongoDAO;
import org.example.mongoDB.dao.MovimientoCuentaCorrienteMongoDAO;
import org.example.mongoDB.dao.ProveedorMongoDAO;
import org.example.mongoDB.model.FacturaProveedor;
import org.example.mongoDB.model.MovimientoCuentaCorriente;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class FacturaProveedorService {

    private final FacturaProveedorMongoDAO facturaDAO;
    private final ProveedorMongoDAO proveedorDAO;
    private final CuentaCorrienteMongoDAO cuentaDAO;
    private final MovimientoCuentaCorrienteMongoDAO movimientoDAO;
    private final PermisosService permisosService;

    private final CuentaCorrienteService cuentaService =
            new CuentaCorrienteService();

    // =========================================
    // CONSTRUCTOR
    // =========================================

    public FacturaProveedorService() {

        facturaDAO = new FacturaProveedorMongoDAO();
        proveedorDAO = new ProveedorMongoDAO();
        cuentaDAO = new CuentaCorrienteMongoDAO();
        movimientoDAO = new MovimientoCuentaCorrienteMongoDAO();
        permisosService = new PermisosService();
    }

    // =========================================
    // REGISTRAR FACTURA DEL PROVEEDOR
    // =========================================

    public FacturaProveedor registrarFactura(
            String usuarioId,
            String proveedorId,
            String concepto,
            Date fechaEmision,
            Date fechaVencimiento,
            BigDecimal importe
    ) {

        permisosService.exigirAdmin(usuarioId);

        if (proveedorId == null || !ObjectId.isValid(proveedorId)) {
            throw new IllegalArgumentException(
                    "El ID del proveedor no es válido."
            );
        }

        if (concepto == null || concepto.isBlank()) {
            throw new IllegalArgumentException(
                    "El concepto es obligatorio."
            );
        }

        if (fechaEmision == null || fechaVencimiento == null) {
            throw new IllegalArgumentException(
                    "Las fechas son obligatorias."
            );
        }

        if (fechaVencimiento.before(fechaEmision)) {
            throw new IllegalArgumentException(
                    "El vencimiento no puede ser anterior a la emisión."
            );
        }

        if (importe == null || importe.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El importe debe ser mayor que cero."
            );
        }

        Document proveedor = proveedorDAO.buscarPorId(proveedorId);

        if (proveedor == null) {
            throw new IllegalArgumentException(
                    "El proveedor no existe."
            );
        }

        // NUEVA VALIDACIÓN:
        // Solo se pueden registrar facturas de
        // proveedores que estén activos.

        if (!"ACTIVO".equalsIgnoreCase(
                proveedor.getString("estado")
        )) {
            throw new IllegalStateException(
                    "No se pueden registrar facturas de proveedores inactivos."
            );
        }

        Document cuenta = cuentaDAO.buscarPorProveedor(proveedorId);

        if (cuenta == null || cuenta.getObjectId("_id") == null) {
            throw new IllegalStateException(
                    "El proveedor no tiene una cuenta corriente."
            );
        }

        String cuentaId = cuenta.getObjectId("_id").toHexString();

        // =====================================
        // TRANSACCIÓN
        // =====================================

        try (ClientSession session =
                     MongoSingleton.getInstance().startSession()) {

            session.startTransaction();

            try {

                // 1. Verificar la cuenta del proveedor.

                Document cuentaActual = cuentaDAO.buscarPorId(
                        session,
                        cuentaId
                );

                if (cuentaActual == null
                        || !new ObjectId(proveedorId).equals(
                                cuentaActual.getObjectId("proveedor_id")
                        )) {

                    throw new IllegalStateException(
                            "No se encontró la cuenta del proveedor."
                    );
                }

                Decimal128 saldoDecimal =
                        cuentaActual.get("saldo", Decimal128.class);

                if (saldoDecimal == null) {
                    throw new IllegalStateException(
                            "La cuenta corriente no tiene un saldo válido."
                    );
                }

                BigDecimal saldoActual =
                        saldoDecimal.bigDecimalValue();

                // 2. Validar el período contable.

                cuentaService.validarPeriodoCuenta(
                        session,
                        cuentaId
                );

                // 3. Generar el número automáticamente.

                String numeroGenerado =
                        facturaDAO.generarNumeroFactura(session);

                // 4. Crear la factura con su número.

                FacturaProveedor factura = new FacturaProveedor(
                        null,
                        proveedorId,
                        numeroGenerado,
                        concepto.trim(),
                        fechaEmision,
                        fechaVencimiento,
                        importe,
                        "PENDIENTE"
                );

                // 5. Guardar la factura.

                facturaDAO.agregar(session, factura);

                // 6. Registrar el movimiento.
                // DEBITO aumenta la deuda del proveedor.

                MovimientoCuentaCorriente movimiento =
                        new MovimientoCuentaCorriente(
                                null,
                                cuentaId,
                                "DEBITO",
                                factura.getId(),
                                "Factura proveedor "
                                        + factura.getNumeroFactura()
                                        + ": "
                                        + factura.getConcepto(),
                                importe,
                                new Date()
                        );

                movimientoDAO.agregar(session, movimiento);

                // 7. Actualizar el saldo negativo.

                BigDecimal nuevoSaldo =
                        saldoActual.subtract(importe);

                cuentaDAO.actualizarSaldo(
                        session,
                        cuentaId,
                        nuevoSaldo
                );

                // 8. Confirmar todas las operaciones.

                session.commitTransaction();

                return factura;

            } catch (RuntimeException e) {

                if (session.hasActiveTransaction()) {
                    session.abortTransaction();
                }

                throw e;
            }
        }
    }

    // =========================================
    // CONSULTAR FACTURAS AUTORIZADAS
    // =========================================

    public List<Document> listarFacturas(String usuarioId) {

        if (!permisosService.puedeAccederVista(
                usuarioId,
                "PAGOS"
        )) {
            throw new SecurityException(
                    "No tenés permisos para consultar pagos."
            );
        }

        permisosService.exigirAdminUOperador(usuarioId);

        return facturaDAO.listarTodos();
    }

    // =========================================
    // CONSULTAR FACTURAS DE UN PROVEEDOR
    // =========================================

    public List<Document> listarPorProveedor(
            String usuarioId,
            String proveedorId
    ) {

        if (proveedorId == null || !ObjectId.isValid(proveedorId)) {
            throw new IllegalArgumentException(
                    "El ID del proveedor no es válido."
            );
        }

        if (permisosService.esProveedor(usuarioId)) {

            String proveedorAsociado =
                    permisosService.obtenerProveedorId(usuarioId);

            if (!proveedorAsociado.equals(proveedorId)) {
                throw new SecurityException(
                        "No tenés permisos para consultar este proveedor."
                );
            }

        } else {

            permisosService.exigirAdminUOperador(usuarioId);
        }

        return facturaDAO.buscarPorProveedor(proveedorId);
    }
}
