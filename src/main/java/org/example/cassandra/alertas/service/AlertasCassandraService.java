package org.example.cassandra.alertas.service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import org.example.cassandra.alertas.dao.AlertasCassandraDAO;
import org.example.conecciones.CassandraSingleton;
import org.example.consola.TablaConsola;

import java.util.List;
import java.util.UUID;

public class AlertasCassandraService {

    private final AlertasCassandraDAO alertasDAO;

    public AlertasCassandraService() {
        CqlSession session = CassandraSingleton.getInstance();
        this.alertasDAO = new AlertasCassandraDAO(session);
    }

    public List<Row> obtenerTodas() {
        return TablaConsola.mostrar(
                "AlertasCassandraService.obtenerTodas",
                alertasDAO.obtenerTodas()
        );
    }

    public void generarAlerta(String nombre, String descripcion) {
        if (nombre == null || nombre.isBlank() || descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("El nombre y la descripción de la alerta son obligatorios.");
        }
        alertasDAO.insertar(UUID.randomUUID().toString(), nombre.trim(), descripcion.trim());
    }
}
