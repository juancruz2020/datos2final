package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Usuario;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class UsuarioMongoDAO {

    private final MongoCollection<Document> coleccion;

    public UsuarioMongoDAO() {
        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("usuarios");
    }


    // AGREGAR USUARIO
    public void agregar(Usuario usuario) {

        Document documento = new Document()
                .append(
                        "cliente_id",
                        new ObjectId(usuario.getClienteId())
                )
                .append(
                        "rol_id",
                        new ObjectId(usuario.getRolId())
                )
                .append("nombre", usuario.getNombre())
                .append("apellido", usuario.getApellido())
                .append("email", usuario.getEmail())
                .append(
                        "contraseña_encriptada",
                        usuario.getContraseñaEncriptada()
                )
                .append("estado", usuario.getEstado())
                .append("fecha_registro", usuario.getFechaRegistro());

        coleccion.insertOne(documento);

        usuario.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


    // MODIFICAR USUARIO
    public void modificar(Usuario usuario) {

        coleccion.updateOne(
                eq("_id", new ObjectId(usuario.getId())),
                combine(
                        set(
                                "cliente_id",
                                new ObjectId(usuario.getClienteId())
                        ),
                        set(
                                "rol_id",
                                new ObjectId(usuario.getRolId())
                        ),
                        set("nombre", usuario.getNombre()),
                        set("apellido", usuario.getApellido()),
                        set("email", usuario.getEmail()),
                        set(
                                "contraseña_encriptada",
                                usuario.getContraseñaEncriptada()
                        ),
                        set("estado", usuario.getEstado()),
                        set(
                                "fecha_registro",
                                usuario.getFechaRegistro()
                        )
                )
        );
    }


    // ELIMINAR USUARIO
    public void eliminar(String id) {

        coleccion.deleteOne(
                eq("_id", new ObjectId(id))
        );
    }
}