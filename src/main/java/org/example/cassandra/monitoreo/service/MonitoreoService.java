package org.example.cassandra.monitoreo.service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;

import org.example.conecciones.CassandraSingleton;
import org.example.DatosPorDefecto.CrearTablasMonitoreo;
import org.example.DatosPorDefecto.DatosDePruebaCassandra;
import org.example.cassandra.monitoreo.dao.ConsultasCassandraMonitoreo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MonitoreoService {

    private final CrearTablasMonitoreo crearTablasMonitoreo;
    private final DatosDePruebaCassandra datosDePruebaCassandra;
    private final ConsultasCassandraMonitoreo monitoreoDAO;


    public MonitoreoService() {

        this.crearTablasMonitoreo =
                new CrearTablasMonitoreo();

        this.datosDePruebaCassandra =
                new DatosDePruebaCassandra();

        CqlSession session =
                CassandraSingleton.getInstance();

        this.monitoreoDAO =
                new ConsultasCassandraMonitoreo(session);
    }





    // ============================================================
    // 1. HISTORIAL DE LECTURAS
    // ============================================================

    public List<Row> obtenerHistorialSensor(
            String sensorId,
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
            String sensorId,
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
            String sensorId,
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
            String sensorId,
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
            String sensorId,
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


    // ============================================================
    // 8. TODAS LAS LECTURAS
    // ============================================================

    public List<Row> obtenerTodasLasLecturas() {

        return monitoreoDAO.obtenerTodasLasLecturas();
    }


    // ============================================================
    // 9. TODAS LAS MÉTRICAS POR REGIÓN
    // ============================================================

    public List<Row> obtenerTodasLasMetricasRegion() {

        return monitoreoDAO.obtenerTodasLasMetricasRegion();
    }


    // ============================================================
    // 10. TODAS LAS MÉTRICAS POR PAÍS
    // ============================================================

    public List<Row> obtenerTodasLasMetricasPais() {

        return monitoreoDAO.obtenerTodasLasMetricasPais();
    }

    // ============================================================
    // 11. POSICIONES GPS DE TODOS LOS SENSORES
    // ============================================================

    public List<Row> obtenerTodasLasPosicionesGPS() {

        return monitoreoDAO.obtenerTodasLasPosicionesGPS();
    }
}