package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.model.Mensaje;
import org.example.mongoDB.service.MensajeService;

import java.util.List;

class MensajeController {

    private final MensajeService mensajeService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    MensajeController() {

        this.mensajeService =
                new MensajeService();
    }


    // =========================================================
    // ENVIAR MENSAJE
    // =========================================================

    public Mensaje enviarMensaje(
            String conversacionId,
            String remitenteId,
            String contenido
    ) {

        return mensajeService
                .enviarMensaje(
                        conversacionId,
                        remitenteId,
                        contenido
                );
    }


    // =========================================================
    // OBTENER HISTORIAL
    // =========================================================

    public List<Document> obtenerHistorial(
            String conversacionId
    ) {

        return mensajeService
                .obtenerHistorial(
                        conversacionId
                );
    }


    // =========================================================
    // MODIFICAR MENSAJE
    // =========================================================

    public void modificarMensaje(
            String mensajeId,
            String usuarioActualId,
            String nuevoContenido
    ) {

        mensajeService
                .modificarMensaje(
                        mensajeId,
                        usuarioActualId,
                        nuevoContenido
                );
    }


    // =========================================================
    // ELIMINAR MENSAJE
    // =========================================================

    public void eliminarMensaje(
            String mensajeId,
            String usuarioActualId
    ) {

        mensajeService
                .eliminarMensaje(
                        mensajeId,
                        usuarioActualId
                );
    }


}