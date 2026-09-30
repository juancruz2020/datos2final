package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.EventoLogistico;

import java.util.ArrayList;
import java.util.List;

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


    // =========================
    // AGREGAR EVENTO LOGISTICO
    // =========================

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


    // =========================
    // MODIFICAR EVENTO LOGISTICO
    // =========================

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


    // =========================
    // ELIMINAR EVENTO LOGISTICO
    // =========================

    public void eliminar(String id) {

        coleccion.deleteOne(
                eq("_id", new ObjectId(id))
        );
    }


    // =========================
    // BUSCAR EVENTO POR ID
    // =========================

    public Document buscarPorId(String id) {

        return coleccion.find(
                eq("_id", new ObjectId(id))
        ).first();
    }


    // =========================
    // LISTAR EVENTOS
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
    // BUSCAR EVENTOS POR ENVIO
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
    // BUSCAR EVENTOS POR TIPO
    // =========================

    public List<Document> buscarPorTipo(String tipoEvento) {

        return coleccion.find(
                eq("tipo_evento", tipoEvento)
        ).into(new ArrayList<>());
    }
}