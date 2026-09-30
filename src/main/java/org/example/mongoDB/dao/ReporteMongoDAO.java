package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Reporte;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class ReporteMongoDAO {

    private final MongoCollection<Document> coleccion;

    public ReporteMongoDAO() {
        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("reportes");
    }


    // AGREGAR REPORTE
    public void agregar(Reporte reporte) {

        Document documento = new Document()
                .append(
                        "usuario_id",
                        new ObjectId(reporte.getUsuarioId())
                )
                .append("tipo", reporte.getTipo())
                .append("fecha_generacion", reporte.getFechaGeneracion())
                .append("formato", reporte.getFormato())
                .append("estado", reporte.getEstado());

        coleccion.insertOne(documento);

        reporte.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


    // MODIFICAR REPORTE
    public void modificar(Reporte reporte) {

        coleccion.updateOne(
                eq("_id", new ObjectId(reporte.getId())),
                combine(
                        set(
                                "usuario_id",
                                new ObjectId(reporte.getUsuarioId())
                        ),
                        set("tipo", reporte.getTipo()),
                        set("fecha_generacion", reporte.getFechaGeneracion()),
                        set("formato", reporte.getFormato()),
                        set("estado", reporte.getEstado())
                )
        );
    }


    // ELIMINAR REPORTE
    public void eliminar(String id) {

        coleccion.deleteOne(
                eq("_id", new ObjectId(id))
        );
    }
}