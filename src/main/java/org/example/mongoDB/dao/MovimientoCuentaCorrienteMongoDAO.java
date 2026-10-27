
package org.example.mongoDB.dao;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.MovimientoCuentaCorriente;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.ascending;

public class MovimientoCuentaCorrienteMongoDAO {

    private final MongoCollection<Document> coleccion;

    // =========================================
    // CONSTRUCTOR
    // =========================================

    public MovimientoCuentaCorrienteMongoDAO() {

        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection(
                "movimientos_cuenta_corriente"
        );
    }

    // =========================================
    // CREAR ÍNDICE PARA EVITAR DUPLICADOS
    // =========================================

    public void crearIndiceUnico() {

        coleccion.createIndex(
                Indexes.compoundIndex(
                        Indexes.ascending("cuenta_corriente_id"),
                        Indexes.ascending("tipo"),
                        Indexes.ascending("referencia_id")
                ),
                new IndexOptions()
                        .unique(true)
                        .partialFilterExpression(
                                new Document(
                                        "referencia_id",
                                        new Document("$type", "string")
                                )
                        )
        );
    }

    // =========================================
    // CONSTRUIR DOCUMENTO
    // =========================================

    private Document crearDocumento(
            MovimientoCuentaCorriente movimiento
    ) {

        return new Document()
                .append(
                        "cuenta_corriente_id",
                        new ObjectId(movimiento.getCuentaCorrienteId())
                )
                .append(
                        "tipo",
                        movimiento.getTipo()
                )
                .append(
                        "referencia_id",
                        movimiento.getReferenciaId()
                )
                .append(
                        "descripcion",
                        movimiento.getDescripcion()
                )
                .append(
                        "importe",
                        new Decimal128(movimiento.getImporte())
                )
                .append(
                        "fecha",
                        movimiento.getFecha()
                );
    }


    // =========================================
    // AGREGAR MOVIMIENTO CON TRANSACCIÓN
    // =========================================

    public void agregar(
            ClientSession session,
            MovimientoCuentaCorriente movimiento
    ) {

        Document documento = crearDocumento(movimiento);

        coleccion.insertOne(session, documento);

        movimiento.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


    // =========================================
    // BUSCAR MOVIMIENTOS POR CUENTA
    // =========================================

    public List<Document> buscarPorCuenta(
            String cuentaCorrienteId
    ) {

        return coleccion.find(
                eq(
                        "cuenta_corriente_id",
                        new ObjectId(cuentaCorrienteId)
                )
        )
        .sort(ascending("fecha"))
        .into(new ArrayList<>());
    }


}
