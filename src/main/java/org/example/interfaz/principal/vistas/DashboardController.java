package org.example.interfaz.principal.vistas;

import com.datastax.oss.driver.api.core.cql.Row;
import org.example.cassandra.monitoreo.controller.ControllerMonitoreo;
import org.example.mongoDB.controller.IncidenteController;
import org.example.neo4j.controller.ControllerNeo4j;
import org.bson.Document;

import javax.swing.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.stream.Collectors;

public class DashboardController {

    private final DashboardPanel view;

    private final ControllerMonitoreo monitoreo;
    private final IncidenteController incidentes;
    private final ControllerNeo4j neo4j;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DashboardController(
            DashboardPanel view
    ) {

        this.view =
                view;

        this.monitoreo =
                new ControllerMonitoreo();

        this.incidentes = new IncidenteController();
        this.neo4j = new ControllerNeo4j();
        view.getBtnActualizar().addActionListener(e -> cargarDashboard());

        cargarDashboard();
    }


    // =========================================================
    // CARGAR DASHBOARD
    // =========================================================

    private void cargarDashboard() {

        try {

            List<Row> lecturas =
                    monitoreo.obtenerTodasLasLecturas();


            if (lecturas == null) lecturas = List.of();


            calcularIndicadores(
                    lecturas
            );

            calcularTemperaturaPorDia(
                    lecturas
            );

            calcularHumedadPorDia(
                    lecturas
            );

            calcularVibracionPorRegion(
                    lecturas
            );

            calcularIndicadoresLogisticos(lecturas);


        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudo cargar el Dashboard:\n"
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void calcularIndicadoresLogisticos(List<Row> lecturas) {
        try {
            List<Map<String, Object>> envios = neo4j.listarEnvios();
            List<Document> todosIncidentes = incidentes.listarIncidentes();
            view.actualizarEnvios(envios.size());
            view.actualizarIncidentesAbiertos(incidentes.buscarAbiertos().size());
            actualizarEstadosEnvios(envios);
            actualizarSeveridadIncidentes(todosIncidentes);
        } catch (Exception e) {
            view.actualizarEnvios(0);
            view.actualizarIncidentesAbiertos(0);
        }
        int temperaturasRiesgo = 0;
        for (Row fila : lecturas) {
            BigDecimal temperatura = fila.getBigDecimal("temperatura");
            if (temperatura != null && (temperatura.compareTo(BigDecimal.valueOf(15)) < 0
                    || temperatura.compareTo(BigDecimal.valueOf(40)) > 0)) {
                temperaturasRiesgo++;
            }
        }
        view.actualizarTemperaturasRiesgo(temperaturasRiesgo);
    }

    private void actualizarEstadosEnvios(List<Map<String, Object>> envios) {
        Map<String, Integer> cantidades = new TreeMap<>();
        for (Map<String, Object> envio : envios) {
            String estado = String.valueOf(envio.getOrDefault("estado", "SIN ESTADO"));
            cantidades.merge(estado, 1, Integer::sum);
        }
        javax.swing.table.DefaultTableModel modelo =
                (javax.swing.table.DefaultTableModel) view.getTablaEstadosEnvios().getModel();
        modelo.setRowCount(0);
        cantidades.forEach((estado, cantidad) -> modelo.addRow(new Object[]{estado, cantidad}));
    }

    private void actualizarSeveridadIncidentes(List<Document> incidentes) {
        Map<String, Integer> cantidades = new TreeMap<>();
        for (Document incidente : incidentes) {
            String severidad = incidente.getString("severidad");
            if (severidad != null && !severidad.isBlank()) cantidades.merge(severidad, 1, Integer::sum);
        }
        javax.swing.table.DefaultTableModel modelo =
                (javax.swing.table.DefaultTableModel) view.getTablaSeveridadIncidentes().getModel();
        modelo.setRowCount(0);
        cantidades.forEach((severidad, cantidad) -> modelo.addRow(new Object[]{severidad, cantidad}));
    }


    // =========================================================
    // INDICADORES
    // =========================================================

    private void calcularIndicadores(
            List<Row> lecturas
    ) {

        Set<String> sensores =
                new HashSet<>();

        Set<String> contenedores =
                new HashSet<>();


        double sumaTemperatura =
                0;

        int cantidadTemperaturas =
                0;


        Set<String> sensoresBateriaBaja =
                new HashSet<>();


        for (Row fila : lecturas) {

            String sensorId =
                    fila.getString(
                            "sensor_id"
                    );


            String contenedorId =
                    fila.getString(
                            "contenedor_id"
                    );


            if (sensorId != null) {

                sensores.add(
                        sensorId
                );
            }


            if (contenedorId != null) {

                contenedores.add(
                        contenedorId
                );
            }


            BigDecimal temperatura =
                    fila.getBigDecimal(
                            "temperatura"
                    );


            if (temperatura != null) {

                sumaTemperatura +=
                        temperatura.doubleValue();

                cantidadTemperaturas++;
            }


            BigDecimal bateria =
                    fila.getBigDecimal(
                            "bateria"
                    );


            if (
                    bateria != null
                            && bateria.compareTo(BigDecimal.valueOf(40)) <= 0
                            && sensorId != null
            ) {

                sensoresBateriaBaja.add(
                        sensorId
                );
            }
        }


        double temperaturaPromedio =
                cantidadTemperaturas > 0
                        ? sumaTemperatura
                        / cantidadTemperaturas
                        : 0;


        view.actualizarSensores(
                sensores.size()
        );


        view.actualizarContenedores(
                contenedores.size()
        );


        view.actualizarTemperatura(
                temperaturaPromedio
        );


        view.actualizarBateriaBaja(
                sensoresBateriaBaja.size()
        );
    }


    // =========================================================
    // TEMPERATURA PROMEDIO POR DÍA
    // =========================================================

    private void calcularTemperaturaPorDia(
            List<Row> lecturas
    ) {

        Map<LocalDate, List<Double>>
                temperaturas =
                new HashMap<>();


        for (Row fila : lecturas) {

            LocalDate fecha =
                    fila.getLocalDate(
                            "fecha_dia"
                    );


            BigDecimal temperatura =
                    fila.getBigDecimal(
                            "temperatura"
                    );


            if (
                    fecha == null
                            || temperatura == null
            ) {

                continue;
            }


            temperaturas
                    .computeIfAbsent(
                            fecha,
                            k -> new ArrayList<>()
                    )
                    .add(
                            temperatura.doubleValue()
                    );
        }


        List<LocalDate> fechas =
                temperaturas.keySet()
                        .stream()
                        .sorted()
                        .collect(
                                Collectors.toList()
                        );


        List<String> etiquetas =
                new ArrayList<>();

        List<Double> valores =
                new ArrayList<>();


        for (LocalDate fecha : fechas) {

            List<Double> datos =
                    temperaturas.get(
                            fecha
                    );


            double promedio =
                    datos.stream()
                            .mapToDouble(
                                    Double::doubleValue
                            )
                            .average()
                            .orElse(0);


            etiquetas.add(
                    fecha.toString()
            );

            valores.add(
                    promedio
            );
        }


        view.actualizarTemperaturas(
                etiquetas,
                valores
        );
    }


    // =========================================================
    // HUMEDAD PROMEDIO POR DÍA
    // =========================================================

    private void calcularHumedadPorDia(
            List<Row> lecturas
    ) {

        Map<LocalDate, List<Double>>
                humedades =
                new HashMap<>();


        for (Row fila : lecturas) {

            LocalDate fecha =
                    fila.getLocalDate(
                            "fecha_dia"
                    );


            BigDecimal humedad =
                    fila.getBigDecimal(
                            "humedad"
                    );


            if (
                    fecha == null
                            || humedad == null
            ) {

                continue;
            }


            humedades
                    .computeIfAbsent(
                            fecha,
                            k -> new ArrayList<>()
                    )
                    .add(
                            humedad.doubleValue()
                    );
        }


        List<LocalDate> fechas =
                humedades.keySet()
                        .stream()
                        .sorted()
                        .collect(
                                Collectors.toList()
                        );


        List<String> etiquetas =
                new ArrayList<>();

        List<Double> valores =
                new ArrayList<>();


        for (LocalDate fecha : fechas) {

            List<Double> datos =
                    humedades.get(
                            fecha
                    );


            double promedio =
                    datos.stream()
                            .mapToDouble(
                                    Double::doubleValue
                            )
                            .average()
                            .orElse(0);


            etiquetas.add(
                    fecha.toString()
            );

            valores.add(
                    promedio
            );
        }


        view.actualizarHumedades(
                etiquetas,
                valores
        );
    }


    // =========================================================
    // VIBRACIÓN PROMEDIO POR REGIÓN
    // =========================================================

    private void calcularVibracionPorRegion(
            List<Row> lecturas
    ) {

        Map<String, List<Double>>
                vibraciones =
                new HashMap<>();


        for (Row fila : lecturas) {

            String region =
                    fila.getString(
                            "region"
                    );


            BigDecimal vibracion =
                    fila.getBigDecimal(
                            "vibracion"
                    );


            if (
                    region == null
                            || vibracion == null
            ) {

                continue;
            }


            vibraciones
                    .computeIfAbsent(
                            region,
                            k -> new ArrayList<>()
                    )
                    .add(
                            vibracion.doubleValue()
                    );
        }


        List<String> regiones =
                vibraciones.keySet()
                        .stream()
                        .sorted()
                        .collect(
                                Collectors.toList()
                        );


        List<String> etiquetas =
                new ArrayList<>();

        List<Double> valores =
                new ArrayList<>();


        for (String region : regiones) {

            List<Double> datos =
                    vibraciones.get(
                            region
                    );


            double promedio =
                    datos.stream()
                            .mapToDouble(
                                    Double::doubleValue
                            )
                            .average()
                            .orElse(0);


            etiquetas.add(
                    region
            );

            valores.add(
                    promedio
            );
        }


        view.actualizarVibraciones(
                etiquetas,
                valores
        );
    }
}
