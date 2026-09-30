package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Contenedor;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class ContenedorMongoDAO {

    private final MongoCollection<Document> coleccion;

    public ContenedorMongoDAO() {
        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("contenedores");
    }


    // AGREGAR CONTENEDOR
    public void agregar(Contenedor contenedor) {

        Document documento = new Document()
                .append(
                        "codigo_internacional",
                        contenedor.getCodigoInternacional()
                )
                .append("tipo", contenedor.getTipo())
                .append("capacidad", contenedor.getCapacidad())
                .append("estado", contenedor.getEstado());

        coleccion.insertOne(documento);

        contenedor.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


    // MODIFICAR CONTENEDOR
    public void modificar(Contenedor contenedor) {

        coleccion.updateOne(
                eq("_id", new ObjectId(contenedor.getId())),
                combine(
                        set(
                                "codigo_internacional",
                                contenedor.getCodigoInternacional()
                        ),
                        set("tipo", contenedor.getTipo()),
                        set("capacidad", contenedor.getCapacidad()),
                        set("estado", contenedor.getEstado())
                )
        );
    }


    // ELIMINAR CONTENEDOR
    public void eliminar(String id) {

        coleccion.deleteOne(
                eq("_id", new ObjectId(id))
        );
    }
}