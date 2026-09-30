package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Rol;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class RolMongoDAO {

    private final MongoCollection<Document> coleccion;

    public RolMongoDAO() {
        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("roles");
    }


    // AGREGAR ROL
    public void agregar(Rol rol) {

        Document documento = new Document()
                .append("descripcion", rol.getDescripcion())
                .append("nivel_permisos", rol.getNivelPermisos());

        coleccion.insertOne(documento);

        // Mongo genera el ObjectId automáticamente
        rol.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


    // MODIFICAR ROL
    public void modificar(Rol rol) {

        coleccion.updateOne(
                eq("_id", new ObjectId(rol.getId())),
                combine(
                        set("descripcion", rol.getDescripcion()),
                        set("nivel_permisos", rol.getNivelPermisos())
                )
        );
    }


    // ELIMINAR ROL
    public void eliminar(String id) {

        coleccion.deleteOne(
                eq("_id", new ObjectId(id))
        );
    }
}