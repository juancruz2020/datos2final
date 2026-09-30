package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Rol;

import java.util.ArrayList;
import java.util.List;

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


    // =========================
    // AGREGAR ROL
    // =========================

    public void agregar(Rol rol) {

        Document documento = new Document()
                .append("descripcion", rol.getDescripcion())
                .append("nivel_permisos", rol.getNivelPermisos());

        coleccion.insertOne(documento);

        rol.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


    // =========================
    // MODIFICAR ROL
    // =========================

    public void modificar(Rol rol) {

        coleccion.updateOne(
                eq("_id", new ObjectId(rol.getId())),
                combine(
                        set("descripcion", rol.getDescripcion()),
                        set("nivel_permisos", rol.getNivelPermisos())
                )
        );
    }


    // =========================
    // ELIMINAR ROL
    // =========================

    public void eliminar(String id) {

        coleccion.deleteOne(
                eq("_id", new ObjectId(id))
        );
    }


    // =========================
    // BUSCAR ROL POR ID
    // =========================

    public Document buscarPorId(String id) {

        return coleccion.find(
                eq("_id", new ObjectId(id))
        ).first();
    }


    // =========================
    // LISTAR ROLES
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
    // BUSCAR ROL POR DESCRIPCION
    // =========================

    public Document buscarPorDescripcion(String descripcion) {

        return coleccion.find(
                eq("descripcion", descripcion)
        ).first();
    }
}