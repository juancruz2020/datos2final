package org.example.mongoDB.controller;

import org.bson.Document;
import org.example.mongoDB.service.RolService;

import java.util.List;

class RolController {

    private final RolService rolService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    RolController() {

        this.rolService =
                new RolService();
    }


    // =========================================================
    // LISTAR ROLES
    // =========================================================

    public List<Document> listarRoles() {

        return rolService.listarRoles();
    }


    // =========================================================
    // BUSCAR ROL POR DESCRIPCIÓN
    // =========================================================

    public Document buscarPorDescripcion(
            String descripcion
    ) {

        return rolService.buscarPorDescripcion(
                descripcion
        );
    }
}