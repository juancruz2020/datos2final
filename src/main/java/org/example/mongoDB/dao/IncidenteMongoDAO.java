package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Incidente;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class IncidenteMongoDAO {

    private final MongoCollection<Document> coleccion;

    public IncidenteMongoDAO() {
        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("incidentes");
    }


    // =========================
    // AGREGAR INCIDENTE
    // =========================

    public void agregar(Incidente incidente) {

        Document documento = new Document()
                .append(
                        "envio_id",
                        new ObjectId(incidente.getEnvioId())
                )
                .append("tipo", incidente.getTipo())
                .append("fecha", incidente.getFecha())
                .append("severidad", incidente.getSeveridad())
                .append("estado", incidente.getEstado())
                .append("descripcion", incidente.getDescripcion());

        coleccion.insertOne(documento);

        incidente.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


    // =========================
    // MODIFICAR INCIDENTE
    // =========================

    public void modificar(Incidente incidente) {

        coleccion.updateOne(
                eq("_id", new ObjectId(incidente.getId())),
                combine(
                        set(
                                "envio_id",
                                new ObjectId(incidente.getEnvioId())
                        ),
                        set("tipo", incidente.getTipo()),
                        set("fecha", incidente.getFecha()),
                        set("severidad", incidente.getSeveridad()),
                        set("estado", incidente.getEstado()),
                        set("descripcion", incidente.getDescripcion())
                )
        );
    }


    // =========================
    // ELIMINAR INCIDENTE
    // =========================

    public void eliminar(String id) {

        coleccion.deleteOne(
                eq("_id", new ObjectId(id))
        );
    }


    // =========================
    // BUSCAR INCIDENTE POR ID
    // =========================

    public Document buscarPorId(String id) {

        return coleccion.find(
                eq("_id", new ObjectId(id))
        ).first();
    }


    // =========================
    // LISTAR TODOS
    // =========================

    public List<Document> listarTodos() {

        return coleccion.find()
                .into(new ArrayList<>());
    }


    // =========================
    // BUSCAR POR ENVIO
    // =========================

    public List<Document> buscarPorEnvio(String envioId) {

        return coleccion.find(
                eq(
                        "envio_id",
                        new ObjectId(envioId)
                )
        ).into(new ArrayList<>());
    }


    // =========================
    // BUSCAR INCIDENTES ABIERTOS
    // =========================

    public List<Document> buscarAbiertos() {

        return coleccion.find(
                eq("estado", "ABIERTO")
        ).into(new ArrayList<>());
    }
}