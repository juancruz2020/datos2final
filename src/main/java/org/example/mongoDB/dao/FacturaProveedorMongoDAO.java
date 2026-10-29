
package org.example.mongoDB.dao;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.FacturaProveedor;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

public class FacturaProveedorMongoDAO {

    private final MongoCollection<Document> coleccion;
    private final MongoCollection<Document> contadores;

    // =========================================
    // CONSTRUCTOR
    // =========================================

    public FacturaProveedorMongoDAO() {

        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        coleccion = database.getCollection(
                "facturas_proveedores"
        );

        contadores = database.getCollection(
                "contadores"
        );

        coleccion.createIndex(
                Indexes.compoundIndex(
                        Indexes.ascending("proveedor_id"),
                        Indexes.ascending("numero_factura")
                ),
                new IndexOptions().unique(true)
        );
    }

    // =========================================
    // GENERAR NÚMERO AUTOMÁTICO
    // =========================================

    public String generarNumeroFactura(
            ClientSession session
    ) {

        if (session == null || !session.hasActiveTransaction()) {
            throw new IllegalArgumentException(
                    "La generación del número necesita "
                            + "una transacción activa."
            );
        }

        String contadorId = "facturas_proveedores";

        Document contador = contadores.find(
                session,
                eq("_id", contadorId)
        ).first();

        // Si todavía no existe el contador,
        // considerar las facturas ya registradas.

        if (contador == null) {

            long ultimoNumero = 0;

            for (Document factura : coleccion.find(session)) {

                String numero =
                        factura.getString("numero_factura");

                if (numero == null) {
                    continue;
                }

                String parteNumerica;

                if (numero.matches("FP-\\d+")) {

                    parteNumerica = numero.substring(3);

                } else if (numero.matches("\\d+")) {

                    parteNumerica = numero;

                } else {
                    continue;
                }

                try {

                    long valor =
                            Long.parseLong(parteNumerica);

                    if (valor > ultimoNumero) {
                        ultimoNumero = valor;
                    }

                } catch (NumberFormatException e) {

                    throw new IllegalStateException(
                            "Existe un número de factura "
                                    + "fuera del rango permitido: "
                                    + numero
                    );
                }
            }

            contadores.updateOne(
                    session,
                    eq("_id", contadorId),
                    new Document(
                            "$setOnInsert",
                            new Document(
                                    "secuencia",
                                    ultimoNumero
                            )
                    ),
                    new UpdateOptions().upsert(true)
            );
        }

        // Incrementar el contador dentro de
        // la misma transacción de la factura.

        Document actualizado = contadores.findOneAndUpdate(
                session,
                eq("_id", contadorId),
                new Document(
                        "$inc",
                        new Document("secuencia", 1L)
                ),
                new FindOneAndUpdateOptions()
                        .returnDocument(ReturnDocument.AFTER)
        );

        if (actualizado == null) {
            throw new IllegalStateException(
                    "No se pudo generar el número de factura."
            );
        }

        long numero =
        ((Number) actualizado.get("secuencia")).longValue();
        return String.format(
                java.util.Locale.ROOT,
                "FP-%06d",
                numero
        );
    }

    // =========================================
    // AGREGAR FACTURA
    // =========================================

    public void agregar(
            ClientSession session,
            FacturaProveedor factura
    ) {

        if (session == null || !session.hasActiveTransaction()) {
            throw new IllegalArgumentException(
                    "La factura necesita una transacción activa."
            );
        }

        Document documento = new Document()
                .append(
                        "proveedor_id",
                        new ObjectId(factura.getProveedorId())
                )
                .append(
                        "numero_factura",
                        factura.getNumeroFactura()
                )
                .append(
                        "concepto",
                        factura.getConcepto()
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

        coleccion.insertOne(session, documento);

        factura.setId(
                documento.getObjectId("_id").toHexString()
        );
    }

    // =========================================
    // BUSCAR POR ID
    // =========================================

    public Document buscarPorId(String facturaId) {

        return coleccion.find(
                eq("_id", new ObjectId(facturaId))
        ).first();
    }

    // =========================================
    // BUSCAR POR PROVEEDOR Y NÚMERO
    // =========================================

    public Document buscarPorNumero(
            String proveedorId,
            String numeroFactura
    ) {

        return coleccion.find(
                and(
                        eq("proveedor_id", new ObjectId(proveedorId)),
                        eq("numero_factura", numeroFactura)
                )
        ).first();
    }

    // =========================================
    // LISTAR FACTURAS
    // =========================================

    public List<Document> listarTodos() {

        return coleccion.find()
                .into(new ArrayList<>());
    }

    // =========================================
    // LISTAR FACTURAS DE UN PROVEEDOR
    // =========================================

    public List<Document> buscarPorProveedor(
            String proveedorId
    ) {

        return coleccion.find(
                eq(
                        "proveedor_id",
                        new ObjectId(proveedorId)
                )
        ).into(new ArrayList<>());
    }

    // =========================================
    // ACTUALIZAR ESTADO
    // =========================================

    public void actualizarEstado(
            ClientSession session,
            String facturaId,
            String estado
    ) {

        if (session == null || !session.hasActiveTransaction()) {
            throw new IllegalArgumentException(
                    "La actualización necesita una transacción activa."
            );
        }

        coleccion.updateOne(
                session,
                eq("_id", new ObjectId(facturaId)),
                new Document(
                        "$set",
                        new Document("estado", estado)
                )
        );
    }
}
