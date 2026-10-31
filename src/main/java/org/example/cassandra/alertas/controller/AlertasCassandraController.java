package org.example.cassandra.alertas.controller;

import com.datastax.oss.driver.api.core.cql.Row;
import org.example.cassandra.alertas.service.AlertasCassandraService;

import java.util.List;

public class AlertasCassandraController {

    private final AlertasCassandraService service = new AlertasCassandraService();

    public List<Row> obtenerTodas() {
        return service.obtenerTodas();
    }
}
