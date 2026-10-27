
package org.example.pdf.reporte;

import org.example.mongoDB.controller.ControllerMongoDB;

import org.example.consola.TablaConsola;

import org.bson.Document;
import org.example.mongoDB.model.Sensor;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ReportePDFService {

    private final ControllerMongoDB mongo = new ControllerMongoDB();

    private final ReportePDFGenerator pdfGenerator;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ReportePDFService() {

        this.pdfGenerator = new ReportePDFGenerator();
    }

    // =========================================================
    // REPORTE DE ENVIOS POR CLIENTE
    // =========================================================

    public File generarEnviosPorCliente(
            String clienteId,
            String nombreCliente
    ) throws IOException {

        validarCliente(clienteId);

        List<Document> envios =
                mongo.buscarEnviosPorCliente(clienteId);

        return pdfGenerator.generarEnviosPorCliente(
                nombreCliente,
                envios
        );
    }

    // =========================================================
    // ENVIOS POR ESTADO - ADMINISTRADOR
    // =========================================================

    public File generarEnviosPorEstado(
            String estado
    ) throws IOException {

        validarEstado(estado);

        List<Document> envios =
                mongo.buscarEnviosPorEstado(estado);

        return pdfGenerator.generarEnviosPorEstado(
                estado,
                null,
                envios
        );
    }

    // =========================================================
    // ENVIOS POR ESTADO - CLIENTE
    // =========================================================

    public File generarEnviosPorEstado(
            String estado,
            String clienteId,
            String nombreCliente
    ) throws IOException {

        validarEstado(estado);
        validarCliente(clienteId);

        List<Document> enviosCliente =
                mongo.buscarEnviosPorCliente(clienteId);

        List<Document> enviosFiltrados =
                new ArrayList<>();

        for (Document envio : enviosCliente) {

            String estadoEnvio =
                    envio.getString("estado");

            if (estadoEnvio != null
                    && estado.equalsIgnoreCase(estadoEnvio)) {

                enviosFiltrados.add(envio);
            }
        }

        return pdfGenerator.generarEnviosPorEstado(
                estado,
                nombreCliente,
                enviosFiltrados
        );
    }

    // =========================================================
    // ENVIOS POR PAIS - ADMINISTRADOR
    // =========================================================

    public File generarEnviosPorPais(
            String pais
    ) throws IOException {

        validarPais(pais);

        List<Document> envios =
                mongo.buscarEnviosPorPais(pais);

        return pdfGenerator.generarEnviosPorPais(
                pais,
                null,
                envios
        );
    }

    // =========================================================
    // ENVIOS POR PAIS - CLIENTE
    // =========================================================

    public File generarEnviosPorPais(
            String pais,
            String clienteId,
            String nombreCliente
    ) throws IOException {

        validarPais(pais);
        validarCliente(clienteId);

        List<Document> enviosCliente =
                mongo.buscarEnviosPorCliente(clienteId);

        List<Document> enviosFiltrados =
                new ArrayList<>();

        for (Document envio : enviosCliente) {

            Document origen =
                    envio.get("origen", Document.class);

            Document destino =
                    envio.get("destino", Document.class);

            boolean coincideOrigen =
                    origen != null
                            && pais.equals(
                                    origen.getString("pais")
                            );

            boolean coincideDestino =
                    destino != null
                            && pais.equals(
                                    destino.getString("pais")
                            );

            if (coincideOrigen || coincideDestino) {
                enviosFiltrados.add(envio);
            }
        }

        return pdfGenerator.generarEnviosPorPais(
                pais,
                nombreCliente,
                enviosFiltrados
        );
    }

    // =========================================================
    // ENVIOS DEMORADOS - ADMINISTRADOR
    // =========================================================

    public File generarEnviosDemorados()
            throws IOException {

        List<Document> envios =
                mongo.buscarEnviosDemorados();

        return pdfGenerator.generarEnviosDemorados(
                null,
                envios
        );
    }

    // =========================================================
    // ENVIOS DEMORADOS - CLIENTE
    // =========================================================

    public File generarEnviosDemorados(
            String clienteId,
            String nombreCliente
    ) throws IOException {

        validarCliente(clienteId);

        List<Document> enviosCliente =
                mongo.buscarEnviosPorCliente(clienteId);

        List<Document> enviosDemorados =
                new ArrayList<>();

        for (Document envio : enviosCliente) {

            String estado =
                    envio.getString("estado");

            if ("DEMORADO".equalsIgnoreCase(estado)) {
                enviosDemorados.add(envio);
            }
        }

        return pdfGenerator.generarEnviosDemorados(
                nombreCliente,
                enviosDemorados
        );
    }

    // =========================================================
    // INCIDENTES ABIERTOS - ADMINISTRADOR
    // =========================================================

    public File generarIncidentesAbiertos()
            throws IOException {

        List<Document> incidentes =
                mongo.buscarIncidentesAbiertos();

        return pdfGenerator.generarIncidentesAbiertos(
                null,
                incidentes
        );
    }

    // =========================================================
    // INCIDENTES ABIERTOS - CLIENTE
    // =========================================================

    public File generarIncidentesAbiertos(
            String clienteId,
            String nombreCliente
    ) throws IOException {

        validarCliente(clienteId);

        List<Document> enviosCliente =
                mongo.buscarEnviosPorCliente(clienteId);

        Set<String> idsEnvios =
                new HashSet<>();

        for (Document envio : enviosCliente) {

            Object id = envio.get("_id");

            if (id != null) {
                idsEnvios.add(id.toString());
            }
        }

        List<Document> incidentesAbiertos =
                mongo.buscarIncidentesAbiertos();

        List<Document> incidentesFiltrados =
                new ArrayList<>();

        for (Document incidente : incidentesAbiertos) {

            Object envioId =
                    incidente.get("envio_id");

            if (envioId != null
                    && idsEnvios.contains(
                            envioId.toString()
                    )) {

                incidentesFiltrados.add(incidente);
            }
        }

        return pdfGenerator.generarIncidentesAbiertos(
                nombreCliente,
                incidentesFiltrados
        );
    }

    // =========================================================
    // PROMEDIO DE INCIDENTES
    // =========================================================

    public File generarPromedioIncidentes(
            String agrupacion
    ) throws IOException {

        return generarPromedioIncidentes(
                agrupacion,
                null,
                null
        );
    }

    public File generarPromedioIncidentes(
            String agrupacion,
            String clienteId,
            String nombreCliente
    ) throws IOException {

        if (!"PAIS".equals(agrupacion)
                && !"ESTADO".equals(agrupacion)
                && !"CLIENTE".equals(agrupacion)) {

            throw new IllegalArgumentException(
                    "La agrupación debe ser PAIS, ESTADO o CLIENTE."
            );
        }

        List<Document> envios;

        if (clienteId == null) {

            envios = mongo.listarEnvios();

        } else {

            validarCliente(clienteId);

            envios =
                    mongo.buscarEnviosPorCliente(clienteId);
        }

        List<Document> incidentes =
                mongo.listarIncidentes();

        Map<String, Integer> incidentesPorEnvio =
                new HashMap<>();

        for (Document incidente : incidentes) {

            Object id = incidente.get("envio_id");

            if (id == null) {
                continue;
            }

            incidentesPorEnvio.merge(
                    id.toString(),
                    1,
                    Integer::sum
            );
        }

        Map<String, EstadisticaIncidentes> estadisticas =
                new TreeMap<>();

        for (Document envio : envios) {

            Object id = envio.get("_id");

            if (id == null) {
                continue;
            }

            String envioId = id.toString();

            int cantidadIncidentes =
                    incidentesPorEnvio.getOrDefault(
                            envioId,
                            0
                    );

            Set<String> grupos =
                    new HashSet<>();

            if ("CLIENTE".equals(agrupacion)) {

                Object cliente =
                        envio.get("cliente_id");

                if (cliente == null) {

                    grupos.add("Sin cliente");

                } else {

                    String idCliente =
                            cliente.toString();

                    Document datosCliente =
                            mongo.consultarClientePorId(
                                    idCliente
                            );

                    String razonSocial =
                            datosCliente == null
                                    ? null
                                    : datosCliente.getString(
                                            "razon_social"
                                    );

                    grupos.add(
                            razonSocial != null
                                    && !razonSocial.isBlank()
                                    ? razonSocial
                                    : idCliente
                    );
                }

            } else if ("ESTADO".equals(agrupacion)) {

                String estado =
                        envio.getString("estado");

                grupos.add(
                        estado == null || estado.isBlank()
                                ? "Sin estado"
                                : estado
                );

            } else {

                Document origen =
                        envio.get("origen", Document.class);

                Document destino =
                        envio.get("destino", Document.class);

                if (origen != null) {

                    String pais =
                            origen.getString("pais");

                    if (pais != null && !pais.isBlank()) {
                        grupos.add(pais);
                    }
                }

                if (destino != null) {

                    String pais =
                            destino.getString("pais");

                    if (pais != null && !pais.isBlank()) {
                        grupos.add(pais);
                    }
                }

                if (grupos.isEmpty()) {
                    grupos.add("Sin pais");
                }
            }

            for (String grupo : grupos) {

                EstadisticaIncidentes estadistica =
                        estadisticas.computeIfAbsent(
                                grupo,
                                k -> new EstadisticaIncidentes()
                        );

                estadistica.envios++;
                estadistica.incidentes += cantidadIncidentes;
            }
        }

        List<Document> resultados =
                new ArrayList<>();

        for (Map.Entry<String, EstadisticaIncidentes> entry
                : estadisticas.entrySet()) {

            EstadisticaIncidentes estadistica =
                    entry.getValue();

            double promedio =
                    estadistica.envios == 0
                            ? 0
                            : (double) estadistica.incidentes
                            / estadistica.envios;

            resultados.add(
                    new Document("grupo", entry.getKey())
                            .append(
                                    "cantidad_envios",
                                    estadistica.envios
                            )
                            .append(
                                    "cantidad_incidentes",
                                    estadistica.incidentes
                            )
                            .append(
                                    "promedio",
                                    promedio
                            )
            );
        }

        return pdfGenerator.generarPromedioIncidentes(
                agrupacion,
                nombreCliente,
                resultados
        );
    }

    // =========================================================
    // RANKING DE RUTAS CON INCIDENTES
    // =========================================================

    public List<Document> obtenerRankingRutasIncidentes() {
        return TablaConsola.mostrar("ReportePDFService.obtenerRankingRutasIncidentes", obtenerRankingRutasIncidentes(null));
    }

    public List<Document> obtenerRankingRutasIncidentes(
            String clienteId
    ) {

        List<Document> envios;

        if (clienteId == null) {

            envios = mongo.listarEnvios();

        } else {

            validarCliente(clienteId);

            envios =
                    mongo.buscarEnviosPorCliente(clienteId);
        }

        Map<String, Integer> incidentesPorEnvio =
                new HashMap<>();

        for (Document incidente :
                mongo.listarIncidentes()) {

            Object envioId =
                    incidente.get("envio_id");

            if (envioId != null) {

                incidentesPorEnvio.merge(
                        envioId.toString(),
                        1,
                        Integer::sum
                );
            }
        }

        Map<String, Document> rutas =
                new HashMap<>();

        for (Document envio : envios) {

            Object envioId =
                    envio.get("_id");

            if (envioId == null) {
                continue;
            }

            Document origen =
                    envio.get("origen", Document.class);

            Document destino =
                    envio.get("destino", Document.class);

            if (origen == null || destino == null) {
                continue;
            }

            String paisOrigen =
                    origen.getString("pais");

            String paisDestino =
                    destino.getString("pais");

            if (paisOrigen == null
                    || paisOrigen.isBlank()
                    || paisDestino == null
                    || paisDestino.isBlank()) {

                continue;
            }

            paisOrigen = paisOrigen.trim();
            paisDestino = paisDestino.trim();

            String clave =
                    paisOrigen.toLowerCase(Locale.ROOT)
                            + "\u0000"
                            + paisDestino.toLowerCase(Locale.ROOT);

            Document ruta = rutas.get(clave);

            if (ruta == null) {

                ruta =
                        new Document("origen", paisOrigen)
                                .append("destino", paisDestino)
                                .append(
                                        "ruta",
                                        paisOrigen + " → " + paisDestino
                                )
                                .append("cantidad_envios", 0)
                                .append("cantidad_incidentes", 0);

                rutas.put(clave, ruta);
            }

            ruta.put(
                    "cantidad_envios",
                    ruta.getInteger("cantidad_envios") + 1
            );

            ruta.put(
                    "cantidad_incidentes",
                    ruta.getInteger("cantidad_incidentes")
                            + incidentesPorEnvio.getOrDefault(
                                    envioId.toString(),
                                    0
                            )
            );
        }

        List<Document> ranking =
                new ArrayList<>(rutas.values());

        ranking.sort((a, b) -> {

            int porIncidentes = Integer.compare(
                    b.getInteger("cantidad_incidentes"),
                    a.getInteger("cantidad_incidentes")
            );

            return porIncidentes != 0
                    ? porIncidentes
                    : a.getString("ruta")
                    .compareToIgnoreCase(
                            b.getString("ruta")
                    );
        });

        return TablaConsola.mostrar("ReportePDFService.obtenerRankingRutasIncidentes", ranking);
    }

    // =========================================================
    // PDF - RANKING DE RUTAS
    // =========================================================

    public File generarRankingRutasIncidentes()
            throws IOException {

        List<Document> ranking =
                obtenerRankingRutasIncidentes();

        return pdfGenerator.generarRankingRutasIncidentes(
                null,
                ranking
        );
    }

    public File generarRankingRutasIncidentes(
            String clienteId,
            String nombreCliente
    ) throws IOException {

        List<Document> ranking =
                obtenerRankingRutasIncidentes(clienteId);

        return pdfGenerator.generarRankingRutasIncidentes(
                nombreCliente,
                ranking
        );
    }

    // =========================================================
    // REPORTE DE ESTADO DE SENSORES
    // =========================================================

    public File generarEstadoSensores()
            throws IOException {

        List<Document> documentos =
                mongo.listarSensores();

        List<Sensor> sensores =
                new ArrayList<>();

        for (Document documento : documentos) {

            Object id =
                    documento.get("_id");

            Object contenedorId =
                    documento.get("contenedor_id");

            sensores.add(
                    new Sensor(
                            id == null
                                    ? null
                                    : id.toString(),
                            contenedorId == null
                                    ? null
                                    : contenedorId.toString(),
                            documento.getString("tipo"),
                            documento.getString("fabricante"),
                            documento.getDate("fecha_instalacion"),
                            documento.getString("estado")
                    )
            );
        }

        return pdfGenerator.generarEstadoSensores(
                sensores
        );
    }

    // =========================================================
    // TIEMPOS DE ENTREGA POR CLIENTE
    // =========================================================

    public List<Document> obtenerTiemposEntregaPorCliente(
            String clienteId
    ) {

        List<Document> envios =
                clienteId == null
                        ? mongo.listarEnvios()
                        : mongo.buscarEnviosPorCliente(clienteId);

        Map<String, List<Long>> duraciones =
                new TreeMap<>();

        for (Document envio : envios) {

            Long dias = calcularDiasEntrega(envio);

            if (dias == null) {
                continue;
            }

            Object cliente =
                    envio.get("cliente_id");

            if (cliente == null
                    || cliente.toString().isBlank()) {
                continue;
            }

            duraciones.computeIfAbsent(
                    cliente.toString(),
                    k -> new ArrayList<>()
            ).add(dias);
        }

        List<Document> resultados =
                new ArrayList<>();

        for (Map.Entry<String, List<Long>> entry
                : duraciones.entrySet()) {

            List<Long> dias =
                    entry.getValue();

            long minimo =
                    dias.stream()
                            .mapToLong(Long::longValue)
                            .min()
                            .orElse(0);

            long maximo =
                    dias.stream()
                            .mapToLong(Long::longValue)
                            .max()
                            .orElse(0);

            double promedio =
                    dias.stream()
                            .mapToLong(Long::longValue)
                            .average()
                            .orElse(0);

            Document datosCliente =
                    mongo.consultarClientePorId(
                            entry.getKey()
                    );

            String nombre =
                    datosCliente == null
                            ? null
                            : datosCliente.getString("razon_social");

            if (nombre == null || nombre.isBlank()) {
                nombre = entry.getKey();
            }

            resultados.add(
                    new Document(
                            "cliente_id",
                            entry.getKey()
                    )
                            .append("cliente", nombre)
                            .append(
                                    "cantidad_envios",
                                    dias.size()
                            )
                            .append("minimo_dias", minimo)
                            .append("maximo_dias", maximo)
                            .append("promedio_dias", promedio)
            );
        }

        return TablaConsola.mostrar("ReportePDFService.obtenerTiemposEntregaPorCliente", resultados);
    }

    // =========================================================
    // TIEMPO PROMEDIO DE ENTREGA
    // =========================================================

    public Document obtenerTiempoPromedioEntrega(
            String clienteId
    ) {

        List<Document> envios =
                clienteId == null
                        ? mongo.listarEnvios()
                        : mongo.buscarEnviosPorCliente(clienteId);

        long cantidad = 0;
        long diasTotales = 0;

        for (Document envio : envios) {

            Long dias = calcularDiasEntrega(envio);

            if (dias != null) {
                cantidad++;
                diasTotales += dias;
            }
        }

        return TablaConsola.mostrar("ReportePDFService.obtenerTiempoPromedioEntrega", new Document(
                "cliente_id",
                clienteId
        )
                .append(
                        "cantidad_envios",
                        cantidad
                )
                .append(
                        "promedio_dias",
                        cantidad == 0
                                ? null
                                : (double) diasTotales / cantidad
                ));
    }

    // =========================================================
    // CALCULAR DIAS DE ENTREGA
    // =========================================================

    private Long calcularDiasEntrega(
            Document envio
    ) {

        if (!"ENTREGADO".equalsIgnoreCase(
                envio.getString("estado")
        )) {
            return null;
        }

        LocalDate salida =
                leerFechaEnvio(
                        envio.get("fecha_salida")
                );

        LocalDate entrega =
                leerFechaEnvio(
                        envio.get("fecha_entrega")
                );

        if (salida == null
                || entrega == null
                || entrega.isBefore(salida)) {

            return null;
        }

        return ChronoUnit.DAYS.between(
                salida,
                entrega
        );
    }

    private LocalDate leerFechaEnvio(
            Object valor
    ) {

        if (valor == null
                || valor.toString().isBlank()) {
            return null;
        }

        try {

            return LocalDate.parse(
                    valor.toString().trim()
            );

        } catch (java.time.format.DateTimeParseException e) {

            return null;
        }
    }

    // =========================================================
    // PDF - TIEMPOS MINIMOS Y MAXIMOS
    // =========================================================

    public File generarTiemposPorCliente(
            String clienteId
    ) throws IOException {

        List<Document> resultados =
                obtenerTiemposEntregaPorCliente(clienteId);

        return pdfGenerator.generarTiemposPorCliente(
                resultados
        );
    }

    // =========================================================
    // PDF - TIEMPO PROMEDIO DE ENTREGA
    // =========================================================

    public File generarTiempoPromedioEntrega(
            String clienteId
    ) throws IOException {

        Document resultado =
                obtenerTiempoPromedioEntrega(clienteId);

        return pdfGenerator.generarTiempoPromedioEntrega(
                resultado
        );
    }

    // =========================================================
    // COSTOS POR TIPO DE TRANSPORTE
    // =========================================================

    public List<Document> obtenerCostosPorTipoTransporte(
            String clienteId
    ) {

        List<Document> envios;

        if (clienteId == null) {

            envios = mongo.listarEnvios();

        } else {

            validarCliente(clienteId);

            envios =
                    mongo.buscarEnviosPorCliente(clienteId);
        }

        Map<String, EstadisticaCostos> estadisticas =
                new TreeMap<>();

        for (Document envio : envios) {

            String transporte =
                    envio.getString("tipo_transporte");

            Object costoValor =
                    envio.get("costo_envio");

            if (transporte == null
                    || transporte.isBlank()
                    || !(costoValor instanceof Number)) {

                continue;
            }

            double costo =
                    ((Number) costoValor).doubleValue();

            if (!Double.isFinite(costo)
                    || costo < 0) {

                continue;
            }

            String tipo =
                    transporte.trim()
                            .toUpperCase(Locale.ROOT);

            EstadisticaCostos estadistica =
                    estadisticas.computeIfAbsent(
                            tipo,
                            k -> new EstadisticaCostos()
                    );

            estadistica.cantidadEnvios++;
            estadistica.costoTotal =
                    estadistica.costoTotal.add(
                            BigDecimal.valueOf(costo)
                    );
        }

        List<Document> resultados =
                new ArrayList<>();

        for (Map.Entry<String, EstadisticaCostos> entry
                : estadisticas.entrySet()) {

            EstadisticaCostos estadistica =
                    entry.getValue();

            resultados.add(
                    new Document(
                            "tipo_transporte",
                            entry.getKey()
                    )
                            .append(
                                    "cantidad_envios",
                                    estadistica.cantidadEnvios
                            )
                            .append(
                                    "costo_total",
                                    estadistica.costoTotal.doubleValue()
                            )
            );
        }

        return TablaConsola.mostrar("ReportePDFService.obtenerCostosPorTipoTransporte", resultados);
    }

    // =========================================================
    // PDF - COSTOS POR TIPO DE TRANSPORTE
    // =========================================================

    public File generarCostosPorTipoTransporte(
            String clienteId,
            String nombreCliente
    ) throws IOException {

        if (clienteId != null) {
            validarCliente(clienteId);
        }

        List<Document> resultados =
                obtenerCostosPorTipoTransporte(clienteId);

        return pdfGenerator.generarCostosPorTipoTransporte(
                nombreCliente,
                resultados
        );
    }

    // =========================================================
    // CLASES AUXILIARES
    // =========================================================

    private static class EstadisticaIncidentes {

        private int envios = 0;
        private int incidentes = 0;
    }

    private static class EstadisticaCostos {

        private int cantidadEnvios = 0;
        private BigDecimal costoTotal = BigDecimal.ZERO;
    }

    // =========================================================
    // VALIDACIONES
    // =========================================================

    private void validarEstado(
            String estado
    ) {

        if (estado == null || estado.isBlank()) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un estado."
            );
        }
    }

    private void validarPais(
            String pais
    ) {

        if (pais == null || pais.isBlank()) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un país."
            );
        }
    }

    private void validarCliente(
            String clienteId
    ) {

        if (clienteId == null || clienteId.isBlank()) {

            throw new IllegalArgumentException(
                    "El usuario no tiene un cliente asociado."
            );
        }
    }
}
