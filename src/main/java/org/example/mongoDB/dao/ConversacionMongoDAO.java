package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Conversacion;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

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
    // MODIFICAR
    // =========================================================

    public void modificar(
            Conversacion conversacion
    ) {

        coleccion.updateOne(
                eq(
                        "_id",
                        new ObjectId(
                                conversacion.getId()
                        )
                ),
                combine(
                        set(
                                "nombre",
                                conversacion.getNombre()
                        ),
                        set(
                                "tipo",
                                conversacion.getTipo()
                        ),
                        set(
                                "participantes_ids",
                                conversacion.getParticipantesIds()
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
    // LISTAR TODAS
    // =========================================================

    public List<Document> listarTodos() {

        return coleccion
                .find()
                .into(
                        new ArrayList<>()
                );
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


    // =========================================================
    // BUSCAR POR TIPO
    // =========================================================

    public List<Document> buscarPorTipo(
            String tipo
    ) {

        return coleccion
                .find(
                        eq(
                                "tipo",
                                tipo
                        )
                )
                .into(
                        new ArrayList<>()
                );
    }


    // =========================================================
    // EXISTE POR ID
    // =========================================================

    public boolean existePorId(
            String id
    ) {

        Document documento =
                buscarPorId(id);

        return documento != null;
    }


    // =========================================================
    // OBTENER IDS
    // =========================================================

    public List<String> obtenerTodosLosIds() {

        List<String> ids =
                new ArrayList<>();

        for (
                Document documento :
                coleccion.find()
        ) {

            ObjectId id =
                    documento.getObjectId(
                            "_id"
                    );

            if (id != null) {

                ids.add(
                        id.toHexString()
                );
            }
        }

        return ids;
    }
}