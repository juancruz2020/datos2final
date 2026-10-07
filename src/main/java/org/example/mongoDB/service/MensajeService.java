package org.example.mongoDB.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.ConversacionMongoDAO;
import org.example.mongoDB.dao.MensajeMongoDAO;
import org.example.mongoDB.model.Mensaje;

import java.util.Date;
import java.util.List;

public class MensajeService {

    private final MensajeMongoDAO mensajeDAO;
    private final ConversacionMongoDAO conversacionDAO;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public MensajeService() {

        this.mensajeDAO =
                new MensajeMongoDAO();

        this.conversacionDAO =
                new ConversacionMongoDAO();
    }


    // =========================================================
    // ENVIAR MENSAJE
    // =========================================================

    public Mensaje enviarMensaje(
            String conversacionId,
            String remitenteId,
            String contenido
    ) {

        validarConversacionId(
                conversacionId
        );

        validarUsuarioId(
                remitenteId
        );

        validarContenido(
                contenido
        );


        Document conversacion =
                conversacionDAO.buscarPorId(
                        conversacionId
                );


        if (conversacion == null) {

            throw new IllegalArgumentException(
                    "La conversación no existe."
            );
        }


        List<String> participantes =
                conversacion.getList(
                        "participantes_ids",
                        String.class
                );


        if (
                participantes == null
                        ||
                !participantes.contains(
                        remitenteId
                )
        ) {

            throw new SecurityException(
                    "El usuario no pertenece a esta conversación."
            );
        }


        Mensaje mensaje =
                new Mensaje(
                        null,
                        conversacionId,
                        remitenteId,
                        contenido.trim(),
                        new Date()
                );


        mensajeDAO.agregar(
                mensaje
        );


        return mensaje;
    }


    // =========================================================
    // HISTORIAL DE UNA CONVERSACIÓN
    // =========================================================

    public List<Document> obtenerHistorial(
            String conversacionId
    ) {

        validarConversacionId(
                conversacionId
        );


        if (
                conversacionDAO.buscarPorId(
                        conversacionId
                ) == null
        ) {

            throw new IllegalArgumentException(
                    "La conversación no existe."
            );
        }


        return mensajeDAO
                .buscarPorConversacion(
                        conversacionId
                );
    }


    // =========================================================
    // LISTAR TODOS LOS MENSAJES
    // =========================================================

    public List<Document> listarMensajes() {

        return mensajeDAO
                .listarTodos();
    }


    // =========================================================
    // MENSAJES POR REMITENTE
    // =========================================================

    public List<Document> obtenerMensajesPorRemitente(
            String remitenteId
    ) {

        validarUsuarioId(
                remitenteId
        );


        return mensajeDAO
                .buscarPorRemitente(
                        remitenteId
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

        validarMensajeId(
                mensajeId
        );

        validarUsuarioId(
                usuarioActualId
        );

        validarContenido(
                nuevoContenido
        );


        Document mensaje =
                mensajeDAO.buscarPorId(
                        mensajeId
                );


        if (mensaje == null) {

            throw new IllegalArgumentException(
                    "El mensaje no existe."
            );
        }


        String remitenteId =
                mensaje.getString(
                        "remitente_id"
                );


        if (
                !usuarioActualId.equals(
                        remitenteId
                )
        ) {

            throw new SecurityException(
                    "No podés modificar un mensaje enviado por otro usuario."
            );
        }


        mensajeDAO.modificar(
                mensajeId,
                nuevoContenido.trim()
        );
    }


    // =========================================================
    // ELIMINAR MENSAJE
    // =========================================================

    public void eliminarMensaje(
            String mensajeId,
            String usuarioActualId
    ) {

        validarMensajeId(
                mensajeId
        );

        validarUsuarioId(
                usuarioActualId
        );


        Document mensaje =
                mensajeDAO.buscarPorId(
                        mensajeId
                );


        if (mensaje == null) {

            throw new IllegalArgumentException(
                    "El mensaje no existe."
            );
        }


        String remitenteId =
                mensaje.getString(
                        "remitente_id"
                );


        if (
                !usuarioActualId.equals(
                        remitenteId
                )
        ) {

            throw new SecurityException(
                    "No podés eliminar un mensaje enviado por otro usuario."
            );
        }


        mensajeDAO.eliminar(
                mensajeId
        );
    }


    // =========================================================
    // ELIMINAR HISTORIAL
    // =========================================================

    public void eliminarHistorial(
            String conversacionId
    ) {

        validarConversacionId(
                conversacionId
        );


        mensajeDAO
                .eliminarPorConversacion(
                        conversacionId
                );
    }


    // =========================================================
    // VALIDAR CONTENIDO
    // =========================================================

    private void validarContenido(
            String contenido
    ) {

        if (
                contenido == null
                        ||
                contenido.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "El mensaje no puede estar vacío."
            );
        }
    }


    // =========================================================
    // VALIDAR CONVERSACIÓN ID
    // =========================================================

    private void validarConversacionId(
            String conversacionId
    ) {

        if (
                conversacionId == null
                        ||
                conversacionId.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "El ID de la conversación es obligatorio."
            );
        }


        if (
                !ObjectId.isValid(
                        conversacionId
                )
        ) {

            throw new IllegalArgumentException(
                    "El ID de la conversación no es válido."
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
                usuarioId == null
                        ||
                usuarioId.isBlank()
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
    // VALIDAR MENSAJE ID
    // =========================================================

    private void validarMensajeId(
            String mensajeId
    ) {

        if (
                mensajeId == null
                        ||
                mensajeId.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "El ID del mensaje es obligatorio."
            );
        }


        if (
                !ObjectId.isValid(
                        mensajeId
                )
        ) {

            throw new IllegalArgumentException(
                    "El ID del mensaje no es válido."
            );
        }
    }
}