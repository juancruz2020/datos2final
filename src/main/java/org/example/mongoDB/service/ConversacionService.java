package org.example.mongoDB.service;

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
    // LISTAR TODAS
    // =========================================================

    public List<Document> listarConversaciones() {

        return conversacionDAO
                .listarTodos();
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

        return conversacionDAO
                .buscarPorUsuario(
                        usuarioId
                );
    }


    // =========================================================
    // CONVERSACIONES PRIVADAS
    // =========================================================

    public List<Document> obtenerConversacionesPrivadas() {

        return conversacionDAO
                .buscarPorTipo(
                        "PRIVADA"
                );
    }


    // =========================================================
    // CONVERSACIONES GRUPALES
    // =========================================================

    public List<Document> obtenerConversacionesGrupales() {

        return conversacionDAO
                .buscarPorTipo(
                        "GRUPAL"
                );
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        validarConversacionId(
                id
        );

        Document conversacion =
                conversacionDAO
                        .buscarPorId(id);

        if (conversacion == null) {
            throw new IllegalArgumentException(
                    "La conversación no existe."
            );
        }

        return conversacion;
    }


    // =========================================================
    // MODIFICAR CONVERSACION
    // =========================================================

    public void modificarConversacion(
            String id,
            String nombre,
            String tipo,
            List<String> participantesIds
    ) {

        validarConversacionId(id);
        validarNombre(nombre);

        if (
                tipo == null ||
                (
                        !tipo.equals("PRIVADA") &&
                        !tipo.equals("GRUPAL")
                )
        ) {
            throw new IllegalArgumentException(
                    "El tipo debe ser PRIVADA o GRUPAL."
            );
        }

        if (
                participantesIds == null ||
                participantesIds.size() < 2
        ) {
            throw new IllegalArgumentException(
                    "La conversación debe tener al menos dos participantes."
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

        if (
                tipo.equals("PRIVADA") &&
                participantesIds.size() != 2
        ) {
            throw new IllegalArgumentException(
                    "Una conversación privada debe tener exactamente dos participantes."
            );
        }

        Conversacion conversacion =
                new Conversacion(
                        id,
                        nombre.trim(),
                        tipo,
                        participantesIds,
                        null
                );

        conversacionDAO.modificar(
                conversacion
        );
    }


    // =========================================================
    // ELIMINAR
    // =========================================================

    public void eliminarConversacion(
            String id
    ) {

        validarConversacionId(id);

        if (
                !conversacionDAO
                        .existePorId(id)
        ) {
            throw new IllegalArgumentException(
                    "La conversación no existe."
            );
        }

        conversacionDAO.eliminar(
                id
        );
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


    // =========================================================
    // VALIDAR CONVERSACION ID
    // =========================================================

    private void validarConversacionId(
            String id
    ) {

        if (
                id == null ||
                id.trim().isEmpty()
        ) {
            throw new IllegalArgumentException(
                    "El ID de la conversación es obligatorio."
            );
        }

        if (
                !ObjectId.isValid(id)
        ) {
            throw new IllegalArgumentException(
                    "El ID de la conversación no es válido."
            );
        }
    }
}