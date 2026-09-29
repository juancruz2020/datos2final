package org.example.monitoreo.dao;

import com.datastax.oss.driver.api.core.CqlSession;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class InsertarCassandra {

    // =====================================================
    // INSERTAR LECTURAS DE SENSORES
    // =====================================================

    public static void insertarLecturaSensor(
            CqlSession session,
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

        String cql = """
            INSERT INTO logistica.lecturas_sensor (
                sensor_id,
                fecha_dia,
                fecha_hora,
                contenedor_id,
                temperatura,
                humedad,
                vibracion,
                latitud,
                longitud,
                bateria,
                pais,
                region
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        session.execute(
                session.prepare(cql).bind(
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
                )
        );
    }


    // =====================================================
    // INSERTAR MÉTRICA IoT POR REGIÓN
    // =====================================================

    public static void insertarMetricaRegion(
            CqlSession session,
            String region,
            LocalDate fechaDia,
            BigDecimal temperaturaMin,
            BigDecimal temperaturaMax,
            BigDecimal humedadPromedio,
            long cantidadLecturas) {

        String cql = """
            INSERT INTO logistica.metricas_iot_region_dia (
                region,
                fecha_dia,
                temperatura_min,
                temperatura_max,
                humedad_promedio,
                cantidad_lecturas
            )
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        session.execute(
                session.prepare(cql).bind(
                        region,
                        fechaDia,
                        temperaturaMin,
                        temperaturaMax,
                        humedadPromedio,
                        cantidadLecturas
                )
        );
    }


    // =====================================================
    // INSERTAR MÉTRICA IoT POR PAÍS
    // =====================================================

    public static void insertarMetricaPais(
            CqlSession session,
            String pais,
            LocalDate fechaDia,
            BigDecimal humedadPromedio,
            BigDecimal temperaturaPromedio,
            long cantidadLecturas) {

        String cql = """
            INSERT INTO logistica.metricas_iot_pais_dia (
                pais,
                fecha_dia,
                humedad_promedio,
                temperatura_promedio,
                cantidad_lecturas
            )
            VALUES (?, ?, ?, ?, ?)
            """;

        session.execute(
                session.prepare(cql).bind(
                        pais,
                        fechaDia,
                        humedadPromedio,
                        temperaturaPromedio,
                        cantidadLecturas
                )
        );
    }
}