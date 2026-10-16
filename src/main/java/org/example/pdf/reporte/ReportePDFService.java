
package org.example.pdf.reporte;

import org.bson.Document;
import org.example.mongoDB.controller.ClienteController;
import org.example.mongoDB.controller.EnvioController;
import org.example.mongoDB.controller.IncidenteController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ReportePDFService {

    private final ClienteController clienteController;
    private final EnvioController envioController;
    private final IncidenteController incidenteController;
    private final ReportePDFGenerator pdfGenerator;

    public ReportePDFService() {

        this.clienteController = new ClienteController();
        this.envioController = new EnvioController();
        this.incidenteController = new IncidenteController();
        this.pdfGenerator = new ReportePDFGenerator();
    }

    // =========================================================
    // REPORTE POR CLIENTE
    // =========================================================

    public File generarEnviosPorCliente(
            String clienteId,
            String nombreCliente
    ) throws IOException {

        validarCliente(clienteId);

        List<Document> envios =
                envioController.buscarPorCliente(clienteId);

        return pdfGenerator.generarEnviosPorCliente(
                nombreCliente,
                envios
        );
    }

    // =========================================================
    // REPORTE POR ESTADO - ADMINISTRADOR
    // =========================================================

    public File generarEnviosPorEstado(
            String estado
    ) throws IOException {

        validarEstado(estado);

        List<Document> envios =
                envioController.buscarPorEstado(estado);

        return pdfGenerator.generarEnviosPorEstado(
                estado,
                null,
                envios
        );
    }

    // =========================================================
    // REPORTE POR ESTADO - CLIENTE
    // =========================================================

    public File generarEnviosPorEstado(
            String estado,
            String clienteId,
            String nombreCliente
    ) throws IOException {

        validarEstado(estado);
        validarCliente(clienteId);

        List<Document> enviosCliente =
                envioController.buscarPorCliente(clienteId);

        List<Document> enviosFiltrados = new ArrayList<>();

        for (Document envio : enviosCliente) {

            String estadoEnvio = envio.getString("estado");

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
    // REPORTE POR PAIS - ADMINISTRADOR
    // =========================================================

    public File generarEnviosPorPais(
            String pais
    ) throws IOException {

        validarPais(pais);

        List<Document> envios =
                envioController.buscarPorPais(pais);

        return pdfGenerator.generarEnviosPorPais(
                pais,
                null,
                envios
        );
    }

    // =========================================================
    // REPORTE POR PAIS - CLIENTE
    // =========================================================

    public File generarEnviosPorPais(
            String pais,
            String clienteId,
            String nombreCliente
    ) throws IOException {

        validarPais(pais);
        validarCliente(clienteId);

        List<Document> enviosCliente =
                envioController.buscarPorCliente(clienteId);

        List<Document> enviosFiltrados = new ArrayList<>();

        for (Document envio : enviosCliente) {

            Document origen = envio.get(
                    "origen",
                    Document.class
            );

            Document destino = envio.get(
                    "destino",
                    Document.class
            );

            boolean coincideOrigen =
                    origen != null
                            && pais.equals(origen.getString("pais"));

            boolean coincideDestino =
                    destino != null
                            && pais.equals(destino.getString("pais"));

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

    public File generarEnviosDemorados() throws IOException {

        List<Document> envios =
                envioController.buscarDemorados();

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
                envioController.buscarPorCliente(clienteId);

        List<Document> enviosDemorados = new ArrayList<>();

        for (Document envio : enviosCliente) {

            String estado = envio.getString("estado");

            if (estado != null
                    && "DEMORADO".equalsIgnoreCase(estado)) {

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

    public File generarIncidentesAbiertos() throws IOException {

        List<Document> incidentes =
                incidenteController.buscarAbiertos();

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
                envioController.buscarPorCliente(clienteId);

        Set<String> idsEnvios = new HashSet<>();

        for (Document envio : enviosCliente) {

            Object id = envio.get("_id");

            if (id != null) {
                idsEnvios.add(id.toString());
            }
        }

        List<Document> incidentesAbiertos =
                incidenteController.buscarAbiertos();

        List<Document> incidentesFiltrados =
                new ArrayList<>();

        for (Document incidente : incidentesAbiertos) {

            Object envioId = incidente.get("envio_id");

            if (envioId != null
                    && idsEnvios.contains(envioId.toString())) {

                incidentesFiltrados.add(incidente);
            }
        }

        return pdfGenerator.generarIncidentesAbiertos(
                nombreCliente,
                incidentesFiltrados
        );
    }

    // =========================================================
    // REPORTE ANALITICO - PROMEDIO DE INCIDENTES
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
                    "La agrupacion debe ser PAIS, ESTADO o CLIENTE."
            );
        }

        List<Document> envios;

        if (clienteId == null) {

            envios = envioController.listarEnvios();

        } else {

            validarCliente(clienteId);

            envios = envioController.buscarPorCliente(
                    clienteId
            );
        }

        List<Document> incidentes =
                incidenteController.listarIncidentes();

        // Cantidad de incidentes por envio.

        Map<String, Integer> incidentesPorEnvio =
                new HashMap<>();

        for (Document incidente : incidentes) {

            Object id = incidente.get("envio_id");

            if (id == null) {
                continue;
            }

            String envioId = id.toString();

            incidentesPorEnvio.merge(
                    envioId,
                    1,
                    Integer::sum
            );
        }

        // Agrupar envios e incidentes.

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

            Set<String> grupos = new HashSet<>();

            if ("CLIENTE".equals(agrupacion)) {

                Object cliente = envio.get("cliente_id");

                if (cliente == null) {

                    grupos.add("Sin cliente");

                } else {

                    String idCliente = cliente.toString();

                    // Buscar la razon social en MongoDB.

                    Document datosCliente =
                            clienteController.buscarPorId(
                                    idCliente
                            );

                    String razonSocial = null;

                    if (datosCliente != null) {
                        razonSocial =
                                datosCliente.getString("razon_social");
                    }

                    if (razonSocial != null
                            && !razonSocial.isBlank()) {

                        grupos.add(razonSocial);

                    } else {

                        // Si no existe la razon social,
                        // conservamos el ID como respaldo.

                        grupos.add(idCliente);
                    }
                }

            } else if ("ESTADO".equals(agrupacion)) {

                String estado = envio.getString("estado");

                grupos.add(
                        estado == null || estado.isBlank()
                                ? "Sin estado"
                                : estado
                );

            } else {

                Document origen = envio.get(
                        "origen",
                        Document.class
                );

                Document destino = envio.get(
                        "destino",
                        Document.class
                );

                if (origen != null) {

                    String pais = origen.getString("pais");

                    if (pais != null && !pais.isBlank()) {
                        grupos.add(pais);
                    }
                }

                if (destino != null) {

                    String pais = destino.getString("pais");

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

        List<Document> resultados = new ArrayList<>();

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
    // CLASE AUXILIAR PARA CALCULOS
    // =========================================================

    private static class EstadisticaIncidentes {

        private int envios = 0;
        private int incidentes = 0;
    }

    // =========================================================
    // VALIDACIONES
    // =========================================================

    private void validarEstado(String estado) {

        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un estado."
            );
        }
    }

    private void validarPais(String pais) {

        if (pais == null || pais.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un país."
            );
        }
    }

    private void validarCliente(String clienteId) {

        if (clienteId == null || clienteId.isBlank()) {
            throw new IllegalArgumentException(
                    "El usuario no tiene un cliente asociado."
            );
        }
    }
}
