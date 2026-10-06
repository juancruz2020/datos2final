package org.example.DatosPorDefecto;

import com.datastax.oss.driver.api.core.CqlSession;
import org.example.cassandra.monitoreo.dao.InsertarCassandraMonitoreo;

public class DatosDePruebaCassandra {

    public void insertarDatos(CqlSession session) {

        // =====================================================
        // LECTURAS DE SENSORES
        // =====================================================

        InsertarCassandraMonitoreo.insertarLecturaSensor(
                session,
                java.util.UUID.fromString(
                        "11111111-1111-1111-1111-111111111111"
                ),
                java.time.LocalDate.of(2026, 9, 29),
                java.time.Instant.parse(
                        "2026-09-29T09:00:00Z"
                ),
                java.util.UUID.fromString(
                        "21111111-1111-1111-1111-111111111111"
                ),
                new java.math.BigDecimal("24.5"),
                new java.math.BigDecimal("63.2"),
                new java.math.BigDecimal("0.12"),
                new java.math.BigDecimal("-34.6037"),
                new java.math.BigDecimal("-58.3816"),
                new java.math.BigDecimal("87.5"),
                "Argentina",
                "Buenos Aires"
        );


        InsertarCassandraMonitoreo.insertarLecturaSensor(
                session,
                java.util.UUID.fromString(
                        "22222222-2222-2222-2222-222222222222"
                ),
                java.time.LocalDate.of(2026, 9, 29),
                java.time.Instant.parse(
                        "2026-09-29T10:00:00Z"
                ),
                java.util.UUID.fromString(
                        "22222222-2222-2222-2222-222222222222"
                ),
                new java.math.BigDecimal("28.7"),
                new java.math.BigDecimal("58.4"),
                new java.math.BigDecimal("0.21"),
                new java.math.BigDecimal("-32.8895"),
                new java.math.BigDecimal("-68.8458"),
                new java.math.BigDecimal("92.1"),
                "Argentina",
                "Mendoza"
        );


        // Las demás lecturas se insertan
        // de la misma manera.

        System.out.println(
                "Lecturas de prueba cargadas correctamente."
        );
    }
}