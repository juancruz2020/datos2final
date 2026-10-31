package org.example.cassandra.alertas.dao;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;

import java.util.ArrayList;
import java.util.List;

public class AlertasCassandraDAO {

    private final CqlSession session;

    public AlertasCassandraDAO(CqlSession session) {
        this.session = session;
    }

    public List<Row> obtenerTodas() {
        List<Row> alertas = new ArrayList<>();
        for (Row row : session.execute("SELECT id, nombre, descripcion FROM logistica.alertas")) {
            alertas.add(row);
        }
        return alertas;
    }

    public void insertar(String id, String nombre, String descripcion) {
        session.execute("""
                INSERT INTO logistica.alertas (id, nombre, descripcion)
                VALUES (?, ?, ?)
                """, id, nombre, descripcion);
    }
}
