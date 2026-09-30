package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Reporte;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class ReporteMongoDAO {

    private final MongoCollection<Document> coleccion;

    public ReporteMongoDAO() {
        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("reportes");
    }


    // =========================
    // AGREGAR REPORTE
    // =========================

    public void agregar(Reporte reporte) {

        Document documento = new Document()
                .append(
                        "usuario_id",
                        new ObjectId(reporte.getUsuarioId())
                )
                .append("tipo", reporte.getTipo())
                .append("fecha_generacion", reporte.getFechaGeneracion())
                .append("formato", reporte.getFormato())
                .append("estado", reporte.getEstado());

        coleccion.insertOne(documento);

        reporte.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


    // =========================
    // MODIFICAR REPORTE
    // =========================

    public void modificar(Reporte reporte) {

        coleccion.updateOne(
                eq("_id", new ObjectId(reporte.getId())),
                combine(
                        set(
                                "usuario_id",
                                new ObjectId(reporte.getUsuarioId())
                        ),
                        set("tipo", reporte.getTipo()),
                        set("fecha_generacion", reporte.getFechaGeneracion()),
                        set("formato", reporte.getFormato()),
                        set("estado", reporte.getEstado())
                )
        );
    }


    // =========================
    // ELIMINAR REPORTE
    // =========================

    public void eliminar(String id) {

        coleccion.deleteOne(
                eq("_id", new ObjectId(id))
        );
    }


    // =========================
    // BUSCAR REPORTE POR ID
    // =========================

    public Document buscarPorId(String id) {

        return coleccion.find(
                eq("_id", new ObjectId(id))
        ).first();
    }


    // =========================
    // LISTAR REPORTES
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
    // BUSCAR REPORTES POR USUARIO
    // =========================

    public List<Document> buscarPorUsuario(String usuarioId) {

        return coleccion.find(
                eq(
                        "usuario_id",
                        new ObjectId(usuarioId)
                )
        ).into(new ArrayList<>());
    }


    // =========================
    // BUSCAR REPORTES POR TIPO
    // =========================

    public List<Document> buscarPorTipo(String tipo) {

        return coleccion.find(
                eq("tipo", tipo)
        ).into(new ArrayList<>());
    }


    // =========================
    // BUSCAR REPORTES POR ESTADO
    // =========================

    public List<Document> buscarPorEstado(String estado) {

        return coleccion.find(
                eq("estado", estado)
        ).into(new ArrayList<>());
    }
}