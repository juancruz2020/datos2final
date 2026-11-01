
package org.example.mongoDB.service;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import org.example.conecciones.MongoSingleton;
import org.example.logging.AppLog;
import org.example.mongoDB.dao.PagoMongoDAO;
import org.example.mongoDB.model.Pago;

import java.math.BigDecimal;
import java.util.Date;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class PagoFacturaService {

    private final PagoMongoDAO pagoDAO;
    private final CuentaCorrienteService cuentaService;

    private final MongoCollection<Document> facturas;
    private final MongoCollection<Document> pagos;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public PagoFacturaService() {

        this.pagoDAO = new PagoMongoDAO();
        this.cuentaService = new CuentaCorrienteService();

        MongoDatabase db = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.facturas = db.getCollection("facturas");
        this.pagos = db.getCollection("pagos");
    }

    // ==========================================
    // CONSULTAR SALDO PENDIENTE
    // ==========================================

    public BigDecimal obtenerSaldoPendiente(String facturaId) {

        validarFacturaId(facturaId);

        ObjectId idFactura = new ObjectId(facturaId);

        Document factura = facturas.find(
                eq("_id", idFactura)
        ).first();

        if (factura == null) {
            throw new IllegalArgumentException(
                    "La factura no existe."
            );
        }

        BigDecimal total = importeDecimal(
                factura.get("importe_total")
        );

        BigDecimal pagado = BigDecimal.ZERO;

        for (Document pago : pagos.find(
                eq("factura_id", idFactura)
        )) {

            BigDecimal monto = importeDecimal(
                    pago.get("monto")
            );

            pagado = pagado.add(monto);
        }

        BigDecimal pendiente = total.subtract(pagado);

        if (pendiente.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException(
                    "Los pagos registrados superan "
                            + "el importe total de la factura."
            );
        }

        return pendiente;
    }

    // ==========================================
    // REGISTRAR PAGO DEL SALDO PENDIENTE
    // ==========================================

    public void registrarPago(
            String facturaId,
            BigDecimal importe,
            String medioPago
    ) {

        validarFacturaId(facturaId);

        if (importe == null
                || importe.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El importe del pago debe ser mayor a cero."
            );
        }

        // Único medio permitido para pagos nuevos.

        if (!"TRANSFERENCIA".equalsIgnoreCase(medioPago)) {

            throw new IllegalArgumentException(
                    "El único medio de pago permitido "
                            + "es TRANSFERENCIA."
            );
        }

        ObjectId idFactura = new ObjectId(facturaId);

        try (ClientSession session = MongoSingleton
                .getInstance()
                .startSession()) {

            session.startTransaction();

            try {

                // ==================================
                // BUSCAR FACTURA
                // ==================================

                Document factura = facturas.find(
                        session,
                        eq("_id", idFactura)
                ).first();

                if (factura == null) {
                    throw new IllegalArgumentException(
                            "La factura no existe."
                    );
                }

                if ("PAGADA".equalsIgnoreCase(
                        factura.getString("estado"))) {

                    throw new IllegalArgumentException(
                            "Esta factura ya está pagada."
                    );
                }

                ObjectId clienteId =
                        factura.getObjectId("cliente_id");

                if (clienteId == null) {
                    throw new IllegalStateException(
                            "La factura no tiene un cliente asociado."
                    );
                }

                // ==================================
                // IMPORTE TOTAL DE LA FACTURA
                // ==================================

                BigDecimal total = importeDecimal(
                        factura.get("importe_total")
                );

                // ==================================
                // SUMAR LOS PAGOS EXISTENTES
                // ==================================

                BigDecimal totalPagado = BigDecimal.ZERO;

                for (Document pagoAnterior : pagos.find(
                        session,
                        eq("factura_id", idFactura)
                )) {

                    BigDecimal montoAnterior = importeDecimal(
                            pagoAnterior.get("monto")
                    );

                    totalPagado = totalPagado.add(montoAnterior);
                }

                // ==================================
                // CALCULAR SALDO PENDIENTE
                // ==================================

                BigDecimal saldoPendiente =
                        total.subtract(totalPagado);

                if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {

                    throw new IllegalStateException(
                            "Los pagos registrados superan "
                                    + "el importe total de la factura."
                    );
                }

                if (saldoPendiente.compareTo(BigDecimal.ZERO) == 0) {

                    throw new IllegalStateException(
                            "Esta factura ya tiene registrado "
                                    + "el pago de su importe total."
                    );
                }

                // ==================================
                // EXIGIR EL SALDO COMPLETO
                // ==================================

                // Mantenemos la regla de pago completo,
                // pero ahora sobre el saldo pendiente,
                // no sobre el total original.

                if (importe.compareTo(saldoPendiente) != 0) {

                    throw new IllegalArgumentException(
                            "Debés pagar el saldo pendiente "
                                    + "de la factura.\n"
                                    + "Importe obligatorio: $"
                                    + saldoPendiente.toPlainString()
                    );
                }

                // ==================================
                // CREAR PAGO
                // ==================================

                Pago pago = new Pago(
                        null,
                        facturaId,
                        new Date(),
                        saldoPendiente,
                        "TRANSFERENCIA"
                );

                // ==================================
                // GUARDAR PAGO
                // ==================================

                pagoDAO.agregar(session, pago);

                // ==================================
                // DESCONTAR CUENTA CORRIENTE
                // ==================================

                // Solo se descuenta lo que faltaba pagar.
                // No se vuelven a descontar los pagos
                // que ya estaban registrados.

                cuentaService.descontarPagoFactura(
                        session,
                        clienteId.toHexString(),
                        pago.getId(),
                        saldoPendiente
                );

                // ==================================
                // MARCAR FACTURA COMO PAGADA
                // ==================================

                facturas.updateOne(
                        session,
                        eq("_id", idFactura),
                        set("estado", "PAGADA")
                );

                // ==================================
                // CONFIRMAR TRANSACCIÓN
                // ==================================

                session.commitTransaction();

                AppLog.info(
                        PagoFacturaService.class,
                        "Pago de saldo pendiente confirmado."
                );

            } catch (RuntimeException e) {

                if (session.hasActiveTransaction()) {
                    session.abortTransaction();

                    AppLog.warning(
                            PagoFacturaService.class,
                            "Pago de factura revertido."
                    );
                }

                throw e;
            }
        }
    }

    // ==========================================
    // VALIDAR ID
    // ==========================================

    private void validarFacturaId(String facturaId) {

        if (facturaId == null
                || !ObjectId.isValid(facturaId)) {

            throw new IllegalArgumentException(
                    "La factura seleccionada no es válida."
            );
        }
    }

    // ==========================================
    // CONVERTIR IMPORTE DE MONGODB
    // ==========================================

    private BigDecimal importeDecimal(Object valor) {

        if (valor instanceof Decimal128 decimal) {
            return decimal.bigDecimalValue();
        }

        if (valor instanceof BigDecimal decimal) {
            return decimal;
        }

        if (valor instanceof Number numero) {
            return new BigDecimal(numero.toString());
        }

        throw new IllegalStateException(
                "La factura o el pago tienen un importe inválido."
        );
    }
}
