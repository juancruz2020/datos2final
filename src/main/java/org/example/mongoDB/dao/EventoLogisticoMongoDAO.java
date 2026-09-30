package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.EventoLogistico;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class EventoLogisticoMongoDAO {

    private final MongoCollection<Document> coleccion;

    public EventoLogisticoMongoDAO() {
        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("eventos_logisticos");
    }


    // AGREGAR EVENTO LOGISTICO
    public void agregar(EventoLogistico evento) {

        Document documento = new Document()
                .append(
                        "envio_id",
                        new ObjectId(evento.getEnvioId())
                )
                .append("fecha_hora", evento.getFechaHora())
                .append("tipo_evento", evento.getTipoEvento())
                .append("ubicacion", evento.getUbicacion())
                .append("descripcion", evento.getDescripcion());

        coleccion.insertOne(documento);

        evento.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


    // MODIFICAR EVENTO LOGISTICO
    public void modificar(EventoLogistico evento) {

        coleccion.updateOne(
                eq("_id", new ObjectId(evento.getId())),
                combine(
                        set(
                                "envio_id",
                                new ObjectId(evento.getEnvioId())
                        ),
                        set("fecha_hora", evento.getFechaHora()),
                        set("tipo_evento", evento.getTipoEvento()),
                        set("ubicacion", evento.getUbicacion()),
                        set("descripcion", evento.getDescripcion())
                )
        );
    }


    // ELIMINAR EVENTO LOGISTICO
    public void eliminar(String id) {

        coleccion.deleteOne(
                eq("_id", new ObjectId(id))
        );
    }
}