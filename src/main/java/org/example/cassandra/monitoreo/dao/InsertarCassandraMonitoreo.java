
package org.example.cassandra.monitoreo.dao;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.Row;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class InsertarCassandraMonitoreo {

    // =====================================================
    // INSERTAR LECTURA DE SENSOR Y SU POSICIÓN
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

        // =====================================================
        // 1. INSERTAR LECTURA COMPLETA
        // =====================================================

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

        BoundStatement lectura = session.prepare(cql).bind(
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

        // =====================================================
        // 2. INSERTAR POSICIÓN EN LA TABLA NUEVA
        // =====================================================

        String cqlPosicion = """
            INSERT INTO logistica.posiciones_sensor (
                sensor_id,
                fecha_hora,
                contenedor_id,
                latitud,
                longitud,
                pais,
                region
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        BoundStatement posicion = session.prepare(cqlPosicion).bind(
                        sensorId,
                        fechaHora,
                        contenedorId,
                        latitud,
                        longitud,
                        pais,
                        region
                );

        // =====================================================
        // 3. ACTUALIZAR MÉTRICAS
        // =====================================================

        BoundStatement metricaRegion = actualizarMetricaRegion(
                session,
                region,
                fechaDia,
                temperatura,
                humedad
        );

        BoundStatement metricaPais = actualizarMetricaPais(
                session,
                pais,
                fechaDia,
                temperatura,
                humedad
        );

        // Un batch LOGGED evita que la lectura quede guardada solo en una
        // de sus tablas/proyecciones si falla una de las escrituras.
        session.execute(BatchStatement.builder(BatchType.LOGGED)
                .addStatement(lectura)
                .addStatement(posicion)
                .addStatement(metricaRegion)
                .addStatement(metricaPais)
                .build());
    }

    // =====================================================
    // ACTUALIZAR MÉTRICA DE REGIÓN
    // =====================================================

    private static BoundStatement actualizarMetricaRegion(
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

        if (existente == null) {

            temperaturaMin = temperatura;
            temperaturaMax = temperatura;
            humedadPromedio = humedad;
            cantidadLecturas = 1;

        } else {

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

        return session.prepare(update).bind(
                        region,
                        fechaDia,
                        temperaturaMin,
                        temperaturaMax,
                        humedadPromedio,
                        cantidadLecturas
                );
    }

    // =====================================================
    // ACTUALIZAR MÉTRICA DE PAÍS
    // =====================================================

    private static BoundStatement actualizarMetricaPais(
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

        return session.prepare(update).bind(
                        pais,
                        fechaDia,
                        humedadPromedio,
                        temperaturaPromedio,
                        cantidadLecturas
                );
    }
}
