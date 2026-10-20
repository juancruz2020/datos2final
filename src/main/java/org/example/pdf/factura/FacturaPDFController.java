
package org.example.pdf.factura;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

public class FacturaPDFController {

    private final FacturaPDFService facturaPDFService;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public FacturaPDFController() {
        this.facturaPDFService = new FacturaPDFService();
    }

    // ==========================================
    // GENERAR FACTURA CON VENCIMIENTO
    // ==========================================

    public void generarFactura(
            String clienteId,
            Date fechaEmision,
            Date fechaVencimiento,
            BigDecimal importeTotal,
            String estado
    ) {
        try {
            facturaPDFService.generarFactura(
                    clienteId,
                    fechaEmision,
                    fechaVencimiento,
                    importeTotal,
                    estado
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "No se pudo generar la factura PDF.",
                    e
            );
        }
    }

    // ==========================================
    // COMPATIBILIDAD CON EL METODO ANTERIOR
    // ==========================================

    public void generarFactura(
            String clienteId,
            Date fechaEmision,
            BigDecimal importeTotal,
            String estado
    ) {
        generarFactura(
                clienteId,
                fechaEmision,
                null,
                importeTotal,
                estado
        );
    }
}
