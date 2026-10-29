package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Conversacion;
import com.mongodb.client.ClientSession;

import static com.mongodb.client.model.Updates.pull;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class ConversacionMongoDAO {

    private final MongoCollection<Document> coleccion;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ConversacionMongoDAO() {

        MongoDatabase database =
                MongoSingleton
                        .getInstance()
                        .getDatabase("datos2");

        this.coleccion =
                database.getCollection(
                        "conversaciones"
                );
    }


    // =========================================================
    // AGREGAR
    // =========================================================

    public void agregar(
            Conversacion conversacion
    ) {

        Document documento =
                new Document()
                        .append(
                                "nombre",
                                conversacion.getNombre()
                        )
                        .append(
                                "tipo",
                                conversacion.getTipo()
                        )
                        .append(
                                "participantes_ids",
                                conversacion.getParticipantesIds()
                        )
                        .append(
                                "fecha_creacion",
                                conversacion.getFechaCreacion()
                        );

        coleccion.insertOne(
                documento
        );

        conversacion.setId(
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
    // BUSCAR CONVERSACIONES DE UN USUARIO
    // =========================================================

    public List<Document> buscarPorUsuario(
            String usuarioId
    ) {

        return coleccion
                .find(
                        eq(
                                "participantes_ids",
                                usuarioId
                        )
                )
                .into(
                        new ArrayList<>()
                );
    }


    // ELIMINAR CONVERSACIÓN PRIVADA
    public void eliminar(
            ClientSession session,
            String conversacionId
    ) {
        coleccion.deleteOne(
                session,
                eq("_id", new ObjectId(conversacionId))
        );
    }

    // QUITAR USUARIO DE UNA CONVERSACIÓN GRUPAL
    public void quitarParticipante(
            ClientSession session,
            String conversacionId,
            String usuarioId
    ) {
        coleccion.updateOne(
                session,
                eq("_id", new ObjectId(conversacionId)),
                pull("participantes_ids", usuarioId)
        );
    }

}