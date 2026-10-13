package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Contenedor;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import static com.mongodb.client.model.Updates.unset;

public class ContenedorMongoDAO {

    private final MongoCollection<Document> coleccion;

    public ContenedorMongoDAO() {
        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("contenedores");
    }


    /** Reserva mediante comparación atómica para evitar asignaciones simultáneas. */
    public Document reservarParaEnvio(String id, String envioId, boolean propio) {
        var disponible = or(eq("estado", "DISPONIBLE"), eq("estado", "ACTIVO"));
        var permitido = propio
                ? or(disponible, eq("envio_asignado", envioId),
                    and(eq("estado", "EN_USO"), exists("envio_asignado", false)))
                : disponible;
        return coleccion.findOneAndUpdate(
                and(eq("_id", new ObjectId(id)), permitido),
                combine(set("estado", "EN_USO"), set("envio_asignado", envioId)));
    }

    public void restaurarReserva(Document anterior, String envioId) {
        var propietario = anterior.containsKey("envio_asignado")
                ? set("envio_asignado", anterior.get("envio_asignado"))
                : unset("envio_asignado");
        coleccion.updateOne(and(eq("_id", anterior.getObjectId("_id")),
                        eq("envio_asignado", envioId)),
                combine(set("estado", anterior.get("estado")), propietario));
    }

    public void liberarDeEnvio(String id, String envioId) {
        coleccion.updateOne(and(eq("_id", new ObjectId(id)),
                        or(eq("envio_asignado", envioId), exists("envio_asignado", false))),
                combine(set("estado", "DISPONIBLE"), unset("envio_asignado")));
    }

    // =========================
    // AGREGAR CONTENEDOR
    // =========================

    public void agregar(Contenedor contenedor) {

        Document documento = new Document()
                .append(
                        "codigo_internacional",
                        contenedor.getCodigoInternacional()
                )
                .append("tipo", contenedor.getTipo())
                .append("capacidad", contenedor.getCapacidad())
                .append("estado", contenedor.getEstado());

        coleccion.insertOne(documento);

        contenedor.setId(
                documento.getObjectId("_id").toHexString()
        );
    }


    // =========================
    // MODIFICAR CONTENEDOR
    // =========================

    public void modificar(Contenedor contenedor) {

        coleccion.updateOne(
                eq("_id", new ObjectId(contenedor.getId())),
                combine(
                        set(
                                "codigo_internacional",
                                contenedor.getCodigoInternacional()
                        ),
                        set("tipo", contenedor.getTipo()),
                        set("capacidad", contenedor.getCapacidad()),
                        set("estado", contenedor.getEstado())
                )
        );
    }


    // =========================
    // ELIMINAR CONTENEDOR
    // =========================

    public void eliminar(String id) {

        coleccion.deleteOne(
                eq("_id", new ObjectId(id))
        );
    }


    // =========================
    // BUSCAR CONTENEDOR POR ID
    // =========================

    public Document buscarPorId(String id) {

        return coleccion.find(
                eq("_id", new ObjectId(id))
        ).first();
    }


    // =========================
    // LISTAR CONTENEDORES
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
    // BUSCAR POR CODIGO INTERNACIONAL
    // =========================

    public Document buscarPorCodigo(String codigo) {

        return coleccion.find(
                eq("codigo_internacional", codigo)
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
}