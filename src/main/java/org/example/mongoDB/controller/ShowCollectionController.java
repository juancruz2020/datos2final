package org.example.mongoDB.controller;

import org.example.mongoDB.dao.ShowCollectionsDAO;

import java.util.List;

public class ShowCollectionController {

    private final ShowCollectionsDAO dao;

    public ShowCollectionController() {
        this.dao = new ShowCollectionsDAO();
    }

    // =========================
    // MOSTRAR COLECCIONES
    // =========================

    public List<String> mostrarCollections() {
        return dao.mostrarCollections();
    }
}