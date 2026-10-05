package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.service.AlertaService;

import java.util.List;

public class AlertaController {

    private final AlertaService alertaService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AlertaController() {

        this.alertaService =
                new AlertaService();
    }


    // =========================================================
    // CREAR ALERTA
    // =========================================================

    public void crearAlerta(
            String sensorId,
            String eventoId,
            String tipo
    ) {

        alertaService.crearAlerta(
                sensorId,
                eventoId,
                tipo
        );
    }


    // =========================================================
    // LISTAR ALERTAS
    // =========================================================

    public List<Document> listarAlertas() {

        return alertaService
                .listarAlertas();
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        return alertaService
                .buscarPorId(
                        id
                );
    }


    // =========================================================
    // BUSCAR POR SENSOR
    // =========================================================

    public List<Document> buscarPorSensor(
            String sensorId
    ) {

        return alertaService
                .buscarPorSensor(
                        sensorId
                );
    }


    // =========================================================
    // BUSCAR POR EVENTO
    // =========================================================

    public List<Document> buscarPorEvento(
            String eventoId
    ) {

        return alertaService
                .buscarPorEvento(
                        eventoId
                );
    }


    // =========================================================
    // BUSCAR POR ESTADO
    // =========================================================

    public List<Document> buscarPorEstado(
            String estado
    ) {

        return alertaService
                .buscarPorEstado(
                        estado
                );
    }


    // =========================================================
    // EXISTE POR ID
    // =========================================================

    public boolean existePorId(
            String id
    ) {

        return alertaService
                .existePorId(
                        id
                );
    }


    // =========================================================
    // OBTENER TODOS LOS IDS
    // =========================================================

    public List<String> obtenerTodosLosIds() {

        return alertaService
                .obtenerTodosLosIds();
    }
}