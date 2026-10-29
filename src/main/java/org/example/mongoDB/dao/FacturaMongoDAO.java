
package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Factura;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class FacturaMongoDAO {

    private final MongoCollection<Document> coleccion;

    public FacturaMongoDAO() {
        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("facturas");
    }

    // =========================
    // AGREGAR FACTURA
    // =========================

    public void agregar(Factura factura) {

        Document documento = new Document()
                .append(
                        "cliente_id",
                        new ObjectId(factura.getClienteId())
                )
                .append(
                        "fecha_emision",
                        factura.getFechaEmision()
                )
                .append(
                        "fecha_vencimiento",
                        factura.getFechaVencimiento()
                )
                .append(
                        "importe_total",
                        new Decimal128(factura.getImporteTotal())
                )
                .append(
                        "estado",
                        factura.getEstado()
                );

        coleccion.insertOne(documento);

        factura.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


    // =========================
    // ACTUALIZAR VENCIMIENTO Y ESTADO
    // =========================

    public void actualizarVencimientoYEstado(
            String facturaId,
            java.util.Date fechaVencimiento,
            String estado
    ) {
        coleccion.updateOne(
                eq("_id", new ObjectId(facturaId)),
                combine(
                        set("fecha_vencimiento", fechaVencimiento),
                        set("estado", estado)
                )
        );
    }


    // =========================
    // BUSCAR FACTURA POR ID
    // =========================

    public Document buscarPorId(String id) {

        return coleccion.find(
                eq("_id", new ObjectId(id))
        ).first();
    }

    // =========================
    // LISTAR FACTURAS
    // =========================

    public List<Document> listarTodos() {

        return coleccion.find()
                .into(new ArrayList<>());
    }


    // =========================
    // BUSCAR FACTURAS POR CLIENTE
    // =========================

    public List<Document> buscarPorCliente(String clienteId) {

        return coleccion.find(
                eq(
                        "cliente_id",
                        new ObjectId(clienteId)
                )
        ).into(new ArrayList<>());
    }


    // =========================
    // ELIMINAR FACTURAS POR CLIENTE
    // =========================

    public void eliminarPorCliente(
            com.mongodb.client.ClientSession session,
            String clienteId
    ) {
        coleccion.deleteMany(
                session,
                eq("cliente_id", new ObjectId(clienteId))
        );
    }



}
