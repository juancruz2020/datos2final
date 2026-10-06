package org.example.DatosPorDefecto;

import com.datastax.oss.driver.api.core.CqlSession;
import org.example.conecciones.CassandraSingleton;

public class CrearTablasMonitoreo {

    public static void crearTablas() {

        // =====================================================
        // OBTENER SESIÓN DE CASSANDRA
        // =====================================================

        CqlSession session = CassandraSingleton.getInstance();


        // =====================================================
        // LECTURAS DE SENSORES
        // =====================================================

        session.execute("""
            CREATE TABLE IF NOT EXISTS logistica.lecturas_sensor (
                sensor_id text,
                fecha_dia date,
                fecha_hora timestamp,
                contenedor_id text,

                temperatura decimal,
                humedad decimal,
                vibracion decimal,

                latitud decimal,
                longitud decimal,

                bateria decimal,

                pais text,
                region text,

                PRIMARY KEY ((sensor_id, fecha_dia), fecha_hora)
            )
            WITH CLUSTERING ORDER BY (fecha_hora DESC)
            """);


        // =====================================================
        // MÉTRICAS IoT POR REGIÓN
        // =====================================================

        session.execute("""
            CREATE TABLE IF NOT EXISTS logistica.metricas_iot_region_dia (
                region text,
                fecha_dia date,

                temperatura_min decimal,
                temperatura_max decimal,

                humedad_promedio decimal,

                cantidad_lecturas bigint,

                PRIMARY KEY ((region), fecha_dia)
            )
            WITH CLUSTERING ORDER BY (fecha_dia DESC)
            """);


        // =====================================================
        // MÉTRICAS IoT POR PAÍS
        // =====================================================

        session.execute("""
            CREATE TABLE IF NOT EXISTS logistica.metricas_iot_pais_dia (
                pais text,
                fecha_dia date,

                humedad_promedio decimal,
                temperatura_promedio decimal,

                cantidad_lecturas bigint,

                PRIMARY KEY ((pais), fecha_dia)
            )
            WITH CLUSTERING ORDER BY (fecha_dia DESC)
            """);


        System.out.println(
                "Tablas de monitoreo creadas/verificadas correctamente."
        );
    }
}