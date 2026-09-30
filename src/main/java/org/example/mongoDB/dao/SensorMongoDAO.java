package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Sensor;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class SensorMongoDAO {

    private final MongoCollection<Document> coleccion;

    public SensorMongoDAO() {
        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("sensores");
    }


    // =========================
    // AGREGAR SENSOR
    // =========================

    public void agregar(Sensor sensor) {

        Document documento = new Document()
                .append(
                        "contenedor_id",
                        new ObjectId(sensor.getContenedorId())
                )
                .append("tipo", sensor.getTipo())
                .append("fabricante", sensor.getFabricante())
                .append("fecha_instalacion", sensor.getFechaInstalacion())
                .append("estado", sensor.getEstado());

        coleccion.insertOne(documento);

        sensor.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


    // =========================
    // MODIFICAR SENSOR
    // =========================

    public void modificar(Sensor sensor) {

        coleccion.updateOne(
                eq("_id", new ObjectId(sensor.getId())),
                combine(
                        set(
                                "contenedor_id",
                                new ObjectId(sensor.getContenedorId())
                        ),
                        set("tipo", sensor.getTipo()),
                        set("fabricante", sensor.getFabricante()),
                        set(
                                "fecha_instalacion",
                                sensor.getFechaInstalacion()
                        ),
                        set("estado", sensor.getEstado())
                )
        );
    }


    // =========================
    // ELIMINAR SENSOR
    // =========================

    public void eliminar(String id) {

        coleccion.deleteOne(
                eq("_id", new ObjectId(id))
        );
    }


    // =========================
    // BUSCAR SENSOR POR ID
    // =========================

    public Document buscarPorId(String id) {

        return coleccion.find(
                eq("_id", new ObjectId(id))
        ).first();
    }


    // =========================
    // LISTAR SENSORES
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
    // BUSCAR SENSORES POR CONTENEDOR
    // =========================

    public List<Document> buscarPorContenedor(String contenedorId) {

        return coleccion.find(
                eq(
                        "contenedor_id",
                        new ObjectId(contenedorId)
                )
        ).into(new ArrayList<>());
    }


    // =========================
    // BUSCAR SENSORES POR ESTADO
    // =========================

    public List<Document> buscarPorEstado(String estado) {

        return coleccion.find(
                eq("estado", estado)
        ).into(new ArrayList<>());
    }
}