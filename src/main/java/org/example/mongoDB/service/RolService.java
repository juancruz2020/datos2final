package org.example.mongoDB.service;

import org.example.consola.TablaConsola;

import org.bson.Document;
import org.example.mongoDB.dao.RolMongoDAO;

import java.util.List;

public class RolService {

    private final RolMongoDAO rolDAO;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RolService() {

        this.rolDAO =
                new RolMongoDAO();
    }


    // =========================================================
    // LISTAR ROLES
    // =========================================================

    public List<Document> listarRoles() {

        return TablaConsola.mostrar("RolService.listarRoles", rolDAO.listarTodos());
    }


    // =========================================================
    // BUSCAR ROL POR DESCRIPCIÓN
    // =========================================================

    public Document buscarPorDescripcion(
            String descripcion
    ) {

        if (descripcion == null
                || descripcion.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "La descripción del rol es obligatoria."
            );
        }

        return TablaConsola.mostrar("RolService.buscarPorDescripcion", rolDAO.buscarPorDescripcion(
                descripcion.trim()
        ));
    }


}