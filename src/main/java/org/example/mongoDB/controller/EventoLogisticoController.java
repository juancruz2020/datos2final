package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.service.EventoLogisticoService;

import java.util.List;

public class EventoLogisticoController {

    private final EventoLogisticoService eventoService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public EventoLogisticoController() {

        this.eventoService =
                new EventoLogisticoService();
    }


    // =========================================================
    // CREAR EVENTO
    // =========================================================

    public void crearEvento(
            String envioId,
            String tipoEvento,
            String ubicacion,
            String descripcion
    ) {

        eventoService.crearEvento(
                envioId,
                tipoEvento,
                ubicacion,
                descripcion
        );
    }


    // =========================================================
    // MODIFICAR EVENTO
    // =========================================================

    public void modificarEvento(
            String id,
            String envioId,
            String tipoEvento,
            String ubicacion,
            String descripcion
    ) {

        eventoService.modificarEvento(
                id,
                envioId,
                tipoEvento,
                ubicacion,
                descripcion
        );
    }


    // =========================================================
    // ELIMINAR EVENTO
    // =========================================================

    public void eliminarEvento(
            String id
    ) {

        eventoService.eliminarEvento(
                id
        );
    }


    // =========================================================
    // LISTAR EVENTOS
    // =========================================================

    public List<Document> listarEventos() {

        return eventoService
                .listarEventos();
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        return eventoService
                .buscarPorId(
                        id
                );
    }


}