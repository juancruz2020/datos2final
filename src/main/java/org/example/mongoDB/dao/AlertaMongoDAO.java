package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Alerta;

import java.util.ArrayList;
import java.util.List;

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


    // =========================
    // MODIFICAR ALERTA
    // =========================

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


    // =========================
    // ELIMINAR ALERTA
    // =========================

    public void eliminar(String id) {

        coleccion.deleteOne(
                eq("_id", new ObjectId(id))
        );
    }


    // =========================
    // BUSCAR ALERTA POR ID
    // =========================

    public Document buscarPorId(String id) {

        return coleccion.find(
                eq("_id", new ObjectId(id))
        ).first();
    }


    // =========================
    // LISTAR ALERTAS
    // =========================

    public List<Document> listarTodos() {

        return coleccion.find()
                .into(new ArrayList<>());
    }


    // =========================
    // OBTENER TODOS LOS IDS
    // =========================

    public List<String> obtenerTodosLosIds() {

        List<String> ids = new ArrayList<>();

        for (Document documento : coleccion.find()) {

            ObjectId id = documento.getObjectId("_id");

            if (id != null) {
                ids.add(id.toHexString());
            }
        }

        return ids;
    }


    // =========================
    // VERIFICAR SI EXISTE POR ID
    // =========================

    public boolean existePorId(String id) {

        Document documento = coleccion.find(
                eq("_id", new ObjectId(id))
        ).first();

        return documento != null;
    }


    // =========================
    // BUSCAR ALERTAS POR SENSOR
    // =========================

    public List<Document> buscarPorSensor(String sensorId) {

        return coleccion.find(
                eq(
                        "sensor_id",
                        new ObjectId(sensorId)
                )
        ).into(new ArrayList<>());
    }


    // =========================
    // BUSCAR ALERTAS POR EVENTO
    // =========================

    public List<Document> buscarPorEvento(String eventoId) {

        return coleccion.find(
                eq(
                        "evento_id",
                        new ObjectId(eventoId)
                )
        ).into(new ArrayList<>());
    }


    // =========================
    // BUSCAR ALERTAS POR ESTADO
    // =========================

    public List<Document> buscarPorEstado(String estado) {

        return coleccion.find(
                eq("estado", estado)
        ).into(new ArrayList<>());
    }
}