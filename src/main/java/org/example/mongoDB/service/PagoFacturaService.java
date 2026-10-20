
package org.example.mongoDB.service;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import org.example.conecciones.MongoSingleton;
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
    // REGISTRAR PAGO COMPLETO
    // ==========================================

    public void registrarPago(
            String facturaId,
            BigDecimal importe,
            String medioPago
    ) {

        if (facturaId == null
                || !ObjectId.isValid(facturaId)) {

            throw new IllegalArgumentException(
                    "La factura seleccionada no es válida."
            );
        }

        if (importe == null
                || importe.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El importe del pago debe ser mayor a cero."
            );
        }

        if (medioPago == null || medioPago.isBlank()) {

            throw new IllegalArgumentException(
                    "Seleccioná un medio de pago."
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
                // OBTENER IMPORTE TOTAL
                // ==================================

                BigDecimal total = importeDecimal(
                        factura.get("importe_total")
                );

                // ==================================
                // NO PERMITIR IMPORTES DIFERENTES
                // ==================================

                if (importe.compareTo(total) != 0) {

                    throw new IllegalArgumentException(
                            "La factura debe pagarse completa.\n"
                                    + "Importe obligatorio: $"
                                    + total.toPlainString()
                    );
                }

                // ==================================
                // CONTROLAR PAGOS ANTERIORES
                // ==================================

                // No permitimos volver a cobrar una
                // factura que ya tenga pagos registrados,
                // aunque todavía figure PENDIENTE.

                if (pagos.find(
                        session,
                        eq("factura_id", idFactura)
                ).first() != null) {

                    throw new IllegalArgumentException(
                            "Esta factura tiene pagos registrados anteriormente.\n"
                                    + "Revisá su historial antes de cobrarla "
                                    + "para evitar descuentos duplicados."
                    );
                }

                // ==================================
                // CREAR PAGO
                // ==================================

                Pago pago = new Pago(
                        null,
                        facturaId,
                        new Date(),
                        total,
                        medioPago.trim()
                );

                // ==================================
                // GUARDAR PAGO EN TRANSACCIÓN
                // ==================================

                pagoDAO.agregar(session, pago);

                // ==================================
                // DESCONTAR CUENTA CORRIENTE
                // ==================================

                // Este método controla:
                // - Existencia de la cuenta
                // - Cierres mensuales
                // - Saldo disponible
                //
                // Si falla, se cancela también
                // la inserción del pago.

                cuentaService.descontarPagoFactura(
                        session,
                        clienteId.toHexString(),
                        pago.getId(),
                        total
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
                // CONFIRMAR TODOS LOS CAMBIOS
                // ==================================

                session.commitTransaction();

            } catch (RuntimeException e) {

                if (session.hasActiveTransaction()) {
                    session.abortTransaction();
                }

                throw e;
            }
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
                "La factura tiene un importe inválido."
        );
    }
}
