
package org.example.mongoDB.dao;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.inc;
import static com.mongodb.client.model.Updates.set;

public class ClienteMongoDAO {

    private final MongoCollection<Document> coleccion;
    private final MongoCollection<Document> contadores;

    // =========================
    // CONSTRUCTOR
    // =========================

    public ClienteMongoDAO() {

        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("clientes");
        this.contadores = database.getCollection("contadores");
    }

    // =========================
    // GENERAR CODIGO AUTOMATICO
    // =========================

    private String generarCodigo(ClientSession session) {

        FindOneAndUpdateOptions opciones = new FindOneAndUpdateOptions()
                .upsert(true)
                .returnDocument(ReturnDocument.AFTER);

        Document contador = session == null
                ? contadores.findOneAndUpdate(
                        eq("_id", "clientes"), inc("secuencia", 1), opciones)
                : contadores.findOneAndUpdate(
                        session, eq("_id", "clientes"), inc("secuencia", 1), opciones);

        if (contador == null) {
            throw new IllegalStateException(
                    "No se pudo generar el código del cliente."
            );
        }

        int numero = ((Number) contador.get("secuencia")).intValue();

        return String.format(
                Locale.ROOT,
                "CLI-%03d",
                numero
        );
    }

    // =========================
    // AGREGAR CLIENTE
    // =========================

    public void agregar(Cliente cliente) {

        agregar(null, cliente);
    }

    public void agregar(ClientSession session, Cliente cliente) {

        String codigo = generarCodigo(session);

        Document direccion = new Document()
                .append("calle", cliente.getDireccion().getCalle())
                .append("numero", cliente.getDireccion().getNumero())
                .append("ciudad", cliente.getDireccion().getCiudad())
                .append(
                        "codigo_postal",
                        cliente.getDireccion().getCodigoPostal()
                );

        Document documento = new Document()
                .append("codigo", codigo)
                .append("razon_social", cliente.getRazonSocial())
                .append("cuit", cliente.getCuit())
                .append("email", cliente.getEmail())
                .append("telefono", cliente.getTelefono())
                .append("direccion", direccion)
                .append("pais", cliente.getPais())
                .append("estado", cliente.getEstado());

        if (session == null) {
            coleccion.insertOne(documento);
        } else {
            coleccion.insertOne(session, documento);
        }

        cliente.setId(
                documento.getObjectId("_id").toHexString()
        );

        cliente.setCodigo(codigo);
    }

    // =========================
    // MODIFICAR CLIENTE
    // =========================

    public void modificar(Cliente cliente) {

        coleccion.updateOne(
                eq("_id", new ObjectId(cliente.getId())),
                combine(
                        set("razon_social", cliente.getRazonSocial()),
                        set("cuit", cliente.getCuit()),
                        set("email", cliente.getEmail()),
                        set("telefono", cliente.getTelefono()),

                        set(
                                "direccion.calle",
                                cliente.getDireccion().getCalle()
                        ),
                        set(
                                "direccion.numero",
                                cliente.getDireccion().getNumero()
                        ),
                        set(
                                "direccion.ciudad",
                                cliente.getDireccion().getCiudad()
                        ),
                        set(
                                "direccion.codigo_postal",
                                cliente.getDireccion().getCodigoPostal()
                        ),

                        set("pais", cliente.getPais()),
                        set("estado", cliente.getEstado())
                )
        );

        // El codigo no se modifica.
    }

    // =========================
    // ELIMINAR CLIENTE
    // =========================

    public void eliminar(String id) {

        coleccion.deleteOne(
                eq("_id", new ObjectId(id))
        );
    }

    // =========================
    // BUSCAR CLIENTE POR ID
    // =========================

    public Document buscarPorId(String id) {

        return coleccion.find(
                eq("_id", new ObjectId(id))
        ).first();
    }

    // =========================
    // LISTAR TODOS LOS CLIENTES
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
    // BUSCAR CLIENTE POR CUIT
    // =========================

    public Document buscarPorCuit(String cuit) {

        return coleccion.find(
                eq("cuit", cuit)
        ).first();
    }

    // =========================
    // BUSCAR CLIENTE POR CODIGO
    // =========================

    public Document buscarPorCodigo(String codigo) {

        return coleccion.find(
                eq("codigo", codigo)
        ).first();
    }
}
