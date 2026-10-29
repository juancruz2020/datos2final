
package org.example.mongoDB.service;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.dao.CuentaCorrienteMongoDAO;
import org.example.mongoDB.dao.PagoProveedorMongoDAO;
import org.example.mongoDB.model.MovimientoCuentaCorriente;
import org.example.mongoDB.model.PagoProveedor;
import org.example.mongoDB.dao.MovimientoCuentaCorrienteMongoDAO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class PagoProveedorService {

    private final PagoProveedorMongoDAO pagoDAO;
    private final CuentaCorrienteMongoDAO cuentaDAO;
    private final MovimientoCuentaCorrienteMongoDAO movimientoDAO;
    private final PermisosService permisosService;

    private final CuentaCorrienteService cuentaService =
            new CuentaCorrienteService();

    private final MongoCollection<Document> facturas;
    private final MongoCollection<Document> pagos;

    // =========================================
    // CONSTRUCTOR
    // =========================================

    public PagoProveedorService() {

        pagoDAO = new PagoProveedorMongoDAO();
        cuentaDAO = new CuentaCorrienteMongoDAO();
        movimientoDAO = new MovimientoCuentaCorrienteMongoDAO();
        permisosService = new PermisosService();

        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        facturas = database.getCollection("facturas_proveedores");
        pagos = database.getCollection("pagos_proveedores");
    }

    // =========================================
    // REGISTRAR PAGO A PROVEEDOR
    // =========================================

    public PagoProveedor registrarPago(
            String usuarioId,
            String facturaProveedorId,
            BigDecimal importe,
            String metodoPago
    ) {

        permisosService.exigirAdmin(usuarioId);

        if (facturaProveedorId == null
                || !ObjectId.isValid(facturaProveedorId)) {

            throw new IllegalArgumentException(
                    "El ID de la factura no es válido."
            );
        }

        if (importe == null
                || importe.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El importe debe ser mayor que cero."
            );
        }

        if (metodoPago == null || metodoPago.isBlank()) {
            throw new IllegalArgumentException(
                    "El método de pago es obligatorio."
            );
        }

        ObjectId facturaId = new ObjectId(facturaProveedorId);

        try (ClientSession session =
                     MongoSingleton.getInstance().startSession()) {

            session.startTransaction();

            try {

                // =====================================
                // 1. BUSCAR FACTURA
                // =====================================

                Document factura = facturas.find(
                        session,
                        eq("_id", facturaId)
                ).first();

                if (factura == null) {
                    throw new IllegalArgumentException(
                            "No se encontró la factura del proveedor."
                    );
                }

                if ("PAGADA".equals(factura.getString("estado"))) {
                    throw new IllegalStateException(
                            "Esta factura ya está pagada."
                    );
                }

                ObjectId proveedorId =
                        factura.getObjectId("proveedor_id");

                if (proveedorId == null) {
                    throw new IllegalStateException(
                            "La factura no tiene un proveedor asociado."
                    );
                }

                Decimal128 totalDecimal =
                        factura.get("importe_total", Decimal128.class);

                if (totalDecimal == null) {
                    throw new IllegalStateException(
                            "La factura no tiene un importe válido."
                    );
                }

                BigDecimal totalFactura =
                        totalDecimal.bigDecimalValue();

                // =====================================
                // 2. CALCULAR PAGOS ANTERIORES
                // =====================================

                BigDecimal totalPagado = BigDecimal.ZERO;

                for (Document pago : pagos.find(
                        session,
                        eq("factura_proveedor_id", facturaId)
                )) {

                    Decimal128 valor =
                            pago.get("importe", Decimal128.class);

                    if (valor == null) {
                        throw new IllegalStateException(
                                "Existe un pago con importe inválido."
                        );
                    }

                    totalPagado = totalPagado.add(
                            valor.bigDecimalValue()
                    );
                }

                BigDecimal pendiente =
                        totalFactura.subtract(totalPagado);

                if (pendiente.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalStateException(
                            "La factura no tiene saldo pendiente."
                    );
                }
                System.out.println("IMPORTE DEL PAGO: " + importe.toPlainString());
                System.out.println("PENDIENTE FACTURA: " + pendiente.toPlainString());

                if (importe.compareTo(pendiente) > 0) {
                    throw new IllegalArgumentException(
                            "El pago supera el importe pendiente: $"
                                    + pendiente.toPlainString()
                    );
                }

                // =====================================
                // 3. BUSCAR CUENTA DEL PROVEEDOR
                // =====================================

                Document cuenta = cuentaDAO.buscarPorProveedor(
                        proveedorId.toHexString()
                );

                if (cuenta == null
                        || cuenta.getObjectId("_id") == null) {

                    throw new IllegalStateException(
                            "El proveedor no tiene una cuenta corriente."
                    );
                }

                String cuentaId =
                        cuenta.getObjectId("_id").toHexString();

                Document cuentaActual = cuentaDAO.buscarPorId(
                        session,
                        cuentaId
                );

                if (cuentaActual == null
                        || !proveedorId.equals(
                                cuentaActual.getObjectId("proveedor_id")
                        )) {

                    throw new IllegalStateException(
                            "La cuenta no corresponde al proveedor."
                    );
                }

                Decimal128 saldoDecimal =
                        cuentaActual.get("saldo", Decimal128.class);

                if (saldoDecimal == null) {
                    throw new IllegalStateException(
                            "La cuenta corriente no tiene saldo válido."
                    );
                }

                BigDecimal saldoActual =
                        saldoDecimal.bigDecimalValue();

                // El saldo negativo representa la deuda.
                // No permitimos pagar más de lo que debemos.

                if (saldoActual.compareTo(BigDecimal.ZERO) >= 0
                        || saldoActual.add(importe)
                        .compareTo(BigDecimal.ZERO) > 0) {

                    throw new IllegalArgumentException(
                            "El pago supera la deuda de la cuenta corriente."
                    );
                }

                // =====================================
                // 4. VALIDAR PERÍODO CONTABLE
                // =====================================

                cuentaService.validarPeriodoCuenta(
                        session,
                        cuentaId
                );

                // =====================================
                // 5. REGISTRAR PAGO
                // =====================================

                PagoProveedor nuevoPago = new PagoProveedor(
                        null,
                        facturaProveedorId,
                        new Date(),
                        importe,
                        metodoPago.trim()
                );

                pagoDAO.agregar(session, nuevoPago);

                // =====================================
                // 6. REGISTRAR MOVIMIENTO DE CUENTA
                // =====================================

                MovimientoCuentaCorriente movimiento =
                        new MovimientoCuentaCorriente(
                                null,
                                cuentaId,
                                "CREDITO",
                                nuevoPago.getId(),
                                "Pago de factura de proveedor "
                                        + factura.getString("numero_factura"),
                                importe,
                                new Date()
                        );

                movimientoDAO.agregar(session, movimiento);

                // =====================================
                // 7. ACTUALIZAR SALDO
                // =====================================

                BigDecimal nuevoSaldo =
                        saldoActual.add(importe);

                cuentaDAO.actualizarSaldo(
                        session,
                        cuentaId,
                        nuevoSaldo
                );

                // =====================================
                // 8. ACTUALIZAR ESTADO DE FACTURA
                // =====================================

                BigDecimal nuevoTotalPagado =
                        totalPagado.add(importe);

                String nuevoEstado =
                        nuevoTotalPagado.compareTo(totalFactura) == 0
                                ? "PAGADA"
                                : "PARCIALMENTE_PAGADA";

                facturas.updateOne(
                        session,
                        eq("_id", facturaId),
                        new Document(
                                "$set",
                                new Document("estado", nuevoEstado)
                        )
                );

                // =====================================
                // 9. CONFIRMAR TRANSACCIÓN
                // =====================================

                session.commitTransaction();

                return nuevoPago;

            } catch (RuntimeException e) {

                if (session.hasActiveTransaction()) {
                    session.abortTransaction();
                }

                throw e;
            }
        }
    }

    // =========================================
    // CONSULTAR PAGOS DE UNA FACTURA
    // =========================================

    public List<Document> listarPorFactura(
            String usuarioId,
            String facturaProveedorId
    ) {

        permisosService.exigirAdminUOperador(usuarioId);

        if (facturaProveedorId == null
                || !ObjectId.isValid(facturaProveedorId)) {

            throw new IllegalArgumentException(
                    "El ID de la factura no es válido."
            );
        }

        return pagoDAO.buscarPorFactura(facturaProveedorId);
    }

    // =========================================
    // LISTAR TODOS LOS PAGOS
    // =========================================

    public List<Document> listarPagos(String usuarioId) {

        permisosService.exigirAdminUOperador(usuarioId);

        return pagoDAO.listarTodos();
    }
}
