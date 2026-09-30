package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Cliente;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class ClienteMongoDAO {

    private final MongoCollection<Document> coleccion;

    public ClienteMongoDAO() {
        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("clientes");
    }


    // AGREGAR CLIENTE
    public void agregar(Cliente cliente) {

        Document direccion = new Document()
                .append("calle", cliente.getDireccion().getCalle())
                .append("numero", cliente.getDireccion().getNumero())
                .append("ciudad", cliente.getDireccion().getCiudad())
                .append("codigo_postal", cliente.getDireccion().getCodigoPostal());

        Document documento = new Document()
                .append("razon_social", cliente.getRazonSocial())
                .append("cuit", cliente.getCuit())
                .append("email", cliente.getEmail())
                .append("telefono", cliente.getTelefono())
                .append("direccion", direccion)
                .append("pais", cliente.getPais())
                .append("estado", cliente.getEstado());

        coleccion.insertOne(documento);

        // Mongo genera el ObjectId automáticamente.
        // Lo guardamos también en el objeto Java.
        cliente.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


    // MODIFICAR CLIENTE
    public void modificar(Cliente cliente) {

        coleccion.updateOne(
                eq("_id", new ObjectId(cliente.getId())),
                combine(
                        set("razon_social", cliente.getRazonSocial()),
                        set("cuit", cliente.getCuit()),
                        set("email", cliente.getEmail()),
                        set("telefono", cliente.getTelefono()),

                        set(
                                "direccion.calle",
                                cliente.getDireccion().getCalle()
                        ),
                        set(
                                "direccion.numero",
                                cliente.getDireccion().getNumero()
                        ),
                        set(
                                "direccion.ciudad",
                                cliente.getDireccion().getCiudad()
                        ),
                        set(
                                "direccion.codigo_postal",
                                cliente.getDireccion().getCodigoPostal()
                        ),

                        set("pais", cliente.getPais()),
                        set("estado", cliente.getEstado())
                )
        );
    }


    // ELIMINAR CLIENTE
    public void eliminar(String id) {

        coleccion.deleteOne(
                eq("_id", new ObjectId(id))
        );
    }
}