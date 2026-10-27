package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Mensaje;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.ascending;

public class MensajeMongoDAO {

    private final MongoCollection<Document> coleccion;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public MensajeMongoDAO() {

        MongoDatabase database =
                MongoSingleton
                        .getInstance()
                        .getDatabase("datos2");

        this.coleccion =
                database.getCollection(
                        "mensajes"
                );
    }


    // =========================================================
    // AGREGAR
    // =========================================================

    public void agregar(
            Mensaje mensaje
    ) {

        Document documento =
                new Document()
                        .append(
                                "conversacion_id",
                                mensaje.getConversacionId()
                        )
                        .append(
                                "remitente_id",
                                mensaje.getRemitenteId()
                        )
                        .append(
                                "contenido",
                                mensaje.getContenido()
                        )
                        .append(
                                "fecha_envio",
                                mensaje.getFechaEnvio()
                        );

        coleccion.insertOne(
                documento
        );

        mensaje.setId(
                documento
                        .getObjectId("_id")
                        .toHexString()
        );
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        return coleccion
                .find(
                        eq(
                                "_id",
                                new ObjectId(id)
                        )
                )
                .first();
    }


    // =========================================================
    // HISTORIAL DE UNA CONVERSACION
    // =========================================================

    public List<Document> buscarPorConversacion(
            String conversacionId
    ) {

        return coleccion
                .find(
                        eq(
                                "conversacion_id",
                                conversacionId
                        )
                )
                .sort(
                        ascending(
                                "fecha_envio"
                        )
                )
                .into(
                        new ArrayList<>()
                );
    }


    // =========================================================
    // MODIFICAR MENSAJE
    // =========================================================

    public void modificar(
            String mensajeId,
            String contenido
    ) {

        coleccion.updateOne(
                eq(
                        "_id",
                        new ObjectId(mensajeId)
                ),
                new Document(
                        "$set",
                        new Document(
                                "contenido",
                                contenido
                        )
                )
        );
    }


    // =========================================================
    // ELIMINAR
    // =========================================================

    public void eliminar(
            String id
    ) {

        coleccion.deleteOne(
                eq(
                        "_id",
                        new ObjectId(id)
                )
        );
    }


}