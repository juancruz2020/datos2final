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


}