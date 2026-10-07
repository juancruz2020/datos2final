package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Vehiculo;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class VehiculoMongoDAO {

    private final MongoCollection<Document> coleccion;

    public VehiculoMongoDAO() {

        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("vehiculos");
    }


    // =========================
    // AGREGAR VEHICULO
    // =========================

    public void agregar(Vehiculo vehiculo) {

        Document documento = new Document()
                .append("tipo", vehiculo.getTipo())
                .append("identificacion", vehiculo.getIdentificacion())
                .append("empresa_operadora", vehiculo.getEmpresaOperadora())
                .append("estado", vehiculo.getEstado());

        coleccion.insertOne(documento);

        vehiculo.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


    // =========================
    // MODIFICAR VEHICULO
    // =========================

    public void modificar(Vehiculo vehiculo) {

        coleccion.updateOne(
                eq("_id", new ObjectId(vehiculo.getId())),
                combine(
                        set("tipo", vehiculo.getTipo()),
                        set("identificacion", vehiculo.getIdentificacion()),
                        set("empresa_operadora", vehiculo.getEmpresaOperadora()),
                        set("estado", vehiculo.getEstado())
                )
        );
    }


    // =========================
    // ELIMINAR VEHICULO
    // =========================

    public void eliminar(String id) {

        coleccion.deleteOne(
                eq("_id", new ObjectId(id))
        );
    }


    // =========================
    // BUSCAR VEHICULO POR ID
    // =========================

    public Document buscarPorId(String id) {

        return coleccion.find(
                eq("_id", new ObjectId(id))
        ).first();
    }


    // =========================
    // LISTAR VEHICULOS
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
    // BUSCAR POR IDENTIFICACION
    // =========================

    public Document buscarPorIdentificacion(String identificacion) {

        return coleccion.find(
                eq("identificacion", identificacion)
        ).first();
    }


    // =========================
    // BUSCAR POR ESTADO
    // =========================

    public List<Document> buscarPorEstado(String estado) {

        return coleccion.find(
                eq("estado", estado)
        ).into(new ArrayList<>());
    }


    // =========================
    // BUSCAR POR TIPO
    // =========================

    public List<Document> buscarPorTipo(String tipo) {

        return coleccion.find(
                eq("tipo", tipo)
        ).into(new ArrayList<>());
    }
}