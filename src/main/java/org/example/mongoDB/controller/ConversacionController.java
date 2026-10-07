package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.model.Conversacion;
import org.example.mongoDB.service.ConversacionService;

import java.util.List;

public class ConversacionController {

    private final ConversacionService conversacionService;

    public ConversacionController() {
        this.conversacionService = new ConversacionService();
    }


    // =========================================================
    // CREAR CONVERSACION PRIVADA
    // =========================================================

    public Conversacion crearConversacionPrivada(
            String nombre,
            String usuario1Id,
            String usuario2Id
    ) {

        return conversacionService
                .crearConversacionPrivada(
                        nombre,
                        usuario1Id,
                        usuario2Id
                );
    }


    // =========================================================
    // CREAR CONVERSACION GRUPAL
    // =========================================================

    public Conversacion crearConversacionGrupal(
            String nombre,
            List<String> participantesIds
    ) {

        return conversacionService
                .crearConversacionGrupal(
                        nombre,
                        participantesIds
                );
    }


    // =========================================================
    // LISTAR TODAS
    // =========================================================

    public List<Document> listarConversaciones() {

        return conversacionService
                .listarConversaciones();
    }


    // =========================================================
    // CONVERSACIONES DE UN USUARIO
    // =========================================================

    public List<Document> obtenerConversacionesUsuario(
            String usuarioId
    ) {

        return conversacionService
                .obtenerConversacionesUsuario(
                        usuarioId
                );
    }


    // =========================================================
    // CONVERSACIONES PRIVADAS
    // =========================================================

    public List<Document> obtenerConversacionesPrivadas() {

        return conversacionService
                .obtenerConversacionesPrivadas();
    }


    // =========================================================
    // CONVERSACIONES GRUPALES
    // =========================================================

    public List<Document> obtenerConversacionesGrupales() {

        return conversacionService
                .obtenerConversacionesGrupales();
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        return conversacionService
                .buscarPorId(id);
    }


    // =========================================================
    // MODIFICAR
    // =========================================================

    public void modificarConversacion(
            String id,
            String nombre,
            String tipo,
            List<String> participantesIds
    ) {

        conversacionService
                .modificarConversacion(
                        id,
                        nombre,
                        tipo,
                        participantesIds
                );
    }


    // =========================================================
    // ELIMINAR
    // =========================================================

    public void eliminarConversacion(
            String id
    ) {

        conversacionService
                .eliminarConversacion(id);
    }
}