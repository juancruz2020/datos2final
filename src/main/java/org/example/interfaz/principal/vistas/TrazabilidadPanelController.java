
package org.example.interfaz.principal.vistas;

import org.example.mongoDB.controller.ControllerMongoDB;

import com.datastax.oss.driver.api.core.cql.Row;
import org.bson.Document;
import org.example.cassandra.monitoreo.controller.ControllerMonitoreo;
import org.example.mongoDB.service.PermisosService;
import org.example.neo4j.controller.ControllerNeo4j;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TrazabilidadPanelController {

    private final ControllerMongoDB mongo = new ControllerMongoDB();

    private final TrazabilidadPanel view;
    private final String usuarioId;

    private final PermisosService permisosService =
            new PermisosService();


    private final ControllerMonitoreo monitoreoController =
            new ControllerMonitoreo();

    private final ControllerNeo4j neo4j =
            new ControllerNeo4j();

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public TrazabilidadPanelController(
            TrazabilidadPanel view,
            String usuarioId
    ) {

        this.view = view;
        this.usuarioId = usuarioId;

        // Solo Administrador y Operador pueden acceder.
        permisosService.exigirAdminUOperador(usuarioId);

        view.getBtnActualizar().addActionListener(
                e -> cargarAnalisis()
        );

        cargarAnalisis();
    }

    // =========================================================
    // CARGAR ANÁLISIS
    // =========================================================

    private void cargarAnalisis() {

        // Verificar permisos antes de consultar los datos.
        permisosService.exigirAdminUOperador(usuarioId);

        cargarRiesgosPorEventos();
        cargarAnomaliasTemperatura();
        cargarBateriasBajas();
        cargarVibracionesAltas();

        view.getLblEstado().setText(
                "Análisis de trazabilidad actualizado."
        );
    }

    // =========================================================
    // RIESGOS POR EVENTOS
    // =========================================================

    private void cargarRiesgosPorEventos() {

        DefaultTableModel modelo =
                (DefaultTableModel) view
                        .getTablaRiesgosEventos()
                        .getModel();

        modelo.setRowCount(0);

        try {

            Map<String, Map<String, Object>> porId =
                    new HashMap<>();

            // Obtener envíos desde Neo4j.
            for (Map<String, Object> envio : neo4j.listarEnvios()) {

                Object id = envio.get("id");

                if (id != null) {
                    porId.put(id.toString(), envio);
                }
            }

            Map<String, Integer> repeticiones =
                    new HashMap<>();

            Map<String, String> ubicaciones =
                    new HashMap<>();

            Map<String, Map<String, Object>> enviosConIncidente =
                    new HashMap<>();

            // Buscar envíos que tienen incidentes.
            for (Document incidente :
                    mongo.listarIncidentes()) {

                Object idValor = incidente.get("envio_id");

                if (idValor == null) {
                    continue;
                }

                String envioId = idValor.toString();

                Map<String, Object> envio =
                        porId.get(envioId);

                if (envio == null) {
                    continue;
                }

                enviosConIncidente.put(envioId, envio);
            }

            // Contabilizar ubicaciones relacionadas
            // con envíos que tuvieron incidentes.
            for (Map.Entry<String, Map<String, Object>> entrada :
                    enviosConIncidente.entrySet()) {

                Map<String, Object> envio =
                        entrada.getValue();

                for (String campo :
                        new String[]{"ciudadOrigen", "ciudadDestino"}) {

                    Object ubicacion = envio.get(campo);

                    if (ubicacion == null
                            || ubicacion.toString().isBlank()) {
                        continue;
                    }

                    String claveUbicacion =
                            ubicacion.toString()
                                    .trim()
                                    .toLowerCase();

                    repeticiones.merge(
                            claveUbicacion,
                            1,
                            Integer::sum
                    );

                    ubicaciones.put(
                            claveUbicacion,
                            ubicacion.toString()
                    );
                }
            }

            java.util.Set<String> enviosRiesgosos =
                    new java.util.LinkedHashSet<>();

            Map<String, String> ubicacionRiesgoPorEnvio =
                    new HashMap<>();

            // Detectar envíos que comparten ubicaciones
            // con envíos que tuvieron incidentes.
            for (Map.Entry<String, Map<String, Object>> entrada :
                    porId.entrySet()) {

                String envioId = entrada.getKey();

                if (enviosConIncidente.containsKey(envioId)) {
                    continue;
                }

                Map<String, Object> envio =
                        entrada.getValue();

                for (String campo :
                        new String[]{"ciudadOrigen", "ciudadDestino"}) {

                    Object ubicacion = envio.get(campo);

                    if (ubicacion == null) {
                        continue;
                    }

                    String claveUbicacion =
                            ubicacion.toString()
                                    .trim()
                                    .toLowerCase();

                    if (repeticiones.getOrDefault(
                            claveUbicacion, 0) > 1) {

                        enviosRiesgosos.add(envioId);

                        ubicacionRiesgoPorEnvio.putIfAbsent(
                                envioId,
                                ubicaciones.get(claveUbicacion)
                        );
                    }
                }
            }

            // Mostrar resultados.
            for (String envioId : enviosRiesgosos) {

                String ubicacion =
                        ubicacionRiesgoPorEnvio.get(envioId);

                if (ubicacion == null) {
                    continue;
                }

                modelo.addRow(new Object[]{
                        envioId,
                        ubicacion,
                        repeticiones.get(
                                ubicacion.toLowerCase()
                        ),
                        "Comparte ubicación con envíos que tuvieron incidentes"
                });
            }

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron calcular los riesgos por eventos.",
                    e
            );
        }
    }

    // =========================================================
    // ANOMALÍAS DE TEMPERATURA
    // =========================================================

    private void cargarAnomaliasTemperatura() {

        DefaultTableModel modelo =
                (DefaultTableModel) view
                        .getTablaAnomaliasTemperatura()
                        .getModel();

        modelo.setRowCount(0);

        try {

            for (Row row :
                    monitoreoController.obtenerTodasLasLecturas()) {

                if (row.isNull("temperatura")) {
                    continue;
                }

                BigDecimal temperatura =
                        row.getBigDecimal("temperatura");

                // Temperatura fuera del rango 15 a 40 °C.
                if (temperatura.compareTo(
                        BigDecimal.valueOf(15)) < 0
                        || temperatura.compareTo(
                        BigDecimal.valueOf(40)) > 0) {

                    String contenedor =
                            row.isNull("contenedor_id")
                                    ? ""
                                    : row.getString("contenedor_id");

                    String sensor =
                            row.isNull("sensor_id")
                                    ? ""
                                    : row.getString("sensor_id");

                    String fecha =
                            row.isNull("fecha_hora")
                                    ? ""
                                    : row.getObject("fecha_hora")
                                            .toString();

                    modelo.addRow(new Object[]{
                            contenedor,
                            sensor,
                            temperatura,
                            fecha
                    });
                }
            }

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron consultar las temperaturas de riesgo.",
                    e
            );
        }
    }

    // =========================================================
    // BATERÍAS BAJAS
    // =========================================================

    private void cargarBateriasBajas() {

        DefaultTableModel modelo =
                (DefaultTableModel) view
                        .getTablaBateriasBajas()
                        .getModel();

        modelo.setRowCount(0);

        try {

            for (Row row :
                    monitoreoController.obtenerTodasLasLecturas()) {

                if (row.isNull("bateria")) {
                    continue;
                }

                BigDecimal bateria =
                        row.getBigDecimal("bateria");

                // Batería igual o inferior al 40 %.
                if (bateria.compareTo(
                        BigDecimal.valueOf(40)) <= 0) {

                    String contenedor =
                            row.isNull("contenedor_id")
                                    ? ""
                                    : row.getString("contenedor_id");

                    String sensor =
                            row.isNull("sensor_id")
                                    ? ""
                                    : row.getString("sensor_id");

                    String fecha =
                            row.isNull("fecha_hora")
                                    ? ""
                                    : row.getObject("fecha_hora")
                                            .toString();

                    modelo.addRow(new Object[]{
                            contenedor,
                            sensor,
                            bateria,
                            fecha
                    });
                }
            }

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron consultar las baterías bajas.",
                    e
            );
        }
    }

    // =========================================================
    // VIBRACIONES ALTAS
    // =========================================================

    private void cargarVibracionesAltas() {

        DefaultTableModel modelo =
                (DefaultTableModel) view
                        .getTablaVibracionesAltas()
                        .getModel();

        modelo.setRowCount(0);

        try {

            for (Row row :
                    monitoreoController.obtenerTodasLasLecturas()) {

                if (row.isNull("vibracion")) {
                    continue;
                }

                BigDecimal vibracion =
                        row.getBigDecimal("vibracion");

                // Vibración superior a 1.2.
                if (vibracion.compareTo(
                        BigDecimal.valueOf(1.2)) > 0) {

                    String contenedor =
                            row.isNull("contenedor_id")
                                    ? ""
                                    : row.getString("contenedor_id");

                    String sensor =
                            row.isNull("sensor_id")
                                    ? ""
                                    : row.getString("sensor_id");

                    String fecha =
                            row.isNull("fecha_hora")
                                    ? ""
                                    : row.getObject("fecha_hora")
                                            .toString();

                    modelo.addRow(new Object[]{
                            contenedor,
                            sensor,
                            vibracion,
                            fecha
                    });
                }
            }

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron consultar las vibraciones altas.",
                    e
            );
        }
    }

    // =========================================================
    // MOSTRAR ERROR
    // =========================================================

    private void mostrarError(
            String mensaje,
            Exception e
    ) {

        JOptionPane.showMessageDialog(
                view,
                mensaje + "\n\n"
                        + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
