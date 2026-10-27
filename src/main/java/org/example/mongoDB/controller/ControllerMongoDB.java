package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.service.LogisticaMongoService;

import java.util.List;
import org.example.mongoDB.dao.ShowCollectionsDAO;

public class ControllerMongoDB {

    private final LogisticaMongoService service;
    private final ShowCollectionsDAO showCollectionsDAO;


    // =========================
    // CONSTRUCTOR
    // =========================

    public ControllerMongoDB() {
        this.service = new LogisticaMongoService();
        this.showCollectionsDAO = new ShowCollectionsDAO();
    }


    // =========================
    // CLIENTES
    // =========================


    public Document buscarClientePorId(String id) {
        return service.buscarClientePorId(id);
    }


    // =========================
    // USUARIOS
    // =========================


    public List<String> obtenerIdsUsuarios() {
        return service.obtenerIdsUsuarios();
    }


    // =========================
    // CONTENEDORES
    // =========================


    public List<String> obtenerIdsContenedores() {
        return service.obtenerIdsContenedores();
    }


    // =========================
    // SENSORES
    // =========================


    public List<String> obtenerIdsSensores() {
        return service.obtenerIdsSensores();
    }


    // =========================
// MOSTRAR COLLECTIONS
// =========================

    public List<String> mostrarCollections() {
        return showCollectionsDAO.mostrarCollections();
    }
}
