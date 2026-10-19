package org.example.pdf.reporte;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportePDFGenerator {

    private final PDType1Font normal = new PDType1Font(
            Standard14Fonts.FontName.HELVETICA
    );

    private final PDType1Font negrita = new PDType1Font(
            Standard14Fonts.FontName.HELVETICA_BOLD
    );

    // =========================================================
    // REPORTE POR CLIENTE
    // =========================================================

    public File generarEnviosPorCliente(
            String nombreCliente,
            List<Document> envios
    ) throws IOException {

        return generarReporte(
                "REPORTE DE ENVIOS POR CLIENTE",
                "Cliente: " + nombreCliente,
                null,
                "Reporte_Envios_Cliente_",
                envios
        );
    }

    // =========================================================
    // REPORTE POR ESTADO
    // =========================================================

    public File generarEnviosPorEstado(
            String estado,
            String nombreCliente,
            List<Document> envios
    ) throws IOException {

        String detalleCliente = null;

        if (nombreCliente != null && !nombreCliente.isBlank()) {
            detalleCliente = "Cliente: " + nombreCliente;
        }

        return generarReporte(
                "REPORTE DE ENVIOS POR ESTADO",
                "Estado: " + estado,
                detalleCliente,
                "Reporte_Envios_Estado_",
                envios
        );
    }

    // =========================================================
    // REPORTE POR PAIS
    // =========================================================

    public File generarEnviosPorPais(
            String pais,
            String nombreCliente,
            List<Document> envios
    ) throws IOException {

        String detalleCliente = null;

        if (nombreCliente != null && !nombreCliente.isBlank()) {
            detalleCliente = "Cliente: " + nombreCliente;
        }

        return generarReporte(
                "REPORTE DE ENVIOS POR PAIS",
                "Pais (origen o destino): " + pais,
                detalleCliente,
                "Reporte_Envios_Pais_",
                envios
        );
    }

    // =========================================================
    // REPORTE DE ENVIOS DEMORADOS
    // =========================================================

    public File generarEnviosDemorados(
            String nombreCliente,
            List<Document> envios
    ) throws IOException {

        String detalleCliente = null;

        if (nombreCliente != null && !nombreCliente.isBlank()) {
            detalleCliente = "Cliente: " + nombreCliente;
        }

        return generarReporte(
                "REPORTE DE ENVIOS DEMORADOS",
                "Estado: DEMORADO",
                detalleCliente,
                "Reporte_Envios_Demorados_",
                envios
        );
    }

    // =========================================================
    // REPORTE DE INCIDENTES ABIERTOS
    // =========================================================

    public File generarIncidentesAbiertos(
            String nombreCliente,
            List<Document> incidentes
    ) throws IOException {

        if (incidentes == null) {
            throw new IllegalArgumentException(
                    "La lista de incidentes no puede ser nula."
            );
        }

        File archivo = crearArchivo(
                "Reporte_Incidentes_Abiertos_"
        );

        try (PDDocument pdf = new PDDocument()) {

            PDPage pagina = new PDPage(PDRectangle.A4);
            pdf.addPage(pagina);

            PDPageContentStream contenido =
                    new PDPageContentStream(pdf, pagina);

            float y = 770;

            try {

                escribir(
                        contenido,
                        "REPORTE DE INCIDENTES ABIERTOS",
                        50, y, 17, true
                );

                y -= 35;

                escribir(
                        contenido,
                        "Estado: ABIERTO",
                        50, y, 11, true
                );

                y -= 22;

                if (nombreCliente != null
                        && !nombreCliente.isBlank()) {

                    escribir(
                            contenido,
                            "Cliente: " + nombreCliente,
                            50, y, 11, true
                    );

                    y -= 22;
                }

                escribir(
                        contenido,
                        "Fecha: " + fechaActual(),
                        50, y, 10, false
                );

                y -= 22;

                escribir(
                        contenido,
                        "Cantidad de incidentes: " + incidentes.size(),
                        50, y, 10, false
                );

                y -= 35;

                if (incidentes.isEmpty()) {

                    escribir(
                            contenido,
                            "No se encontraron incidentes abiertos.",
                            50, y, 11, false
                    );
                }

                for (Document incidente : incidentes) {

                    if (y < 170) {

                        contenido.close();

                        pagina = new PDPage(PDRectangle.A4);
                        pdf.addPage(pagina);

                        contenido = new PDPageContentStream(
                                pdf, pagina
                        );

                        y = 770;
                    }

                    escribir(
                            contenido,
                            "Incidente ID: " + valor(incidente.get("_id")),
                            50, y, 10, true
                    );

                    y -= 18;

                    escribir(
                            contenido,
                            "Envio ID: " + valor(incidente.get("envio_id")),
                            60, y, 9, false
                    );

                    y -= 16;

                    escribir(
                            contenido,
                            "Tipo: " + valor(incidente.get("tipo")),
                            60, y, 9, false
                    );

                    y -= 16;

                    escribir(
                            contenido,
                            "Severidad: " + valor(incidente.get("severidad")),
                            60, y, 9, false
                    );

                    y -= 16;

                    escribir(
                            contenido,
                            "Estado: " + valor(incidente.get("estado")),
                            60, y, 9, false
                    );

                    y -= 16;

                    escribir(
                            contenido,
                            "Fecha: " + valor(incidente.get("fecha")),
                            60, y, 9, false
                    );

                    y -= 16;

                    escribir(
                            contenido,
                            "Descripcion: " + valor(incidente.get("descripcion")),
                            60, y, 9, false
                    );

                    y -= 30;
                }

            } finally {
                contenido.close();
            }

            pdf.save(archivo);
        }

        return archivo;
    }

    // =========================================================
    // REPORTE ANALITICO - PROMEDIO DE INCIDENTES
    // =========================================================

    public File generarPromedioIncidentes(
            String agrupacion,
            String nombreCliente,
            List<Document> resultados
    ) throws IOException {

        if (resultados == null) {
            throw new IllegalArgumentException(
                    "La lista de resultados no puede ser nula."
            );
        }

        String tituloAgrupacion;

        switch (agrupacion) {

            case "PAIS":
                tituloAgrupacion = "PAIS";
                break;

            case "ESTADO":
                tituloAgrupacion = "ESTADO";
                break;

            case "CLIENTE":
                tituloAgrupacion = "CLIENTE";
                break;

            default:
                throw new IllegalArgumentException(
                        "Agrupacion no valida."
                );
        }

        File archivo = crearArchivo(
                "Reporte_Promedio_Incidentes_" + agrupacion + "_"
        );

        try (PDDocument pdf = new PDDocument()) {

            PDPage pagina = new PDPage(PDRectangle.A4);
            pdf.addPage(pagina);

            PDPageContentStream contenido =
                    new PDPageContentStream(pdf, pagina);

            float y = 770;

            try {

                escribir(
                        contenido,
                        "REPORTE ANALITICO DE INCIDENTES",
                        50, y, 17, true
                );

                y -= 35;

                escribir(
                        contenido,
                        "Agrupacion: " + tituloAgrupacion,
                        50, y, 11, true
                );

                y -= 22;

                if (nombreCliente != null
                        && !nombreCliente.isBlank()) {

                    escribir(
                            contenido,
                            "Cliente: " + nombreCliente,
                            50, y, 11, true
                    );

                    y -= 22;
                }

                escribir(
                        contenido,
                        "Fecha: " + fechaActual(),
                        50, y, 10, false
                );

                y -= 22;

                escribir(
                        contenido,
                        "Cantidad de grupos: " + resultados.size(),
                        50, y, 10, false
                );

                y -= 22;

                escribir(
                        contenido,
                        "Promedio = cantidad de incidentes / cantidad de envios",
                        50, y, 9, false
                );

                y -= 35;

                if (resultados.isEmpty()) {

                    escribir(
                            contenido,
                            "No se encontraron datos para el reporte.",
                            50, y, 11, false
                    );
                }

                for (Document resultado : resultados) {

                    if (y < 150) {

                        contenido.close();

                        pagina = new PDPage(PDRectangle.A4);
                        pdf.addPage(pagina);

                        contenido = new PDPageContentStream(
                                pdf, pagina
                        );

                        y = 770;
                    }

                    String grupo = valor(
                            resultado.get("grupo")
                    );

                    int cantidadEnvios = resultado.getInteger(
                            "cantidad_envios", 0
                    );

                    int cantidadIncidentes = resultado.getInteger(
                            "cantidad_incidentes", 0
                    );

                    double promedio = resultado.getDouble(
                            "promedio"
                    );

                    escribir(
                            contenido,
                            tituloAgrupacion + ": " + grupo,
                            50, y, 11, true
                    );

                    y -= 20;

                    escribir(
                            contenido,
                            "Cantidad de envios: " + cantidadEnvios,
                            60, y, 10, false
                    );

                    y -= 18;

                    escribir(
                            contenido,
                            "Cantidad de incidentes: " + cantidadIncidentes,
                            60, y, 10, false
                    );

                    y -= 18;

                    escribir(
                            contenido,
                            "Promedio de incidentes por envio: "
                                    + String.format(
                                            Locale.US,
                                            "%.2f",
                                            promedio
                                    ),
                            60, y, 10, true
                    );

                    y -= 35;
                }

            } finally {
                contenido.close();
            }

            pdf.save(archivo);
        }

        return archivo;
    }

    // =========================================================
    // RANKING DE RUTAS CON MAYOR CANTIDAD DE INCIDENTES
    // =========================================================

    public File generarRankingRutasIncidentes(
            String nombreCliente,
            List<Document> ranking
    ) throws IOException {

        if (ranking == null) {
            throw new IllegalArgumentException(
                    "El ranking no puede ser nulo."
            );
        }

        File archivo = crearArchivo(
                "Reporte_Ranking_Rutas_Incidentes_"
        );

        try (PDDocument pdf = new PDDocument()) {

            PDPage pagina = new PDPage(PDRectangle.A4);
            pdf.addPage(pagina);

            PDPageContentStream contenido =
                    new PDPageContentStream(pdf, pagina);

            float y = 770;

            try {

                escribir(
                        contenido,
                        "RANKING DE RUTAS CON MAS INCIDENTES",
                        50, y, 16, true
                );

                y -= 32;

                if (nombreCliente != null
                        && !nombreCliente.isBlank()) {

                    escribir(
                            contenido,
                            "Cliente: " + nombreCliente,
                            50, y, 11, true
                    );

                    y -= 22;
                }

                escribir(
                        contenido,
                        "Fecha: " + fechaActual(),
                        50, y, 10, false
                );

                y -= 22;

                escribir(
                        contenido,
                        "Cantidad de rutas: " + ranking.size(),
                        50, y, 10, false
                );

                y -= 32;

                if (ranking.isEmpty()) {

                    escribir(
                            contenido,
                            "No se encontraron rutas para este reporte.",
                            50, y, 11, false
                    );
                }

                int posicion = 1;

                for (Document ruta : ranking) {

                    if (y < 135) {

                        contenido.close();

                        pagina = new PDPage(PDRectangle.A4);
                        pdf.addPage(pagina);

                        contenido = new PDPageContentStream(
                                pdf, pagina
                        );

                        y = 770;
                    }

                    escribir(
                            contenido,
                            posicion + ". Ruta: "
                                    + valor(ruta.get("origen"))
                                    + " -> "
                                    + valor(ruta.get("destino")),
                            50, y, 11, true
                    );

                    y -= 19;

                    escribir(
                            contenido,
                            "Cantidad de envios: "
                                    + ruta.getInteger(
                                            "cantidad_envios", 0
                                    ),
                            60, y, 10, false
                    );

                    y -= 18;

                    escribir(
                            contenido,
                            "Cantidad de incidentes: "
                                    + ruta.getInteger(
                                            "cantidad_incidentes", 0
                                    ),
                            60, y, 10, true
                    );

                    y -= 30;
                    posicion++;
                }

            } finally {
                contenido.close();
            }

            pdf.save(archivo);
        }

        return archivo;
    }

    // =========================================================
    // GENERADOR GENERAL DE PDF DE ENVIOS
    // =========================================================

    private File generarReporte(
            String titulo,
            String detallePrincipal,
            String detalleSecundario,
            String prefijoArchivo,
            List<Document> envios
    ) throws IOException {

        if (envios == null) {
            throw new IllegalArgumentException(
                    "La lista de envios no puede ser nula."
            );
        }

        File archivo = crearArchivo(prefijoArchivo);

        try (PDDocument pdf = new PDDocument()) {

            PDPage pagina = new PDPage(PDRectangle.A4);
            pdf.addPage(pagina);

            PDPageContentStream contenido =
                    new PDPageContentStream(pdf, pagina);

            float y = 770;

            try {

                escribir(
                        contenido,
                        titulo,
                        50, y, 17, true
                );

                y -= 35;

                escribir(
                        contenido,
                        detallePrincipal,
                        50, y, 11, true
                );

                y -= 22;

                if (detalleSecundario != null) {

                    escribir(
                            contenido,
                            detalleSecundario,
                            50, y, 11, true
                    );

                    y -= 22;
                }

                escribir(
                        contenido,
                        "Fecha: " + fechaActual(),
                        50, y, 10, false
                );

                y -= 22;

                escribir(
                        contenido,
                        "Cantidad de envios: " + envios.size(),
                        50, y, 10, false
                );

                y -= 35;

                if (envios.isEmpty()) {

                    escribir(
                            contenido,
                            "No se encontraron envios para este reporte.",
                            50, y, 11, false
                    );
                }

                for (Document envio : envios) {

                    if (y < 150) {

                        contenido.close();

                        pagina = new PDPage(PDRectangle.A4);
                        pdf.addPage(pagina);

                        contenido = new PDPageContentStream(
                                pdf, pagina
                        );

                        y = 770;
                    }

                    Document origen = envio.get(
                            "origen", Document.class
                    );

                    Document destino = envio.get(
                            "destino", Document.class
                    );

                    escribir(
                            contenido,
                            "Envio ID: " + valor(envio.get("_id")),
                            50, y, 10, true
                    );

                    y -= 18;

                    escribir(
                            contenido,
                            "Origen: " + ubicacion(origen),
                            60, y, 9, false
                    );

                    y -= 16;

                    escribir(
                            contenido,
                            "Destino: " + ubicacion(destino),
                            60, y, 9, false
                    );

                    y -= 16;

                    escribir(
                            contenido,
                            "Estado: " + valor(envio.get("estado")),
                            60, y, 9, false
                    );

                    y -= 16;

                    escribir(
                            contenido,
                            "Fecha de creacion: "
                                    + valor(envio.get("fecha_creacion")),
                            60, y, 9, false
                    );

                    y -= 30;
                }

            } finally {
                contenido.close();
            }

            pdf.save(archivo);
        }

        return archivo;
    }

    // =========================================================
    // CREAR ARCHIVO
    // =========================================================

    private File crearArchivo(
            String prefijoArchivo
    ) throws IOException {

        File carpeta = new File("reportes");

        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw new IOException(
                    "No se pudo crear la carpeta de reportes."
            );
        }

        return new File(
                carpeta,
                prefijoArchivo + System.currentTimeMillis() + ".pdf"
        );
    }

    // =========================================================
    // METODOS AUXILIARES
    // =========================================================

    private String fechaActual() {

        SimpleDateFormat formato = new SimpleDateFormat(
                "dd/MM/yyyy HH:mm"
        );

        return formato.format(new Date());
    }

    private String ubicacion(Document ubicacion) {

        if (ubicacion == null) {
            return "Sin datos";
        }

        return valor(ubicacion.get("ciudad"))
                + ", "
                + valor(ubicacion.get("pais"));
    }

    private String valor(Object dato) {

        if (dato == null) {
            return "Sin datos";
        }

        return dato.toString();
    }

    private void escribir(
            PDPageContentStream contenido,
            String texto,
            float x,
            float y,
            int tamanio,
            boolean bold
    ) throws IOException {

        PDType1Font fuente = bold ? negrita : normal;

        String limpio = texto == null
                ? ""
                : texto.replaceAll(
                        "[^\\x20-\\x7E\\xA0-\\xFF]",
                        " "
                );

        int limite = 95;

        if (limpio.length() > limite) {
            limpio = limpio.substring(0, limite - 3) + "...";
        }

        contenido.beginText();
        contenido.setFont(fuente, tamanio);
        contenido.newLineAtOffset(x, y);
        contenido.showText(limpio);
        contenido.endText();
    }
}