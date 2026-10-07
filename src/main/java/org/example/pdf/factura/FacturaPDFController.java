package org.example.pdf.factura;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

public class FacturaPDFController {

    private final FacturaPDFService facturaPDFService;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public FacturaPDFController() {
        this.facturaPDFService = new FacturaPDFService();
    }

    // =====================================================
    // GENERAR FACTURA PDF
    // =====================================================

    public void generarFactura(
            String clienteId,
            Date fechaEmision,
            BigDecimal importeTotal,
            String estado
    ) {

        try {

            facturaPDFService.generarFactura(
                    clienteId,
                    fechaEmision,
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
}