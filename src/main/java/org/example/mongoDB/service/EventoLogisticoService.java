package org.example.mongoDB.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.EnvioMongoDAO;
import org.example.mongoDB.dao.EventoLogisticoMongoDAO;
import org.example.mongoDB.model.EventoLogistico;

import java.util.Date;
import java.util.List;

public class EventoLogisticoService {

    private final EventoLogisticoMongoDAO eventoDAO;
    private final EnvioMongoDAO envioDAO;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public EventoLogisticoService() {

        this.eventoDAO =
                new EventoLogisticoMongoDAO();

        this.envioDAO =
                new EnvioMongoDAO();
    }


    // =========================================================
    // CREAR EVENTO LOGÍSTICO
    // =========================================================

    public void crearEvento(
            String envioId,
            String tipoEvento,
            String ubicacion,
            String descripcion
    ) {

        // -----------------------------------------------------
        // VALIDAR ENVÍO
        // -----------------------------------------------------

        validarObjectId(
                envioId,
                "El ID del envío no es válido."
        );

        if (!envioDAO.existePorId(envioId)) {

            throw new IllegalArgumentException(
                    "El envío seleccionado no existe."
            );
        }


        // -----------------------------------------------------
        // VALIDAR TIPO
        // -----------------------------------------------------

        if (tipoEvento == null
                || tipoEvento.isBlank()) {

            throw new IllegalArgumentException(
                    "El tipo de evento es obligatorio."
            );
        }


        // -----------------------------------------------------
        // VALIDAR UBICACIÓN
        // -----------------------------------------------------

        if (ubicacion == null
                || ubicacion.isBlank()) {

            throw new IllegalArgumentException(
                    "La ubicación es obligatoria."
            );
        }


        // -----------------------------------------------------
        // VALIDAR DESCRIPCIÓN
        // -----------------------------------------------------

        if (descripcion == null
                || descripcion.isBlank()) {

            throw new IllegalArgumentException(
                    "La descripción es obligatoria."
            );
        }


        // -----------------------------------------------------
        // CREAR EVENTO
        // -----------------------------------------------------

        EventoLogistico evento =
                new EventoLogistico(
                        null,
                        envioId,
                        new Date(),
                        tipoEvento.trim(),
                        ubicacion.trim(),
                        descripcion.trim()
                );


        eventoDAO.agregar(
                evento
        );
    }


    // =========================================================
    // MODIFICAR EVENTO LOGÍSTICO
    // =========================================================

    public void modificarEvento(
            String id,
            String envioId,
            String tipoEvento,
            String ubicacion,
            String descripcion
    ) {

        // -----------------------------------------------------
        // VALIDAR EVENTO
        // -----------------------------------------------------

        validarObjectId(
                id,
                "El ID del evento no es válido."
        );


        Document existente =
                eventoDAO.buscarPorId(
                        id
                );


        if (existente == null) {

            throw new IllegalArgumentException(
                    "El evento logístico no existe."
            );
        }


        // -----------------------------------------------------
        // VALIDAR ENVÍO
        // -----------------------------------------------------

        validarObjectId(
                envioId,
                "El ID del envío no es válido."
        );


        if (!envioDAO.existePorId(envioId)) {

            throw new IllegalArgumentException(
                    "El envío seleccionado no existe."
            );
        }


        // -----------------------------------------------------
        // VALIDAR TIPO
        // -----------------------------------------------------

        if (tipoEvento == null
                || tipoEvento.isBlank()) {

            throw new IllegalArgumentException(
                    "El tipo de evento es obligatorio."
            );
        }


        // -----------------------------------------------------
        // VALIDAR UBICACIÓN
        // -----------------------------------------------------

        if (ubicacion == null
                || ubicacion.isBlank()) {

            throw new IllegalArgumentException(
                    "La ubicación es obligatoria."
            );
        }


        // -----------------------------------------------------
        // VALIDAR DESCRIPCIÓN
        // -----------------------------------------------------

        if (descripcion == null
                || descripcion.isBlank()) {

            throw new IllegalArgumentException(
                    "La descripción es obligatoria."
            );
        }


        // -----------------------------------------------------
        // CONSERVAR FECHA ORIGINAL
        // -----------------------------------------------------

        Date fechaHora =
                existente.getDate(
                        "fecha_hora"
                );


        // -----------------------------------------------------
        // CREAR OBJETO MODIFICADO
        // -----------------------------------------------------

        EventoLogistico evento =
                new EventoLogistico(
                        id,
                        envioId,
                        fechaHora,
                        tipoEvento.trim(),
                        ubicacion.trim(),
                        descripcion.trim()
                );


        // -----------------------------------------------------
        // ACTUALIZAR EN MONGODB
        // -----------------------------------------------------

        eventoDAO.modificar(
                evento
        );
    }


    // =========================================================
    // ELIMINAR EVENTO LOGÍSTICO
    // =========================================================

    public void eliminarEvento(
            String id
    ) {

        // -----------------------------------------------------
        // VALIDAR ID
        // -----------------------------------------------------

        validarObjectId(
                id,
                "El ID del evento no es válido."
        );


        // -----------------------------------------------------
        // VERIFICAR EXISTENCIA
        // -----------------------------------------------------

        if (!eventoDAO.existePorId(id)) {

            throw new IllegalArgumentException(
                    "El evento logístico no existe."
            );
        }


        // -----------------------------------------------------
        // ELIMINAR
        // -----------------------------------------------------

        eventoDAO.eliminar(
                id
        );
    }


    // =========================================================
    // LISTAR EVENTOS
    // =========================================================

    public List<Document> listarEventos() {

        return eventoDAO.listarTodos();
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        validarObjectId(
                id,
                "El ID del evento no es válido."
        );


        Document evento =
                eventoDAO.buscarPorId(
                        id
                );


        if (evento == null) {

            throw new IllegalArgumentException(
                    "El evento logístico no existe."
            );
        }


        return evento;
    }


    // =========================================================
    // BUSCAR POR ENVÍO
    // =========================================================

    public List<Document> buscarPorEnvio(
            String envioId
    ) {

        validarObjectId(
                envioId,
                "El ID del envío no es válido."
        );


        return eventoDAO.buscarPorEnvio(
                envioId
        );
    }


    // =========================================================
    // BUSCAR POR TIPO
    // =========================================================

    public List<Document> buscarPorTipo(
            String tipoEvento
    ) {

        if (tipoEvento == null
                || tipoEvento.isBlank()) {

            throw new IllegalArgumentException(
                    "El tipo de evento es obligatorio."
            );
        }


        return eventoDAO.buscarPorTipo(
                tipoEvento.trim()
        );
    }


    // =========================================================
    // EXISTE POR ID
    // =========================================================

    public boolean existePorId(
            String id
    ) {

        validarObjectId(
                id,
                "El ID del evento no es válido."
        );


        return eventoDAO.existePorId(
                id
        );
    }


    // =========================================================
    // OBTENER TODOS LOS IDS
    // =========================================================

    public List<String> obtenerTodosLosIds() {

        return eventoDAO.obtenerTodosLosIds();
    }


    // =========================================================
    // VALIDAR OBJECT ID
    // =========================================================

    private void validarObjectId(
            String id,
            String mensaje
    ) {

        if (id == null
                || id.isBlank()
                || !ObjectId.isValid(id)) {

            throw new IllegalArgumentException(
                    mensaje
            );
        }
    }
}