package org.example.mongoDB.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.dao.ContenedorMongoDAO;
import org.example.mongoDB.model.Contenedor;

import java.util.List;

public class ContenedorService {

    private final ContenedorMongoDAO contenedorDAO;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ContenedorService() {

        this.contenedorDAO =
                new ContenedorMongoDAO();
    }


    // =========================================================
    // CREAR CONTENEDOR
    // =========================================================

    public void crearContenedor(
            String codigoInternacional,
            String tipo,
            double capacidad
    ) {

        if (codigoInternacional == null
                || codigoInternacional.isBlank()) {

            throw new IllegalArgumentException(
                    "El código internacional es obligatorio."
            );
        }

        if (tipo == null
                || tipo.isBlank()) {

            throw new IllegalArgumentException(
                    "El tipo es obligatorio."
            );
        }

        if (capacidad <= 0) {

            throw new IllegalArgumentException(
                    "La capacidad debe ser mayor a 0."
            );
        }


        // -----------------------------------------------------
        // EVITAR CÓDIGOS REPETIDOS
        // -----------------------------------------------------

        Document existente =
                contenedorDAO.buscarPorCodigo(
                        codigoInternacional.trim()
                );

        if (existente != null) {

            throw new IllegalArgumentException(
                    "Ya existe un contenedor con ese código internacional."
            );
        }


        Contenedor contenedor =
                new Contenedor(
                        null,
                        codigoInternacional.trim(),
                        tipo.trim(),
                        capacidad,
                        "ACTIVO"
                );


        contenedorDAO.agregar(
                contenedor
        );
    }


    // =========================================================
    // LISTAR CONTENEDORES
    // =========================================================

    public List<Document> listarContenedores() {

        return contenedorDAO.listarTodos();
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        validarObjectId(
                id
        );


        Document contenedor =
                contenedorDAO.buscarPorId(
                        id
                );


        if (contenedor == null) {

            throw new IllegalArgumentException(
                    "El contenedor no existe."
            );
        }


        return contenedor;
    }


    // =========================================================
    // EXISTE POR ID
    // =========================================================

    public boolean existePorId(
            String id
    ) {

        validarObjectId(
                id
        );


        return contenedorDAO.existePorId(
                id
        );
    }


    // =========================================================
    // OBTENER TODOS LOS IDS
    // =========================================================

    public List<String> obtenerTodosLosIds() {

        return contenedorDAO.obtenerTodosLosIds();
    }


    // =========================================================
    // BUSCAR POR CÓDIGO
    // =========================================================

    public Document buscarPorCodigo(
            String codigo
    ) {

        if (codigo == null
                || codigo.isBlank()) {

            throw new IllegalArgumentException(
                    "El código internacional es obligatorio."
            );
        }


        return contenedorDAO.buscarPorCodigo(
                codigo.trim()
        );
    }


    // =========================================================
    // BUSCAR POR ESTADO
    // =========================================================

    public List<Document> buscarPorEstado(
            String estado
    ) {

        if (estado == null
                || estado.isBlank()) {

            throw new IllegalArgumentException(
                    "El estado es obligatorio."
            );
        }


        return contenedorDAO.buscarPorEstado(
                estado.trim()
        );
    }


    // =========================================================
    // VALIDAR OBJECT ID
    // =========================================================

    private void validarObjectId(
            String id
    ) {

        if (id == null
                || id.isBlank()
                || !ObjectId.isValid(id)) {

            throw new IllegalArgumentException(
                    "El ID del contenedor no es válido."
            );
        }
    }
}