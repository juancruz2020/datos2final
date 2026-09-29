package org.example.monitoreo.service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;

import org.example.conecciones.CassandraSingleton;
import org.example.monitoreo.dao.CrearTablas;
import org.example.monitoreo.dao.DatosDePrueba;
import org.example.monitoreo.dao.ConsultasCassandra;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class MonitoreoService {

    private final CrearTablas crearTablas;
    private final DatosDePrueba datosDePrueba;
    private final ConsultasCassandra monitoreoDAO;


    public MonitoreoService() {

        this.crearTablas = new CrearTablas();
        this.datosDePrueba = new DatosDePrueba();

        CqlSession session = CassandraSingleton.getInstance();

        this.monitoreoDAO = new ConsultasCassandra(session);
    }


    // ============================================================
    // CREAR TABLAS
    // ============================================================

    public void crearTablas() {

        CqlSession session = CassandraSingleton.getInstance();

        crearTablas.crearTablas(session);
    }


    // ============================================================
    // CARGAR DATOS DE PRUEBA
    // ============================================================

    public void cargarDatosDePrueba() {

        CqlSession session = CassandraSingleton.getInstance();

        datosDePrueba.insertarDatos(session);
    }


    // ============================================================
    // 1. HISTORIAL DE LECTURAS DE UN SENSOR
    // ============================================================

    public List<Row> obtenerHistorialSensor(
            UUID sensorId,
            LocalDate fecha) {

        return monitoreoDAO.obtenerLecturasSensor(
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

        if (desde.isAfter(hasta)) {

            throw new IllegalArgumentException(
                    "La fecha inicial no puede ser posterior a la fecha final."
            );
        }

        return monitoreoDAO.obtenerLecturasEntreFechas(
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

        return monitoreoDAO.obtenerTemperaturas(
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

        return monitoreoDAO.obtenerBateria(
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

        return monitoreoDAO.obtenerPosicionesGPS(
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

        return monitoreoDAO.obtenerMetricasRegion(
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

        return monitoreoDAO.obtenerMetricasPais(
                pais,
                desde,
                hasta
        );
    }
}