package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Alerta;


public class AlertaMongoDAO {

    private final MongoCollection<Document> coleccion;

    public AlertaMongoDAO() {
        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("alertas");
    }


    // =========================
    // AGREGAR ALERTA
    // =========================

    public void agregar(Alerta alerta) {

        Document documento = new Document()
                .append(
                        "sensor_id",
                        new ObjectId(alerta.getSensorId())
                )
                .append(
                        "evento_id",
                        new ObjectId(alerta.getEventoId())
                )
                .append("tipo", alerta.getTipo())
                .append("fecha", alerta.getFecha())
                .append("estado", alerta.getEstado());

        coleccion.insertOne(documento);

        alerta.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


}