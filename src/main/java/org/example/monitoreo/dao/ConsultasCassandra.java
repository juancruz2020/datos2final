package org.example.monitoreo.dao;


import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ConsultasCassandra {

    private final CqlSession session;

    public ConsultasCassandra(CqlSession session) {
        this.session = session;
    }

    // ============================================================
    // 1. HISTORIAL DE LECTURAS DE UN SENSOR
    // ============================================================

    public List<Row> obtenerLecturasSensor(
            UUID sensorId,
            LocalDate fechaDia) {

        String cql = """
            SELECT *
            FROM logistica.lecturas_sensor
            WHERE sensor_id = ?
            AND fecha_dia = ?
            """;

        PreparedStatement statement =
                session.prepare(cql);

        ResultSet result = session.execute(
                statement.bind(
                        sensorId,
                        fechaDia
                )
        );

        List<Row> lecturas = new ArrayList<>();

        for (Row row : result) {
            lecturas.add(row);
        }

        return lecturas;
    }


    // ============================================================
    // 2. LECTURAS ENTRE DOS HORARIOS
    // ============================================================

    public List<Row> obtenerLecturasEntreFechas(
            UUID sensorId,
            LocalDate fechaDia,
            LocalDateTime desde,
            LocalDateTime hasta) {

        String cql = """
            SELECT *
            FROM logistica.lecturas_sensor
            WHERE sensor_id = ?
            AND fecha_dia = ?
            AND fecha_hora >= ?
            AND fecha_hora <= ?
            """;

        PreparedStatement statement =
                session.prepare(cql);

        ResultSet result = session.execute(
                statement.bind(
                        sensorId,
                        fechaDia,
                        desde,
                        hasta
                )
        );

        List<Row> lecturas = new ArrayList<>();

        for (Row row : result) {
            lecturas.add(row);
        }

        return lecturas;
    }


    // ============================================================
    // 3. TEMPERATURA HISTÓRICA
    // ============================================================

    public List<Row> obtenerTemperaturas(
            UUID sensorId,
            LocalDate fechaDia) {

        String cql = """
            SELECT fecha_hora, temperatura
            FROM logistica.lecturas_sensor
            WHERE sensor_id = ?
            AND fecha_dia = ?
            """;

        PreparedStatement statement =
                session.prepare(cql);

        ResultSet result = session.execute(
                statement.bind(
                        sensorId,
                        fechaDia
                )
        );

        List<Row> temperaturas = new ArrayList<>();

        for (Row row : result) {
            temperaturas.add(row);
        }

        return temperaturas;
    }


    // ============================================================
    // 4. BATERÍA HISTÓRICA
    // ============================================================

    public List<Row> obtenerBateria(
            UUID sensorId,
            LocalDate fechaDia) {

        String cql = """
            SELECT fecha_hora, bateria
            FROM logistica.lecturas_sensor
            WHERE sensor_id = ?
            AND fecha_dia = ?
            """;

        PreparedStatement statement =
                session.prepare(cql);

        ResultSet result = session.execute(
                statement.bind(
                        sensorId,
                        fechaDia
                )
        );

        List<Row> bateria = new ArrayList<>();

        for (Row row : result) {
            bateria.add(row);
        }

        return bateria;
    }


    // ============================================================
    // 5. POSICIÓN GPS
    // ============================================================

    public List<Row> obtenerPosicionesGPS(
            UUID sensorId,
            LocalDate fechaDia) {

        String cql = """
            SELECT fecha_hora, latitud, longitud
            FROM logistica.lecturas_sensor
            WHERE sensor_id = ?
            AND fecha_dia = ?
            """;

        PreparedStatement statement =
                session.prepare(cql);

        ResultSet result = session.execute(
                statement.bind(
                        sensorId,
                        fechaDia
                )
        );

        List<Row> posiciones = new ArrayList<>();

        for (Row row : result) {
            posiciones.add(row);
        }

        return posiciones;
    }


    // ============================================================
    // 6. MÉTRICAS DE UNA REGIÓN
    // ============================================================

    public List<Row> obtenerMetricasRegion(
            String region,
            LocalDate desde,
            LocalDate hasta) {

        String cql = """
            SELECT fecha_dia,
                   temperatura_min,
                   temperatura_max,
                   humedad_promedio,
                   cantidad_lecturas
            FROM logistica.metricas_iot_region_dia
            WHERE region = ?
            AND fecha_dia >= ?
            AND fecha_dia <= ?
            """;

        PreparedStatement statement =
                session.prepare(cql);

        ResultSet result = session.execute(
                statement.bind(
                        region,
                        desde,
                        hasta
                )
        );

        List<Row> metricas = new ArrayList<>();

        for (Row row : result) {
            metricas.add(row);
        }

        return metricas;
    }


    // ============================================================
    // 7. MÉTRICAS DE UN PAÍS
    // ============================================================

    public List<Row> obtenerMetricasPais(
            String pais,
            LocalDate desde,
            LocalDate hasta) {

        String cql = """
            SELECT fecha_dia,
                   humedad_promedio,
                   temperatura_promedio,
                   cantidad_lecturas
            FROM logistica.metricas_iot_pais_dia
            WHERE pais = ?
            AND fecha_dia >= ?
            AND fecha_dia <= ?
            """;

        PreparedStatement statement =
                session.prepare(cql);

        ResultSet result = session.execute(
                statement.bind(
                        pais,
                        desde,
                        hasta
                )
        );

        List<Row> metricas = new ArrayList<>();

        for (Row row : result) {
            metricas.add(row);
        }

        return metricas;
    }
}

