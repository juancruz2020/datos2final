package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.service.SensorService;

import java.util.Date;
import java.util.List;

public class SensorController {

    private final SensorService sensorService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SensorController() {

        this.sensorService =
                new SensorService();
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

        sensorService.crearSensor(
                contenedorId,
                tipo,
                fabricante,
                fechaInstalacion
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

        sensorService.modificarSensor(
                id,
                contenedorId,
                tipo,
                fabricante,
                fechaInstalacion,
                estado
        );
    }


    // =========================================================
    // ELIMINAR SENSOR
    // =========================================================

    public void eliminarSensor(
            String id
    ) {

        sensorService.eliminarSensor(
                id
        );
    }


    // =========================================================
    // LISTAR SENSORES
    // =========================================================

    public List<Document> listarSensores() {

        return sensorService
                .listarSensores();
    }


}