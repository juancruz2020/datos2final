package org.example.cassandra.monitoreo.dao;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class InsertarCassandraMonitoreo {

    // =====================================================
    // INSERTAR LECTURA DE SENSOR
    // =====================================================

    public static void insertarLecturaSensor(
            CqlSession session,
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

        // Después de guardar la lectura,
        // actualizamos automáticamente las métricas.

        actualizarMetricaRegion(
                session,
                region,
                fechaDia,
                temperatura,
                humedad
        );

        actualizarMetricaPais(
                session,
                pais,
                fechaDia,
                temperatura,
                humedad
        );
    }


    // =====================================================
    // ACTUALIZAR MÉTRICA DE REGIÓN
    // =====================================================

    private static void actualizarMetricaRegion(
            CqlSession session,
            String region,
            LocalDate fechaDia,
            BigDecimal temperatura,
            BigDecimal humedad) {

        String select = """
            SELECT temperatura_min,
                   temperatura_max,
                   humedad_promedio,
                   cantidad_lecturas
            FROM logistica.metricas_iot_region_dia
            WHERE region = ?
            AND fecha_dia = ?
            """;

        Row existente = session.execute(
                session.prepare(select).bind(
                        region,
                        fechaDia
                )
        ).one();

        BigDecimal temperaturaMin;
        BigDecimal temperaturaMax;
        BigDecimal humedadPromedio;
        long cantidadLecturas;

        // -------------------------------------------------
        // PRIMERA LECTURA DE ESA REGIÓN EN ESE DÍA
        // -------------------------------------------------

        if (existente == null) {

            temperaturaMin = temperatura;
            temperaturaMax = temperatura;
            humedadPromedio = humedad;
            cantidadLecturas = 1;

        } else {

            // -------------------------------------------------
            // YA EXISTEN LECTURAS
            // -------------------------------------------------

            BigDecimal minAnterior =
                    existente.getBigDecimal("temperatura_min");

            BigDecimal maxAnterior =
                    existente.getBigDecimal("temperatura_max");

            BigDecimal promedioAnterior =
                    existente.getBigDecimal("humedad_promedio");

            long cantidadAnterior =
                    existente.getLong("cantidad_lecturas");

            temperaturaMin =
                    temperatura.compareTo(minAnterior) < 0
                            ? temperatura
                            : minAnterior;

            temperaturaMax =
                    temperatura.compareTo(maxAnterior) > 0
                            ? temperatura
                            : maxAnterior;

            humedadPromedio =
                    promedioAnterior
                            .multiply(BigDecimal.valueOf(cantidadAnterior))
                            .add(humedad)
                            .divide(
                                    BigDecimal.valueOf(cantidadAnterior + 1),
                                    4,
                                    java.math.RoundingMode.HALF_UP
                            );

            cantidadLecturas = cantidadAnterior + 1;
        }

        String update = """
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
                session.prepare(update).bind(
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
    // ACTUALIZAR MÉTRICA DE PAÍS
    // =====================================================

    private static void actualizarMetricaPais(
            CqlSession session,
            String pais,
            LocalDate fechaDia,
            BigDecimal temperatura,
            BigDecimal humedad) {

        String select = """
            SELECT humedad_promedio,
                   temperatura_promedio,
                   cantidad_lecturas
            FROM logistica.metricas_iot_pais_dia
            WHERE pais = ?
            AND fecha_dia = ?
            """;

        Row existente = session.execute(
                session.prepare(select).bind(
                        pais,
                        fechaDia
                )
        ).one();

        BigDecimal humedadPromedio;
        BigDecimal temperaturaPromedio;
        long cantidadLecturas;

        // -------------------------------------------------
        // PRIMERA LECTURA DEL PAÍS EN ESE DÍA
        // -------------------------------------------------

        if (existente == null) {

            humedadPromedio = humedad;
            temperaturaPromedio = temperatura;
            cantidadLecturas = 1;

        } else {

            BigDecimal humedadAnterior =
                    existente.getBigDecimal("humedad_promedio");

            BigDecimal temperaturaAnterior =
                    existente.getBigDecimal("temperatura_promedio");

            long cantidadAnterior =
                    existente.getLong("cantidad_lecturas");

            humedadPromedio =
                    humedadAnterior
                            .multiply(BigDecimal.valueOf(cantidadAnterior))
                            .add(humedad)
                            .divide(
                                    BigDecimal.valueOf(cantidadAnterior + 1),
                                    4,
                                    java.math.RoundingMode.HALF_UP
                            );

            temperaturaPromedio =
                    temperaturaAnterior
                            .multiply(BigDecimal.valueOf(cantidadAnterior))
                            .add(temperatura)
                            .divide(
                                    BigDecimal.valueOf(cantidadAnterior + 1),
                                    4,
                                    java.math.RoundingMode.HALF_UP
                            );

            cantidadLecturas = cantidadAnterior + 1;
        }

        String update = """
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
                session.prepare(update).bind(
                        pais,
                        fechaDia,
                        humedadPromedio,
                        temperaturaPromedio,
                        cantidadLecturas
                )
        );
    }
}