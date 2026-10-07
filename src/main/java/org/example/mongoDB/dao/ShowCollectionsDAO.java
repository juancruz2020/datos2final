package org.example.mongoDB.dao;

import com.mongodb.client.MongoDatabase;
import org.example.conecciones.MongoSingleton;

import java.util.ArrayList;
import java.util.List;

public class ShowCollectionsDAO {

    private final MongoDatabase database;

    public  ShowCollectionsDAO() {
        this.database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");
    }

    // =========================
    // MOSTRAR COLECCIONES
    // =========================

    public List<String> mostrarCollections() {

        List<String> colecciones = new ArrayList<>();

        for (String nombre : database.listCollectionNames()) {
            colecciones.add(nombre);
        }

        return colecciones;
    }
}