
package org.example.pdf.factura;

import org.bson.Document;
import org.example.mongoDB.controller.ControllerMongoDB;
import org.example.pdf.PDFOpener;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

public class FacturaPDFService {

    private final FacturaPDFGenerator generator;
    private final PDFOpener opener;
    private final ControllerMongoDB mongoController;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public FacturaPDFService() {
        this.generator = new FacturaPDFGenerator();
        this.opener = new PDFOpener();
        this.mongoController = new ControllerMongoDB();
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
    ) throws IOException {

        // Buscar los datos del cliente en MongoDB
        Document cliente =
                mongoController.buscarClientePorId(clienteId);

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "No se encontró el cliente con ID: " + clienteId
            );
        }

        // Generar el PDF con fecha de vencimiento
        File archivo = generator.generar(
                cliente,
                fechaEmision,
                fechaVencimiento,
                importeTotal,
                estado
        );

        // Abrir el PDF generado
        opener.abrir(archivo);
    }


}
