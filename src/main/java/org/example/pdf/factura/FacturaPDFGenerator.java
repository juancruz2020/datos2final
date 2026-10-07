package org.example.pdf.factura;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FacturaPDFGenerator {

    public File generar(
            Document cliente,
            Date fechaEmision,
            BigDecimal importeTotal,
            String estado
    ) throws IOException {

        // =====================================================
        // CARPETA
        // =====================================================

        File carpeta = new File("facturas");

        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        String nombreArchivo =
                "Factura_" + System.currentTimeMillis() + ".pdf";

        File archivo = new File(carpeta, nombreArchivo);

        // =====================================================
        // DATOS DEL CLIENTE
        // =====================================================

        String razonSocial = obtenerString(cliente, "razon_social");
        String cuit = obtenerString(cliente, "cuit");
        String email = obtenerString(cliente, "email");
        String telefono = obtenerString(cliente, "telefono");
        String pais = obtenerString(cliente, "pais");

        Document direccion =
                cliente.get("direccion", Document.class);

        String calle = "";
        String numero = "";
        String ciudad = "";
        String codigoPostal = "";

        if (direccion != null) {
            calle = obtenerString(direccion, "calle");
            numero = obtenerString(direccion, "numero");
            ciudad = obtenerString(direccion, "ciudad");
            codigoPostal = obtenerString(direccion, "codigo_postal");
        }

        SimpleDateFormat formato =
                new SimpleDateFormat("dd/MM/yyyy");

        // =====================================================
        // CREAR PDF
        // =====================================================

        try (PDDocument documento = new PDDocument()) {

            PDPage pagina =
                    new PDPage(PDRectangle.A4);

            documento.addPage(pagina);

            try (PDPageContentStream contenido =
                         new PDPageContentStream(documento, pagina)) {

                // =====================================================
                // TITULO
                // =====================================================

                contenido.beginText();

                contenido.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA_BOLD
                        ),
                        26
                );

                contenido.newLineAtOffset(50, 770);
                contenido.showText("FACTURA");

                contenido.endText();

                // =====================================================
                // NUMERO
                // =====================================================

                contenido.beginText();

                contenido.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA
                        ),
                        10
                );

                contenido.newLineAtOffset(400, 770);

                contenido.showText(
                        "Nro: "
                                + nombreArchivo
                                .replace("Factura_", "")
                                .replace(".pdf", "")
                );

                contenido.endText();

                // =====================================================
                // LINEA PRINCIPAL
                // =====================================================

                contenido.moveTo(50, 750);
                contenido.lineTo(545, 750);
                contenido.stroke();

                // =====================================================
                // FECHA
                // =====================================================

                contenido.beginText();

                contenido.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA
                        ),
                        10
                );

                contenido.newLineAtOffset(50, 725);

                contenido.showText(
                        "Fecha de emision: "
                                + formato.format(fechaEmision)
                );

                contenido.endText();

                // =====================================================
                // CAJA DATOS DEL CLIENTE
                // =====================================================

                /*
                 * La caja comienza en Y=475
                 * y tiene 230 puntos de alto.
                 *
                 * Por lo tanto termina en Y=705.
                 */

                contenido.addRect(
                        50,
                        475,
                        495,
                        230
                );

                contenido.stroke();

                // =====================================================
                // TITULO DEL CLIENTE
                // =====================================================

                contenido.beginText();

                contenido.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA_BOLD
                        ),
                        13
                );

                contenido.newLineAtOffset(
                        65,
                        675
                );

                contenido.showText(
                        "DATOS DEL CLIENTE"
                );

                // =====================================================
                // DATOS
                // =====================================================

                contenido.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA
                        ),
                        10
                );

                // Razon social
                contenido.newLineAtOffset(
                        0,
                        -25
                );

                contenido.showText(
                        "Razon social: " + razonSocial
                );

                // CUIT
                contenido.newLineAtOffset(
                        0,
                        -22
                );

                contenido.showText(
                        "CUIT: " + cuit
                );

                // Email
                contenido.newLineAtOffset(
                        0,
                        -22
                );

                contenido.showText(
                        "Email: " + email
                );

                // Telefono
                contenido.newLineAtOffset(
                        0,
                        -22
                );

                contenido.showText(
                        "Telefono: " + telefono
                );

                // Direccion
                contenido.newLineAtOffset(
                        0,
                        -22
                );

                contenido.showText(
                        "Direccion: "
                                + calle
                                + " "
                                + numero
                );

                // Ciudad
                contenido.newLineAtOffset(
                        0,
                        -22
                );

                contenido.showText(
                        "Ciudad: " + ciudad
                );

                // Codigo postal
                contenido.newLineAtOffset(
                        0,
                        -22
                );

                contenido.showText(
                        "Codigo postal: "
                                + codigoPostal
                );

                // Pais
                contenido.newLineAtOffset(
                        0,
                        -22
                );

                contenido.showText(
                        "Pais: " + pais
                );

                contenido.endText();

                // =====================================================
                // DETALLE
                // =====================================================

                contenido.beginText();

                contenido.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA_BOLD
                        ),
                        13
                );

                contenido.newLineAtOffset(
                        65,
                        440
                );

                contenido.showText(
                        "DETALLE DE FACTURACION"
                );

                contenido.endText();

                // =====================================================
                // CABECERA DETALLE
                // =====================================================

                contenido.addRect(
                        50,
                        380,
                        495,
                        35
                );

                contenido.stroke();

                contenido.beginText();

                contenido.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA_BOLD
                        ),
                        10
                );

                contenido.newLineAtOffset(
                        65,
                        393
                );

                contenido.showText(
                        "CONCEPTO"
                );

                contenido.newLineAtOffset(
                        350,
                        0
                );

                contenido.showText(
                        "IMPORTE"
                );

                contenido.endText();

                // =====================================================
                // DETALLE
                // =====================================================

                contenido.addRect(
                        50,
                        335,
                        495,
                        45
                );

                contenido.stroke();

                contenido.beginText();

                contenido.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA
                        ),
                        10
                );

                contenido.newLineAtOffset(
                        65,
                        353
                );

                contenido.showText(
                        "Servicios logisticos"
                );

                contenido.newLineAtOffset(
                        350,
                        0
                );

                contenido.showText(
                        "$ " + importeTotal
                );

                contenido.endText();

                // =====================================================
                // ESTADO
                // =====================================================

                contenido.addRect(
                        50,
                        255,
                        220,
                        55
                );

                contenido.stroke();

                contenido.beginText();

                contenido.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA_BOLD
                        ),
                        11
                );

                contenido.newLineAtOffset(
                        65,
                        285
                );

                contenido.showText(
                        "ESTADO"
                );

                contenido.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA
                        ),
                        10
                );

                contenido.newLineAtOffset(
                        0,
                        -18
                );

                contenido.showText(
                        estado
                );

                contenido.endText();

                // =====================================================
                // TOTAL
                // =====================================================

                contenido.addRect(
                        300,
                        255,
                        245,
                        55
                );

                contenido.stroke();

                contenido.beginText();

                contenido.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA_BOLD
                        ),
                        13
                );

                contenido.newLineAtOffset(
                        320,
                        285
                );

                contenido.showText(
                        "TOTAL"
                );

                contenido.newLineAtOffset(
                        100,
                        0
                );

                contenido.showText(
                        "$ " + importeTotal
                );

                contenido.endText();

                // =====================================================
                // PIE
                // =====================================================

                contenido.moveTo(
                        50,
                        100
                );

                contenido.lineTo(
                        545,
                        100
                );

                contenido.stroke();

                contenido.beginText();

                contenido.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA
                        ),
                        8
                );

                contenido.newLineAtOffset(
                        50,
                        80
                );

                contenido.showText(
                        "Comprobante generado automaticamente por Logistica Inteligente"
                );

                contenido.newLineAtOffset(
                        0,
                        -15
                );

                contenido.showText(
                        "Documento generado digitalmente."
                );

                contenido.endText();
            }

            // =====================================================
            // GUARDAR
            // =====================================================

            documento.save(archivo);
        }

        return archivo;
    }

    // =====================================================
    // OBTENER STRING DE FORMA SEGURA
    // =====================================================

    private String obtenerString(
            Document documento,
            String campo
    ) {

        String valor =
                documento.getString(campo);

        return valor != null
                ? valor
                : "";
    }
}