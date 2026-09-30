package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Alerta;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class AlertaMongoDAO {

    private final MongoCollection<Document> coleccion;

    public AlertaMongoDAO() {
        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("alertas");
    }


    // AGREGAR ALERTA
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


    // MODIFICAR ALERTA
    public void modificar(Alerta alerta) {

        coleccion.updateOne(
                eq("_id", new ObjectId(alerta.getId())),
                combine(
                        set(
                                "sensor_id",
                                new ObjectId(alerta.getSensorId())
                        ),
                        set(
                                "evento_id",
                                new ObjectId(alerta.getEventoId())
                        ),
                        set("tipo", alerta.getTipo()),
                        set("fecha", alerta.getFecha()),
                        set("estado", alerta.getEstado())
                )
        );
    }


    // ELIMINAR ALERTA
    public void eliminar(String id) {

        coleccion.deleteOne(
                eq("_id", new ObjectId(id))
        );
    }
}