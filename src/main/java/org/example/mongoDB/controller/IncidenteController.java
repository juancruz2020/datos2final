package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.service.IncidenteService;

import java.util.List;

public class IncidenteController {

    private final IncidenteService incidenteService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public IncidenteController() {

        this.incidenteService =
                new IncidenteService();
    }


    // =========================================================
    // CREAR INCIDENTE
    // =========================================================

    public void crearIncidente(
            String envioId,
            String tipo,
            String severidad,
            String descripcion
    ) {

        incidenteService.crearIncidente(
                envioId,
                tipo,
                severidad,
                descripcion
        );
    }


    // =========================================================
    // LISTAR INCIDENTES
    // =========================================================

    public List<Document> listarIncidentes() {

        return incidenteService
                .listarIncidentes();
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        return incidenteService
                .buscarPorId(
                        id
                );
    }


    // =========================================================
    // BUSCAR POR ENVÍO
    // =========================================================

    public List<Document> buscarPorEnvio(
            String envioId
    ) {

        return incidenteService
                .buscarPorEnvio(
                        envioId
                );
    }


    // =========================================================
    // BUSCAR INCIDENTES ABIERTOS
    // =========================================================

    public List<Document> buscarAbiertos() {

        return incidenteService
                .buscarAbiertos();
    }


    // =========================================================
    // EXISTE POR ID
    // =========================================================

    public boolean existePorId(
            String id
    ) {

        return incidenteService
                .existePorId(
                        id
                );
    }


    // =========================================================
    // OBTENER TODOS LOS IDS
    // =========================================================

    public List<String> obtenerTodosLosIds() {

        return incidenteService
                .obtenerTodosLosIds();
    }
}