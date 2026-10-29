
package org.example.mongoDB.dao;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Proveedor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.inc;
import static com.mongodb.client.model.Updates.set;

public class ProveedorMongoDAO {

    private final MongoCollection<Document> coleccion;
    private final MongoCollection<Document> contadores;

    // =========================
    // CONSTRUCTOR
    // =========================

    public ProveedorMongoDAO() {

        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        this.coleccion = database.getCollection("proveedores");
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
                        eq("_id", "proveedores"),
                        inc("secuencia", 1),
                        opciones
                )
                : contadores.findOneAndUpdate(
                        session,
                        eq("_id", "proveedores"),
                        inc("secuencia", 1),
                        opciones
                );

        if (contador == null) {
            throw new IllegalStateException(
                    "No se pudo generar el código del proveedor."
            );
        }

        int numero = ((Number) contador.get("secuencia")).intValue();

        return String.format(
                Locale.ROOT,
                "PROV-%03d",
                numero
        );
    }

    // =========================
    // AGREGAR PROVEEDOR
    // =========================

    public void agregar(Proveedor proveedor) {

        agregar(null, proveedor);
    }

    public void agregar(ClientSession session, Proveedor proveedor) {

        String codigo = generarCodigo(session);

        Document direccion = new Document()
                .append("calle", proveedor.getDireccion().getCalle())
                .append("numero", proveedor.getDireccion().getNumero())
                .append("ciudad", proveedor.getDireccion().getCiudad())
                .append(
                        "codigo_postal",
                        proveedor.getDireccion().getCodigoPostal()
                );

        Document documento = new Document()
                .append("codigo", codigo)
                .append("razon_social", proveedor.getRazonSocial())
                .append("cuit", proveedor.getCuit())
                .append("email", proveedor.getEmail())
                .append("telefono", proveedor.getTelefono())
                .append("direccion", direccion)
                .append("pais", proveedor.getPais())
                .append("estado", proveedor.getEstado());

        if (session == null) {
            coleccion.insertOne(documento);
        } else {
            coleccion.insertOne(session, documento);
        }

        proveedor.setId(
                documento.getObjectId("_id").toHexString()
        );

        proveedor.setCodigo(codigo);
    }

    // =========================
    // MODIFICAR PROVEEDOR
    // =========================

    public void modificar(Proveedor proveedor) {

        coleccion.updateOne(
                eq("_id", new ObjectId(proveedor.getId())),
                combine(
                        set("razon_social", proveedor.getRazonSocial()),
                        set("cuit", proveedor.getCuit()),
                        set("email", proveedor.getEmail()),
                        set("telefono", proveedor.getTelefono()),

                        set(
                                "direccion.calle",
                                proveedor.getDireccion().getCalle()
                        ),
                        set(
                                "direccion.numero",
                                proveedor.getDireccion().getNumero()
                        ),
                        set(
                                "direccion.ciudad",
                                proveedor.getDireccion().getCiudad()
                        ),
                        set(
                                "direccion.codigo_postal",
                                proveedor.getDireccion().getCodigoPostal()
                        ),

                        set("pais", proveedor.getPais()),
                        set("estado", proveedor.getEstado())
                )
        );

        // El código no se modifica.
    }

    // =========================
    // ELIMINAR PROVEEDOR
    // =========================

    public void eliminar(ClientSession session, String id) {

        if (session == null) {
            coleccion.deleteOne(
                    eq("_id", new ObjectId(id))
            );
        } else {
            coleccion.deleteOne(
                    session,
                    eq("_id", new ObjectId(id))
            );
        }
    }

    // =========================
    // BUSCAR PROVEEDOR POR ID
    // =========================

    public Document buscarPorId(String id) {

        return coleccion.find(
                eq("_id", new ObjectId(id))
        ).first();
    }

    // =========================
    // LISTAR TODOS LOS PROVEEDORES
    // =========================

    public List<Document> listarTodos() {

        return coleccion.find()
                .into(new ArrayList<>());
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
    // BUSCAR PROVEEDOR POR CUIT
    // =========================

    public Document buscarPorCuit(String cuit) {

        return coleccion.find(
                eq("cuit", cuit)
        ).first();
    }
}
