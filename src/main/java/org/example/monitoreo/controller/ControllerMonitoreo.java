package org.example.monitoreo.controller;

import com.datastax.oss.driver.api.core.cql.Row;
import org.example.monitoreo.service.MonitoreoService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ControllerMonitoreo {

    private final MonitoreoService serviceMonitoreo;


    public ControllerMonitoreo() {
        this.serviceMonitoreo =
                new MonitoreoService();
    }


    // ============================================================
    // CREAR TABLAS
    // ============================================================

    public void crearTablas() {

        serviceMonitoreo.crearTablas();

        System.out.println(
                "Tablas de monitoreo creadas correctamente."
        );
    }


    // ============================================================
    // CARGAR DATOS DE PRUEBA
    // ============================================================

    public void cargarDatosDePrueba() {

        serviceMonitoreo.cargarDatosDePrueba();

        System.out.println(
                "Datos de prueba de monitoreo cargados correctamente."
        );
    }


    // ============================================================
    // 1. HISTORIAL DE LECTURAS DE UN SENSOR
    // ============================================================

    public List<Row> obtenerHistorialSensor(
            UUID sensorId,
            LocalDate fecha) {

        return serviceMonitoreo.obtenerHistorialSensor(
                sensorId,
                fecha
        );
    }


    // ============================================================
    // 2. LECTURAS ENTRE DOS HORARIOS
    // ============================================================

    public List<Row> obtenerLecturasEntreFechas(
            UUID sensorId,
            LocalDate fecha,
            LocalDateTime desde,
            LocalDateTime hasta) {

        return serviceMonitoreo.obtenerLecturasEntreFechas(
                sensorId,
                fecha,
                desde,
                hasta
        );
    }


    // ============================================================
    // 3. TEMPERATURAS
    // ============================================================

    public List<Row> obtenerTemperaturas(
            UUID sensorId,
            LocalDate fecha) {

        return serviceMonitoreo.obtenerTemperaturas(
                sensorId,
                fecha
        );
    }


    // ============================================================
    // 4. BATERÍA
    // ============================================================

    public List<Row> obtenerBateria(
            UUID sensorId,
            LocalDate fecha) {

        return serviceMonitoreo.obtenerBateria(
                sensorId,
                fecha
        );
    }


    // ============================================================
    // 5. POSICIONES GPS
    // ============================================================

    public List<Row> obtenerGPS(
            UUID sensorId,
            LocalDate fecha) {

        return serviceMonitoreo.obtenerGPS(
                sensorId,
                fecha
        );
    }


    // ============================================================
    // 6. MÉTRICAS POR REGIÓN
    // ============================================================

    public List<Row> obtenerMetricasRegion(
            String region,
            LocalDate desde,
            LocalDate hasta) {

        return serviceMonitoreo.obtenerMetricasRegion(
                region,
                desde,
                hasta
        );
    }


    // ============================================================
    // 7. MÉTRICAS POR PAÍS
    // ============================================================

    public List<Row> obtenerMetricasPais(
            String pais,
            LocalDate desde,
            LocalDate hasta) {

        return serviceMonitoreo.obtenerMetricasPais(
                pais,
                desde,
                hasta
        );
    }
    // ============================================================
// 8. TABLA COMPLETA DE LECTURAS
// ============================================================

    public List<Row> obtenerTodasLasLecturas() {

        return serviceMonitoreo.obtenerTodasLasLecturas();
    }


// ============================================================
// 9. TABLA COMPLETA DE MÉTRICAS POR REGIÓN
// ============================================================

    public List<Row> obtenerTodasLasMetricasRegion() {

        return serviceMonitoreo.obtenerTodasLasMetricasRegion();
    }


// ============================================================
// 10. TABLA COMPLETA DE MÉTRICAS POR PAÍS
// ============================================================

    public List<Row> obtenerTodasLasMetricasPais() {

        return serviceMonitoreo.obtenerTodasLasMetricasPais();
    }
}