
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
import org.example.mongoDB.model.CierreMensualCuentaCorriente;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;

public class CierreMensualCuentaCorrienteMongoDAO {

    private final MongoCollection<Document> coleccion;

    // =========================================
    // CONSTRUCTOR
    // =========================================

    public CierreMensualCuentaCorrienteMongoDAO() {

        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection(
                "cierres_mensuales_cuenta_corriente"
        );
    }

    // =========================================
    // CREAR ÍNDICE ÚNICO
    // =========================================

    public void crearIndiceUnico() {

        coleccion.createIndex(
                Indexes.compoundIndex(
                        Indexes.ascending("cuenta_corriente_id"),
                        Indexes.ascending("anio"),
                        Indexes.ascending("mes")
                ),
                new IndexOptions().unique(true)
        );
    }

    // =========================================
    // CONSTRUIR DOCUMENTO
    // =========================================

    private Document crearDocumento(
            CierreMensualCuentaCorriente cierre
    ) {

        return new Document()
                .append(
                        "cuenta_corriente_id",
                        new ObjectId(cierre.getCuentaCorrienteId())
                )
                .append("anio", cierre.getAnio())
                .append("mes", cierre.getMes())
                .append(
                        "saldo_inicial",
                        new Decimal128(cierre.getSaldoInicial())
                )
                .append(
                        "total_debitos",
                        new Decimal128(cierre.getTotalDebitos())
                )
                .append(
                        "total_creditos",
                        new Decimal128(cierre.getTotalCreditos())
                )
                .append(
                        "saldo_final",
                        new Decimal128(cierre.getSaldoFinal())
                )
                .append(
                        "fecha_cierre",
                        cierre.getFechaCierre()
                );
    }


    // =========================================
    // AGREGAR CIERRE 
    // =========================================

    public void agregar(
            ClientSession session,
            CierreMensualCuentaCorriente cierre
    ) {

        Document documento = crearDocumento(cierre);

        coleccion.insertOne(session, documento);

        cierre.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


    // =========================================
    // BUSCAR CIERRE POR PERÍODO CON SESIÓN
    // =========================================

    public Document buscarPorPeriodo(
            ClientSession session,
            String cuentaCorrienteId,
            int anio,
            int mes
    ) {

        return coleccion.find(
                session,
                and(
                        eq(
                                "cuenta_corriente_id",
                                new ObjectId(cuentaCorrienteId)
                        ),
                        eq("anio", anio),
                        eq("mes", mes)
                )
        ).first();
    }

    // =========================================
    // LISTAR CIERRES POR CUENTA
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
        .sort(
                descending("anio", "mes")
        )
        .into(new ArrayList<>());
    }


}
