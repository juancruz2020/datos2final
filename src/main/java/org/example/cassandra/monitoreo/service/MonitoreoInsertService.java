package org.example.cassandra.monitoreo.service;

import com.datastax.oss.driver.api.core.CqlSession;
import org.example.conecciones.CassandraSingleton;
import org.example.cassandra.monitoreo.dao.InsertarCassandraMonitoreo;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class MonitoreoInsertService {

    private final CqlSession session;


    public MonitoreoInsertService() {
        this.session = CassandraSingleton.getInstance();
    }


    // =====================================================
    // INSERTAR LECTURA DE SENSOR
    // =====================================================

    public void insertarLecturaSensor(
            String sensorId,
            LocalDate fechaDia,
            Instant fechaHora,
            String contenedorId,
            BigDecimal temperatura,
            BigDecimal humedad,
            BigDecimal vibracion,
            BigDecimal latitud,
            BigDecimal longitud,
            BigDecimal bateria,
            String pais,
            String region) {

        validarLectura(
                sensorId,
                fechaDia,
                fechaHora,
                contenedorId,
                temperatura,
                humedad,
                vibracion,
                latitud,
                longitud,
                bateria,
                pais,
                region
        );


        InsertarCassandraMonitoreo.insertarLecturaSensor(
                session,
                sensorId,
                fechaDia,
                fechaHora,
                contenedorId,
                temperatura,
                humedad,
                vibracion,
                latitud,
                longitud,
                bateria,
                pais,
                region
        );
    }


    // =====================================================
    // VALIDACIONES
    // =====================================================

    private void validarLectura(
            String sensorId,
            LocalDate fechaDia,
            Instant fechaHora,
            String contenedorId,
            BigDecimal temperatura,
            BigDecimal humedad,
            BigDecimal vibracion,
            BigDecimal latitud,
            BigDecimal longitud,
            BigDecimal bateria,
            String pais,
            String region) {


        if (sensorId == null || sensorId.isBlank()) {
            throw new IllegalArgumentException(
                    "El sensor es obligatorio."
            );
        }


        if (fechaDia == null) {
            throw new IllegalArgumentException(
                    "La fecha es obligatoria."
            );
        }


        if (fechaHora == null) {
            throw new IllegalArgumentException(
                    "La fecha y hora son obligatorias."
            );
        }


        if (contenedorId == null || contenedorId.isBlank()) {
            throw new IllegalArgumentException(
                    "El contenedor es obligatorio."
            );
        }


        if (temperatura == null) {
            throw new IllegalArgumentException(
                    "La temperatura es obligatoria."
            );
        }


        if (humedad == null) {
            throw new IllegalArgumentException(
                    "La humedad es obligatoria."
            );
        }


        if (bateria == null) {
            throw new IllegalArgumentException(
                    "La batería es obligatoria."
            );
        }


        if (pais == null || pais.isBlank()) {
            throw new IllegalArgumentException(
                    "El país es obligatorio."
            );
        }


        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException(
                    "La región es obligatoria."
            );
        }


        if (humedad.compareTo(BigDecimal.ZERO) < 0 ||
                humedad.compareTo(new BigDecimal("100")) > 0) {

            throw new IllegalArgumentException(
                    "La humedad debe estar entre 0 y 100."
            );
        }


        if (bateria.compareTo(BigDecimal.ZERO) < 0 ||
                bateria.compareTo(new BigDecimal("100")) > 0) {

            throw new IllegalArgumentException(
                    "La batería debe estar entre 0 y 100."
            );
        }


        if (vibracion != null &&
                vibracion.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "La vibración no puede ser negativa."
            );
        }
    }
}