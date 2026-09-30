package org.example.mongoDB.dao;

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

        coleccion.insertOne(documento);

        cuentaCorriente.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


    // =========================
    // MODIFICAR CUENTA CORRIENTE
    // =========================

    public void modificar(CuentaCorriente cuentaCorriente) {

        coleccion.updateOne(
                eq("_id", new ObjectId(cuentaCorriente.getId())),
                combine(
                        set(
                                "cliente_id",
                                new ObjectId(cuentaCorriente.getClienteId())
                        ),
                        set(
                                "saldo",
                                new Decimal128(cuentaCorriente.getSaldo())
                        ),
                        set(
                                "fecha_actualizacion",
                                cuentaCorriente.getFechaActualizacion()
                        )
                )
        );
    }


    // =========================
    // ELIMINAR CUENTA CORRIENTE
    // =========================

    public void eliminar(String id) {

        coleccion.deleteOne(
                eq("_id", new ObjectId(id))
        );
    }


    // =========================
    // BUSCAR CUENTA CORRIENTE POR ID
    // =========================

    public Document buscarPorId(String id) {

        return coleccion.find(
                eq("_id", new ObjectId(id))
        ).first();
    }


    // =========================
    // LISTAR CUENTAS CORRIENTES
    // =========================

    public List<Document> listarTodos() {

        return coleccion.find()
                .into(new ArrayList<>());
    }


    // =========================
    // OBTENER TODOS LOS IDS
    // =========================

    public List<String> obtenerTodosLosIds() {

        List<String> ids = new ArrayList<>();

        for (Document documento : coleccion.find()) {

            ObjectId id = documento.getObjectId("_id");

            if (id != null) {
                ids.add(id.toHexString());
            }
        }

        return ids;
    }


    // =========================
    // VERIFICAR SI EXISTE POR ID
    // =========================

    public boolean existePorId(String id) {

        Document documento = coleccion.find(
                eq("_id", new ObjectId(id))
        ).first();

        return documento != null;
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
}