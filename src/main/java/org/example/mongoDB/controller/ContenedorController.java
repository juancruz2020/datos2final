package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.service.ContenedorService;

import java.util.List;

public class ContenedorController {

    private final ContenedorService contenedorService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ContenedorController() {

        this.contenedorService =
                new ContenedorService();
    }


    // =========================================================
    // CREAR CONTENEDOR
    // =========================================================

    public void crearContenedor(
            String codigoInternacional,
            String tipo,
            double capacidad
    ) {

        contenedorService.crearContenedor(
                codigoInternacional,
                tipo,
                capacidad
        );
    }


    // =========================================================
    // MODIFICAR CONTENEDOR
    // =========================================================

    public void modificarContenedor(
            String id,
            String codigoInternacional,
            String tipo,
            double capacidad,
            String estado
    ) {

        contenedorService.modificarContenedor(
                id,
                codigoInternacional,
                tipo,
                capacidad,
                estado
        );
    }


    // =========================================================
    // ELIMINAR CONTENEDOR
    // =========================================================

    public void eliminarContenedor(
            String id
    ) {

        contenedorService.eliminarContenedor(
                id
        );
    }


    // =========================================================
    // LISTAR CONTENEDORES
    // =========================================================

    public List<Document> listarContenedores() {

        return contenedorService
                .listarContenedores();
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    public Document buscarPorId(
            String id
    ) {

        return contenedorService
                .buscarPorId(
                        id
                );
    }


}