package org.example.mongoDB.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.ContenedorMongoDAO;
import org.example.mongoDB.dao.SensorMongoDAO;
import org.example.mongoDB.model.Sensor;

import java.util.Date;
import java.util.List;

public class SensorService {

    private final SensorMongoDAO sensorDAO;
    private final ContenedorMongoDAO contenedorDAO;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SensorService() {

        this.sensorDAO =
                new SensorMongoDAO();

        this.contenedorDAO =
                new ContenedorMongoDAO();
    }


    // =========================================================
    // CREAR SENSOR
    // =========================================================

    public void crearSensor(
            String contenedorId,
            String tipo,
            String fabricante,
            Date fechaInstalacion
    ) {

        validarObjectId(
                contenedorId,
                "El ID del contenedor no es válido."
        );


        if (!contenedorDAO.existePorId(
                contenedorId
        )) {

            throw new IllegalArgumentException(
                    "El contenedor seleccionado no existe."
            );
        }


        if (tipo == null
                || tipo.isBlank()) {

            throw new IllegalArgumentException(
                    "El tipo de sensor es obligatorio."
            );
        }


        if (fabricante == null
                || fabricante.isBlank()) {

            throw new IllegalArgumentException(
                    "El fabricante es obligatorio."
            );
        }


        if (fechaInstalacion == null) {

            throw new IllegalArgumentException(
                    "La fecha de instalación es obligatoria."
            );
        }


        Sensor sensor =
                new Sensor(
                        null,
                        contenedorId,
                        tipo.trim(),
                        fabricante.trim(),
                        fechaInstalacion,
                        "ACTIVO"
                );


        sensorDAO.agregar(
                sensor
        );
    }


    // =========================================================
    // MODIFICAR SENSOR
    // =========================================================

    public void modificarSensor(
            String id,
            String contenedorId,
            String tipo,
            String fabricante,
            Date fechaInstalacion,
            String estado
    ) {

        // -----------------------------------------------------
        // VALIDAR SENSOR
        // -----------------------------------------------------

        validarObjectId(
                id,
                "El ID del sensor no es válido."
        );


        if (!sensorDAO.existePorId(id)) {

            throw new IllegalArgumentException(
                    "El sensor no existe."
            );
        }


        // -----------------------------------------------------
        // VALIDAR CONTENEDOR
        // -----------------------------------------------------

        validarObjectId(
                contenedorId,
                "El ID del contenedor no es válido."
        );


        if (!contenedorDAO.existePorId(
                contenedorId
        )) {

            throw new IllegalArgumentException(
                    "El contenedor seleccionado no existe."
            );
        }


        // -----------------------------------------------------
        // VALIDAR TIPO
        // -----------------------------------------------------

        if (tipo == null
                || tipo.isBlank()) {

            throw new IllegalArgumentException(
                    "El tipo de sensor es obligatorio."
            );
        }


        // -----------------------------------------------------
        // VALIDAR FABRICANTE
        // -----------------------------------------------------

        if (fabricante == null
                || fabricante.isBlank()) {

            throw new IllegalArgumentException(
                    "El fabricante es obligatorio."
            );
        }


        // -----------------------------------------------------
        // VALIDAR FECHA
        // -----------------------------------------------------

        if (fechaInstalacion == null) {

            throw new IllegalArgumentException(
                    "La fecha de instalación es obligatoria."
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
        // CREAR SENSOR MODIFICADO
        // -----------------------------------------------------

        Sensor sensor =
                new Sensor(
                        id,
                        contenedorId,
                        tipo.trim(),
                        fabricante.trim(),
                        fechaInstalacion,
                        estado.trim()
                );


        sensorDAO.modificar(
                sensor
        );
    }


    // =========================================================
    // ELIMINAR SENSOR
    // =========================================================

    public void eliminarSensor(
            String id
    ) {

        // -----------------------------------------------------
        // VALIDAR ID
        // -----------------------------------------------------

        validarObjectId(
                id,
                "El ID del sensor no es válido."
        );


        // -----------------------------------------------------
        // VERIFICAR QUE EXISTA
        // -----------------------------------------------------

        if (!sensorDAO.existePorId(id)) {

            throw new IllegalArgumentException(
                    "El sensor no existe."
            );
        }


        // -----------------------------------------------------
        // ELIMINAR
        // -----------------------------------------------------

        sensorDAO.eliminar(
                id
        );
    }


    // =========================================================
    // LISTAR SENSORES
    // =========================================================

    public List<Document> listarSensores() {

        return sensorDAO.listarTodos();
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        validarObjectId(
                id,
                "El ID del sensor no es válido."
        );


        Document sensor =
                sensorDAO.buscarPorId(
                        id
                );


        if (sensor == null) {

            throw new IllegalArgumentException(
                    "El sensor no existe."
            );
        }


        return sensor;
    }


    // =========================================================
    // EXISTE POR ID
    // =========================================================

    public boolean existePorId(
            String id
    ) {

        validarObjectId(
                id,
                "El ID del sensor no es válido."
        );


        return sensorDAO.existePorId(
                id
        );
    }


    // =========================================================
    // OBTENER TODOS LOS IDS
    // =========================================================

    public List<String> obtenerTodosLosIds() {

        return sensorDAO.obtenerTodosLosIds();
    }


    // =========================================================
    // BUSCAR POR CONTENEDOR
    // =========================================================

    public List<Document> buscarPorContenedor(
            String contenedorId
    ) {

        validarObjectId(
                contenedorId,
                "El ID del contenedor no es válido."
        );


        return sensorDAO.buscarPorContenedor(
                contenedorId
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


        return sensorDAO.buscarPorEstado(
                estado.trim()
        );
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