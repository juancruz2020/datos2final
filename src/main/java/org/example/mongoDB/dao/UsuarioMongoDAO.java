package org.example.mongoDB.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.model.Usuario;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class UsuarioMongoDAO {

    private final MongoCollection<Document> coleccion;


    // =========================
    // CONSTRUCTOR
    // =========================

    public UsuarioMongoDAO() {

        MongoDatabase database =
                MongoSingleton
                        .getInstance()
                        .getDatabase("datos2");

        this.coleccion =
                database.getCollection("usuarios");
    }


    // =========================
    // AGREGAR USUARIO
    // =========================

    public void agregar(Usuario usuario) {

        Document documento =
                new Document();


        // CLIENTE PUEDE SER NULL

        if (
                usuario.getClienteId() != null
                        &&
                !usuario.getClienteId().isBlank()
        ) {

            documento.append(
                    "cliente_id",
                    new ObjectId(
                            usuario.getClienteId()
                    )
            );

        } else {

            documento.append(
                    "cliente_id",
                    null
            );
        }


        documento
                .append(
                        "rol_id",
                        new ObjectId(
                                usuario.getRolId()
                        )
                )
                .append(
                        "nombre",
                        usuario.getNombre()
                )
                .append(
                        "apellido",
                        usuario.getApellido()
                )
                .append(
                        "email",
                        usuario.getEmail()
                )
                .append(
                        "contraseña_encriptada",
                        usuario.getContraseñaEncriptada()
                )
                .append(
                        "estado",
                        usuario.getEstado()
                )
                .append(
                        "fecha_registro",
                        usuario.getFechaRegistro()
                );


        coleccion.insertOne(
                documento
        );


        usuario.setId(
                documento
                        .getObjectId("_id")
                        .toHexString()
        );
    }


    // =========================
    // MODIFICAR USUARIO
    // =========================

    public void modificar(Usuario usuario) {

        Object clienteIdMongo;


        if (
                usuario.getClienteId() != null
                        &&
                !usuario.getClienteId().isBlank()
        ) {

            clienteIdMongo =
                    new ObjectId(
                            usuario.getClienteId()
                    );

        } else {

            clienteIdMongo = null;
        }


        coleccion.updateOne(

                eq(
                        "_id",
                        new ObjectId(
                                usuario.getId()
                        )
                ),

                combine(

                        set(
                                "cliente_id",
                                clienteIdMongo
                        ),

                        set(
                                "rol_id",
                                new ObjectId(
                                        usuario.getRolId()
                                )
                        ),

                        set(
                                "nombre",
                                usuario.getNombre()
                        ),

                        set(
                                "apellido",
                                usuario.getApellido()
                        ),

                        set(
                                "email",
                                usuario.getEmail()
                        ),

                        set(
                                "contraseña_encriptada",
                                usuario.getContraseñaEncriptada()
                        ),

                        set(
                                "estado",
                                usuario.getEstado()
                        ),

                        set(
                                "fecha_registro",
                                usuario.getFechaRegistro()
                        )
                )
        );
    }


    // =========================
    // ELIMINAR USUARIO
    // =========================

    public void eliminar(String id) {

        coleccion.deleteOne(
                eq(
                        "_id",
                        new ObjectId(id)
                )
        );
    }


    // =========================
    // BUSCAR USUARIO POR ID
    // =========================

    public Document buscarPorId(
            String id
    ) {

        return coleccion
                .find(
                        eq(
                                "_id",
                                new ObjectId(id)
                        )
                )
                .first();
    }


    // =========================
    // LISTAR USUARIOS
    // =========================

    public List<Document> listarTodos() {

        return coleccion
                .find()
                .into(
                        new ArrayList<>()
                );
    }


    // =========================
    // OBTENER TODOS LOS IDS
    // =========================

    public List<String> obtenerTodosLosIds() {

        List<String> ids =
                new ArrayList<>();


        for (
                Document documento :
                coleccion.find()
        ) {

            ObjectId id =
                    documento.getObjectId(
                            "_id"
                    );


            if (id != null) {

                ids.add(
                        id.toHexString()
                );
            }
        }


        return ids;
    }


    // =========================
    // VERIFICAR SI EXISTE POR ID
    // =========================

    public boolean existePorId(
            String id
    ) {

        Document documento =
                coleccion
                        .find(
                                eq(
                                        "_id",
                                        new ObjectId(id)
                                )
                        )
                        .first();


        return documento != null;
    }


    // =========================
    // BUSCAR USUARIO POR EMAIL
    // =========================

    public Document buscarPorEmail(
            String email
    ) {

        return coleccion
                .find(
                        eq(
                                "email",
                                email
                        )
                )
                .first();
    }


    // =========================
    // BUSCAR USUARIOS POR CLIENTE
    // =========================

    public List<Document> buscarPorCliente(
            String clienteId
    ) {

        return coleccion
                .find(
                        eq(
                                "cliente_id",
                                new ObjectId(
                                        clienteId
                                )
                        )
                )
                .into(
                        new ArrayList<>()
                );
    }


    // =========================
    // BUSCAR USUARIOS POR ROL
    // =========================

    public List<Document> buscarPorRol(
            String rolId
    ) {

        return coleccion
                .find(
                        eq(
                                "rol_id",
                                new ObjectId(
                                        rolId
                                )
                        )
                )
                .into(
                        new ArrayList<>()
                );
    }


    // =========================
    // CAMBIAR ROL DE USUARIO
    // =========================

    public void cambiarRol(
            String usuarioId,
            String rolId
    ) {

        coleccion.updateOne(

                eq(
                        "_id",
                        new ObjectId(
                                usuarioId
                        )
                ),

                set(
                        "rol_id",
                        new ObjectId(
                                rolId
                        )
                )
        );
    }


    // =========================
    // CAMBIAR CONTRASEÑA
    // =========================

    public void cambiarContraseña(
            String usuarioId,
            String contraseñaEncriptada
    ) {

        coleccion.updateOne(

                eq(
                        "_id",
                        new ObjectId(
                                usuarioId
                        )
                ),

                set(
                        "contraseña_encriptada",
                        contraseñaEncriptada
                )
        );
    }


    // =========================
    // CAMBIAR ESTADO
    // =========================

    public void cambiarEstado(
            String usuarioId,
            String estado
    ) {

        coleccion.updateOne(

                eq(
                        "_id",
                        new ObjectId(
                                usuarioId
                        )
                ),

                set(
                        "estado",
                        estado
                )
        );
    }
}