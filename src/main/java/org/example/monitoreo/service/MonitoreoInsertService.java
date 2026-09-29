package org.example.monitoreo.service;

import com.datastax.oss.driver.api.core.CqlSession;
import org.example.conecciones.CassandraSingleton;
import org.example.monitoreo.dao.InsertarCassandra;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class MonitoreoInsertService {

    private final CqlSession session;

    public MonitoreoInsertService() {
        this.session = CassandraSingleton.getInstance();
    }


    // =====================================================
    // INSERTAR LECTURA DE SENSOR
    // =====================================================

    public void insertarLecturaSensor(
            UUID sensorId,
            LocalDate fechaDia,
            Instant fechaHora,
            UUID contenedorId,
            BigDecimal temperatura,
            BigDecimal humedad,
            BigDecimal vibracion,
            BigDecimal latitud,
            BigDecimal longitud,
            BigDecimal bateria,
            String pais,
            String region) {

        validarLectura(
                temperatura,
                humedad,
                bateria
        );

        InsertarCassandra.insertarLecturaSensor(
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
    // INSERTAR MÉTRICA POR REGIÓN
    // =====================================================

    public void insertarMetricaRegion(
            String region,
            LocalDate fechaDia,
            BigDecimal temperaturaMin,
            BigDecimal temperaturaMax,
            BigDecimal humedadPromedio,
            long cantidadLecturas) {

        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException(
                    "La región es obligatoria."
            );
        }

        if (temperaturaMin == null ||
                temperaturaMax == null) {

            throw new IllegalArgumentException(
                    "Las temperaturas son obligatorias."
            );
        }

        if (temperaturaMin.compareTo(temperaturaMax) > 0) {

            throw new IllegalArgumentException(
                    "La temperatura mínima no puede ser mayor que la máxima."
            );
        }

        if (cantidadLecturas < 0) {

            throw new IllegalArgumentException(
                    "La cantidad de lecturas no puede ser negativa."
            );
        }

        InsertarCassandra.insertarMetricaRegion(
                session,
                region,
                fechaDia,
                temperaturaMin,
                temperaturaMax,
                humedadPromedio,
                cantidadLecturas
        );
    }


    // =====================================================
    // INSERTAR MÉTRICA POR PAÍS
    // =====================================================

    public void insertarMetricaPais(
            String pais,
            LocalDate fechaDia,
            BigDecimal humedadPromedio,
            BigDecimal temperaturaPromedio,
            long cantidadLecturas) {

        if (pais == null || pais.isBlank()) {
            throw new IllegalArgumentException(
                    "El país es obligatorio."
            );
        }

        if (cantidadLecturas < 0) {
            throw new IllegalArgumentException(
                    "La cantidad de lecturas no puede ser negativa."
            );
        }

        InsertarCassandra.insertarMetricaPais(
                session,
                pais,
                fechaDia,
                humedadPromedio,
                temperaturaPromedio,
                cantidadLecturas
        );
    }


    // =====================================================
    // VALIDACIONES DE LECTURA
    // =====================================================

    private void validarLectura(
            BigDecimal temperatura,
            BigDecimal humedad,
            BigDecimal bateria) {

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
    }
}