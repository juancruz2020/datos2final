package org.example.mongoDB.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.EnvioMongoDAO;
import org.example.mongoDB.dao.IncidenteMongoDAO;
import org.example.mongoDB.model.Incidente;

import java.util.Date;
import java.util.List;

public class IncidenteService {

    private final IncidenteMongoDAO incidenteDAO;
    private final EnvioMongoDAO envioDAO;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public IncidenteService() {

        this.incidenteDAO =
                new IncidenteMongoDAO();

        this.envioDAO =
                new EnvioMongoDAO();
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

        if (tipo == null
                || tipo.isBlank()) {

            throw new IllegalArgumentException(
                    "El tipo de incidente es obligatorio."
            );
        }


        // -----------------------------------------------------
        // VALIDAR SEVERIDAD
        // -----------------------------------------------------

        if (severidad == null
                || severidad.isBlank()) {

            throw new IllegalArgumentException(
                    "La severidad es obligatoria."
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
        // CREAR INCIDENTE
        // -----------------------------------------------------

        Incidente incidente =
                new Incidente(
                        null,
                        envioId,
                        tipo.trim(),
                        new Date(),
                        severidad.trim(),
                        "ABIERTO",
                        descripcion.trim()
                );


        incidenteDAO.agregar(
                incidente
        );
    }


    // =========================================================
    // MODIFICAR INCIDENTE
    // =========================================================

    public void modificarIncidente(
            String id,
            String envioId,
            String tipo,
            String severidad,
            String estado,
            String descripcion
    ) {

        // -----------------------------------------------------
        // VALIDAR INCIDENTE
        // -----------------------------------------------------

        validarObjectId(
                id,
                "El ID del incidente no es válido."
        );


        Document existente =
                incidenteDAO.buscarPorId(
                        id
                );


        if (existente == null) {

            throw new IllegalArgumentException(
                    "El incidente no existe."
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

        if (tipo == null
                || tipo.isBlank()) {

            throw new IllegalArgumentException(
                    "El tipo de incidente es obligatorio."
            );
        }


        // -----------------------------------------------------
        // VALIDAR SEVERIDAD
        // -----------------------------------------------------

        if (severidad == null
                || severidad.isBlank()) {

            throw new IllegalArgumentException(
                    "La severidad es obligatoria."
            );
        }


        // -----------------------------------------------------
        // VALIDAR ESTADO
        // -----------------------------------------------------

        if (estado == null
                || estado.isBlank()) {

            throw new IllegalArgumentException(
                    "El estado es obligatorio."
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

        Date fecha =
                existente.getDate(
                        "fecha"
                );


        // -----------------------------------------------------
        // CREAR OBJETO MODIFICADO
        // -----------------------------------------------------

        Incidente incidente =
                new Incidente(
                        id,
                        envioId,
                        tipo.trim(),
                        fecha,
                        severidad.trim(),
                        estado.trim(),
                        descripcion.trim()
                );


        // -----------------------------------------------------
        // ACTUALIZAR EN MONGODB
        // -----------------------------------------------------

        incidenteDAO.modificar(
                incidente
        );
    }


    // =========================================================
    // ELIMINAR INCIDENTE
    // =========================================================

    public void eliminarIncidente(
            String id
    ) {

        // -----------------------------------------------------
        // VALIDAR ID
        // -----------------------------------------------------

        validarObjectId(
                id,
                "El ID del incidente no es válido."
        );


        // -----------------------------------------------------
        // VERIFICAR EXISTENCIA
        // -----------------------------------------------------

        if (!incidenteDAO.existePorId(id)) {

            throw new IllegalArgumentException(
                    "El incidente no existe."
            );
        }


        // -----------------------------------------------------
        // ELIMINAR
        // -----------------------------------------------------

        incidenteDAO.eliminar(
                id
        );
    }


    // =========================================================
    // LISTAR INCIDENTES
    // =========================================================

    public List<Document> listarIncidentes() {

        return incidenteDAO.listarTodos();
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        validarObjectId(
                id,
                "El ID del incidente no es válido."
        );


        Document incidente =
                incidenteDAO.buscarPorId(
                        id
                );


        if (incidente == null) {

            throw new IllegalArgumentException(
                    "El incidente no existe."
            );
        }


        return incidente;
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


        return incidenteDAO.buscarPorEnvio(
                envioId
        );
    }


    // =========================================================
    // BUSCAR INCIDENTES ABIERTOS
    // =========================================================

    public List<Document> buscarAbiertos() {

        return incidenteDAO.buscarAbiertos();
    }


    // =========================================================
    // EXISTE POR ID
    // =========================================================

    public boolean existePorId(
            String id
    ) {

        validarObjectId(
                id,
                "El ID del incidente no es válido."
        );


        return incidenteDAO.existePorId(
                id
        );
    }


    // =========================================================
    // OBTENER TODOS LOS IDS
    // =========================================================

    public List<String> obtenerTodosLosIds() {

        return incidenteDAO.obtenerTodosLosIds();
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