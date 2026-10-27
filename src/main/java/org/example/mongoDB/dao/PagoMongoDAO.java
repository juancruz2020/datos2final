package org.example.mongoDB.dao;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Pago;


public class PagoMongoDAO {

    private final MongoCollection<Document> coleccion;

    public PagoMongoDAO() {
        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("pagos");
    }


    // =========================
    // AGREGAR PAGO EN UNA TRANSACCION EXISTENTE
    // =========================

    public void agregar(ClientSession session, Pago pago) {
        if (session == null || !session.hasActiveTransaction()) {
            throw new IllegalArgumentException("Se requiere una transacción activa.");
        }
        if (pago == null) {
            throw new IllegalArgumentException("El pago es obligatorio.");
        }

        Document documento = new Document()
                .append("factura_id", new ObjectId(pago.getFacturaId()))
                .append("fecha", pago.getFecha())
                .append("monto", new Decimal128(pago.getMonto()))
                .append("medio_pago", pago.getMedioPago());

        coleccion.insertOne(session, documento);
        pago.setId(documento.getObjectId("_id").toHexString());
    }


}