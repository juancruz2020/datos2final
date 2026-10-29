
package org.example.mongoDB.dao;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.PagoProveedor;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class PagoProveedorMongoDAO {

    private final MongoCollection<Document> coleccion;

    // =========================================
    // CONSTRUCTOR
    // =========================================

    public PagoProveedorMongoDAO() {

        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        coleccion = database.getCollection("pagos_proveedores");

        coleccion.createIndex(
                Indexes.ascending("factura_proveedor_id")
        );
    }

    // =========================================
    // AGREGAR PAGO
    // =========================================

    public void agregar(
            ClientSession session,
            PagoProveedor pago
    ) {

        if (session == null || !session.hasActiveTransaction()) {
            throw new IllegalArgumentException(
                    "El pago necesita una transacción activa."
            );
        }

        Document documento = new Document()
                .append(
                        "factura_proveedor_id",
                        new ObjectId(pago.getFacturaProveedorId())
                )
                .append("fecha", pago.getFecha())
                .append(
                        "importe",
                        new Decimal128(pago.getImporte())
                )
                .append("metodo_pago", pago.getMetodoPago());

        coleccion.insertOne(session, documento);

        pago.setId(
                documento.getObjectId("_id").toHexString()
        );
    }

    // =========================================
    // BUSCAR PAGO POR ID
    // =========================================

    public Document buscarPorId(String pagoId) {

        return coleccion.find(
                eq("_id", new ObjectId(pagoId))
        ).first();
    }

    // =========================================
    // LISTAR PAGOS DE UNA FACTURA
    // =========================================

    public List<Document> buscarPorFactura(
            String facturaProveedorId
    ) {

        return coleccion.find(
                eq(
                        "factura_proveedor_id",
                        new ObjectId(facturaProveedorId)
                )
        ).into(new ArrayList<>());
    }

    // =========================================
    // LISTAR TODOS LOS PAGOS
    // =========================================

    public List<Document> listarTodos() {

        return coleccion.find()
                .into(new ArrayList<>());
    }
}
