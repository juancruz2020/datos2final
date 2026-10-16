
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
import static com.mongodb.client.model.Filters.and;
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
    // AGREGAR MOVIMIENTO
    // =========================================

    public void agregar(
            MovimientoCuentaCorriente movimiento
    ) {

        Document documento = crearDocumento(movimiento);

        coleccion.insertOne(documento);

        movimiento.setId(
                documento.getObjectId("_id").toHexString()
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
    // BUSCAR MOVIMIENTO POR ID
    // =========================================

    public Document buscarPorId(String id) {

        return coleccion.find(
                eq("_id", new ObjectId(id))
        ).first();
    }

    // =========================================
    // LISTAR TODOS LOS MOVIMIENTOS
    // =========================================

    public List<Document> listarTodos() {

        return coleccion.find()
                .sort(ascending("fecha"))
                .into(new ArrayList<>());
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

    // =========================================
    // BUSCAR MOVIMIENTOS POR TIPO
    // =========================================

    public List<Document> buscarPorTipo(String tipo) {

        return coleccion.find(
                eq("tipo", tipo)
        )
        .sort(ascending("fecha"))
        .into(new ArrayList<>());
    }

    // =========================================
    // BUSCAR POR REFERENCIA
    // =========================================

    public Document buscarPorReferencia(
            String cuentaCorrienteId,
            String tipo,
            String referenciaId
    ) {

        return coleccion.find(
                and(
                        eq(
                                "cuenta_corriente_id",
                                new ObjectId(cuentaCorrienteId)
                        ),
                        eq("tipo", tipo),
                        eq("referencia_id", referenciaId)
                )
        ).first();
    }
}
