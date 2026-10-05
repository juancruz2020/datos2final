package org.example.mongoDB.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.AlertaMongoDAO;
import org.example.mongoDB.dao.EventoLogisticoMongoDAO;
import org.example.mongoDB.dao.SensorMongoDAO;
import org.example.mongoDB.model.Alerta;

import java.util.Date;
import java.util.List;

public class AlertaService {

    private final AlertaMongoDAO alertaDAO;
    private final SensorMongoDAO sensorDAO;
    private final EventoLogisticoMongoDAO eventoDAO;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AlertaService() {

        this.alertaDAO =
                new AlertaMongoDAO();

        this.sensorDAO =
                new SensorMongoDAO();

        this.eventoDAO =
                new EventoLogisticoMongoDAO();
    }


    // =========================================================
    // CREAR ALERTA
    // =========================================================

    public void crearAlerta(
            String sensorId,
            String eventoId,
            String tipo
    ) {

        // -----------------------------------------------------
        // VALIDAR SENSOR
        // -----------------------------------------------------

        validarObjectId(
                sensorId,
                "El ID del sensor no es válido."
        );


        if (!sensorDAO.existePorId(sensorId)) {

            throw new IllegalArgumentException(
                    "El sensor seleccionado no existe."
            );
        }


        // -----------------------------------------------------
        // VALIDAR EVENTO
        // -----------------------------------------------------

        validarObjectId(
                eventoId,
                "El ID del evento no es válido."
        );


        if (!eventoDAO.existePorId(eventoId)) {

            throw new IllegalArgumentException(
                    "El evento logístico seleccionado no existe."
            );
        }


        // -----------------------------------------------------
        // VALIDAR TIPO
        // -----------------------------------------------------

        if (tipo == null
                || tipo.isBlank()) {

            throw new IllegalArgumentException(
                    "El tipo de alerta es obligatorio."
            );
        }


        // -----------------------------------------------------
        // CREAR ALERTA
        // -----------------------------------------------------

        Alerta alerta =
                new Alerta(
                        null,
                        sensorId,
                        eventoId,
                        tipo.trim(),
                        new Date(),
                        "ACTIVA"
                );


        alertaDAO.agregar(
                alerta
        );
    }


    // =========================================================
    // LISTAR ALERTAS
    // =========================================================

    public List<Document> listarAlertas() {

        return alertaDAO.listarTodos();
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        validarObjectId(
                id,
                "El ID de la alerta no es válido."
        );


        Document alerta =
                alertaDAO.buscarPorId(
                        id
                );


        if (alerta == null) {

            throw new IllegalArgumentException(
                    "La alerta no existe."
            );
        }


        return alerta;
    }


    // =========================================================
    // BUSCAR POR SENSOR
    // =========================================================

    public List<Document> buscarPorSensor(
            String sensorId
    ) {

        validarObjectId(
                sensorId,
                "El ID del sensor no es válido."
        );


        return alertaDAO.buscarPorSensor(
                sensorId
        );
    }


    // =========================================================
    // BUSCAR POR EVENTO
    // =========================================================

    public List<Document> buscarPorEvento(
            String eventoId
    ) {

        validarObjectId(
                eventoId,
                "El ID del evento no es válido."
        );


        return alertaDAO.buscarPorEvento(
                eventoId
        );
    }


    // =========================================================
    // BUSCAR POR ESTADO
    // =========================================================

    public List<Document> buscarPorEstado(
            String estado
    ) {

        if (estado == null
                || estado.isBlank()) {

            throw new IllegalArgumentException(
                    "El estado es obligatorio."
            );
        }


        return alertaDAO.buscarPorEstado(
                estado.trim()
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
                "El ID de la alerta no es válido."
        );


        return alertaDAO.existePorId(
                id
        );
    }


    // =========================================================
    // OBTENER TODOS LOS IDS
    // =========================================================

    public List<String> obtenerTodosLosIds() {

        return alertaDAO.obtenerTodosLosIds();
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