package org.example.mongoDB.dao;

import java.math.BigDecimal;
import java.util.Date;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.CuentaCorriente;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class CuentaCorrienteMongoDAO {

    private final MongoCollection<Document> coleccion;

    public CuentaCorrienteMongoDAO() {

        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("cuentas_corrientes");
    }

    // =========================
    // AGREGAR CUENTA CORRIENTE
    // =========================

    public void agregar(CuentaCorriente cuentaCorriente) {

        agregar(null, cuentaCorriente);
    }

    public void agregar(
            ClientSession session,
            CuentaCorriente cuentaCorriente
    ) {

        Document documento = new Document()
                .append(
                        "cliente_id",
                        new ObjectId(cuentaCorriente.getClienteId())
                )
                .append(
                        "saldo",
                        new Decimal128(cuentaCorriente.getSaldo())
                )
                .append(
                        "fecha_actualizacion",
                        cuentaCorriente.getFechaActualizacion()
                );

        if (session == null) {
            coleccion.insertOne(documento);
        } else {
            coleccion.insertOne(session, documento);
        }

        cuentaCorriente.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


    // =========================
    // LISTAR CUENTAS CORRIENTES
    // =========================

    public List<Document> listarTodos() {

        return coleccion.find()
                .into(new ArrayList<>());
    }


    // =========================
    // BUSCAR CUENTA CORRIENTE POR CLIENTE
    // =========================

    public Document buscarPorCliente(String clienteId) {

        return coleccion.find(
                eq(
                        "cliente_id",
                        new ObjectId(clienteId)
                )
        ).first();
    }


    // =========================================
    // BUSCAR POR ID CON TRANSACCIÓN
    // =========================================

    public Document buscarPorId(
            ClientSession session,
            String cuentaId
    ) {

        return coleccion.find(
                session,
                eq("_id", new ObjectId(cuentaId))
        ).first();
    }

    // =========================================
    // ACTUALIZAR SALDO CON TRANSACCIÓN
    // =========================================

    public void actualizarSaldo(
            ClientSession session,
            String cuentaId,
            BigDecimal nuevoSaldo
    ) {

        var resultado = coleccion.updateOne(
                session,
                eq("_id", new ObjectId(cuentaId)),
                combine(
                        set("saldo", new Decimal128(nuevoSaldo)),
                        set("fecha_actualizacion", new Date())
                )
        );

        if (resultado.getMatchedCount() == 0) {
            throw new IllegalArgumentException(
                    "No se encontró la cuenta corriente."
            );
        }
    }
}
