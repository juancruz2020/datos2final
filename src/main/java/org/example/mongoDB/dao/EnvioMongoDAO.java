package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Envio;
import org.example.mongoDB.model.Tramo;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class EnvioMongoDAO {

    private final MongoCollection<Document> coleccion;

    public EnvioMongoDAO() {
        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("envios");
    }


    // AGREGAR ENVIO
    public void agregar(Envio envio) {

        Document origen = new Document()
                .append("ciudad", envio.getOrigen().getCiudad())
                .append("pais", envio.getOrigen().getPais());

        Document destino = new Document()
                .append("ciudad", envio.getDestino().getCiudad())
                .append("pais", envio.getDestino().getPais());

        List<ObjectId> contenedoresIds = new ArrayList<>();

        for (String id : envio.getContenedoresIds()) {
            contenedoresIds.add(new ObjectId(id));
        }

        List<Document> tramos = convertirTramos(envio.getTramos());

        Document documento = new Document()
                .append(
                        "cliente_id",
                        new ObjectId(envio.getClienteId())
                )
                .append(
                        "contenedores_ids",
                        contenedoresIds
                )
                .append(
                        "fecha_creacion",
                        envio.getFechaCreacion()
                )
                .append("origen", origen)
                .append("destino", destino)
                .append("estado", envio.getEstado())
                .append("prioridad", envio.getPrioridad())
                .append("tramos", tramos);

        coleccion.insertOne(documento);

        envio.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


    // MODIFICAR ENVIO
    public void modificar(Envio envio) {

        Document origen = new Document()
                .append("ciudad", envio.getOrigen().getCiudad())
                .append("pais", envio.getOrigen().getPais());

        Document destino = new Document()
                .append("ciudad", envio.getDestino().getCiudad())
                .append("pais", envio.getDestino().getPais());

        List<ObjectId> contenedoresIds = new ArrayList<>();

        for (String id : envio.getContenedoresIds()) {
            contenedoresIds.add(new ObjectId(id));
        }

        List<Document> tramos = convertirTramos(envio.getTramos());

        coleccion.updateOne(
                eq("_id", new ObjectId(envio.getId())),
                combine(
                        set(
                                "cliente_id",
                                new ObjectId(envio.getClienteId())
                        ),
                        set(
                                "contenedores_ids",
                                contenedoresIds
                        ),
                        set(
                                "fecha_creacion",
                                envio.getFechaCreacion()
                        ),
                        set("origen", origen),
                        set("destino", destino),
                        set("estado", envio.getEstado()),
                        set("prioridad", envio.getPrioridad()),
                        set("tramos", tramos)
                )
        );
    }


    // ELIMINAR ENVIO
    public void eliminar(String id) {

        coleccion.deleteOne(
                eq("_id", new ObjectId(id))
        );
    }


    // CONVERTIR LOS TRAMOS DE JAVA A DOCUMENTOS MONGO
    private List<Document> convertirTramos(List<Tramo> listaTramos) {

        List<Document> documentos = new ArrayList<>();

        if (listaTramos == null) {
            return documentos;
        }

        for (Tramo tramo : listaTramos) {

            Document documento = new Document()
                    .append(
                            "medio_transporte",
                            tramo.getMedioTransporte()
                    )
                    .append("origen", tramo.getOrigen())
                    .append("destino", tramo.getDestino())
                    .append(
                            "fecha_salida",
                            tramo.getFechaSalida()
                    )
                    .append(
                            "fecha_llegada_estimada",
                            tramo.getFechaLlegadaEstimada()
                    );

            documentos.add(documento);
        }

        return documentos;
    }
}