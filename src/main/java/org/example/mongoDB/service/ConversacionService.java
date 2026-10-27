package org.example.mongoDB.service;

import org.example.consola.TablaConsola;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.ConversacionMongoDAO;
import org.example.mongoDB.model.Conversacion;

import java.util.Date;
import java.util.List;

public class ConversacionService {

    private final ConversacionMongoDAO conversacionDAO;

    public ConversacionService() {
        this.conversacionDAO = new ConversacionMongoDAO();
    }


    // =========================================================
    // CREAR CONVERSACION PRIVADA
    // =========================================================

    public Conversacion crearConversacionPrivada(
            String nombre,
            String usuario1Id,
            String usuario2Id
    ) {

        validarNombre(nombre);
        validarUsuarioId(usuario1Id);
        validarUsuarioId(usuario2Id);

        if (usuario1Id.equals(usuario2Id)) {
            throw new IllegalArgumentException(
                    "Una conversación privada debe tener dos usuarios diferentes."
            );
        }

        List<String> participantes =
                List.of(
                        usuario1Id,
                        usuario2Id
                );

        Conversacion conversacion =
                new Conversacion(
                        null,
                        nombre.trim(),
                        "PRIVADA",
                        participantes,
                        new Date()
                );

        conversacionDAO.agregar(
                conversacion
        );

        return conversacion;
    }


    // =========================================================
    // CREAR CONVERSACION GRUPAL
    // =========================================================

    public Conversacion crearConversacionGrupal(
            String nombre,
            List<String> participantesIds
    ) {

        validarNombre(nombre);

        if (
                participantesIds == null ||
                participantesIds.size() < 2
        ) {
            throw new IllegalArgumentException(
                    "Una conversación grupal debe tener al menos dos participantes."
            );
        }

        for (
                String usuarioId :
                participantesIds
        ) {
            validarUsuarioId(
                    usuarioId
            );
        }

        long cantidadUsuariosDiferentes =
                participantesIds
                        .stream()
                        .distinct()
                        .count();

        if (
                cantidadUsuariosDiferentes
                        != participantesIds.size()
        ) {
            throw new IllegalArgumentException(
                    "No puede haber participantes repetidos."
            );
        }

        Conversacion conversacion =
                new Conversacion(
                        null,
                        nombre.trim(),
                        "GRUPAL",
                        participantesIds,
                        new Date()
                );

        conversacionDAO.agregar(
                conversacion
        );

        return conversacion;
    }


    // =========================================================
    // CONVERSACIONES DE UN USUARIO
    // =========================================================

    public List<Document> obtenerConversacionesUsuario(
            String usuarioId
    ) {

        validarUsuarioId(
                usuarioId
        );

        return TablaConsola.mostrar("ConversacionService.obtenerConversacionesUsuario", conversacionDAO
                .buscarPorUsuario(
                        usuarioId
                ));
    }


    // =========================================================
    // VALIDAR NOMBRE
    // =========================================================

    private void validarNombre(
            String nombre
    ) {

        if (
                nombre == null ||
                nombre.trim().isEmpty()
        ) {
            throw new IllegalArgumentException(
                    "El nombre de la conversación es obligatorio."
            );
        }
    }


    // =========================================================
    // VALIDAR USUARIO ID
    // =========================================================

    private void validarUsuarioId(
            String usuarioId
    ) {

        if (
                usuarioId == null ||
                usuarioId.trim().isEmpty()
        ) {
            throw new IllegalArgumentException(
                    "El ID del usuario es obligatorio."
            );
        }

        if (
                !ObjectId.isValid(
                        usuarioId
                )
        ) {
            throw new IllegalArgumentException(
                    "El ID del usuario no es válido."
            );
        }
    }


}