package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Pago;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class PagoMongoDAO {

    private final MongoCollection<Document> coleccion;

    public PagoMongoDAO() {
        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("pagos");
    }


    // AGREGAR PAGO
    public void agregar(Pago pago) {

        Document documento = new Document()
                .append(
                        "factura_id",
                        new ObjectId(pago.getFacturaId())
                )
                .append(
                        "fecha",
                        pago.getFecha()
                )
                .append(
                        "monto",
                        new Decimal128(pago.getMonto())
                )
                .append(
                        "medio_pago",
                        pago.getMedioPago()
                );

        coleccion.insertOne(documento);

        pago.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


    // MODIFICAR PAGO
    public void modificar(Pago pago) {

        coleccion.updateOne(
                eq("_id", new ObjectId(pago.getId())),
                combine(
                        set(
                                "factura_id",
                                new ObjectId(pago.getFacturaId())
                        ),
                        set(
                                "fecha",
                                pago.getFecha()
                        ),
                        set(
                                "monto",
                                new Decimal128(pago.getMonto())
                        ),
                        set(
                                "medio_pago",
                                pago.getMedioPago()
                        )
                )
        );
    }


    // ELIMINAR PAGO
    public void eliminar(String id) {

        coleccion.deleteOne(
                eq("_id", new ObjectId(id))
        );
    }
}