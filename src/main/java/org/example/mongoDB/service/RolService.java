package org.example.mongoDB.service;

import org.bson.Document;
import org.bson.types.ObjectId;
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

        return rolDAO.listarTodos();
    }


    // =========================================================
    // BUSCAR ROL POR ID
    // =========================================================

    public Document buscarPorId(
            String rolId
    ) {

        validarObjectId(
                rolId,
                "El ID del rol no es válido."
        );

        Document rol =
                rolDAO.buscarPorId(
                        rolId
                );

        if (rol == null) {

            throw new IllegalArgumentException(
                    "El rol no existe."
            );
        }

        return rol;
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

        return rolDAO.buscarPorDescripcion(
                descripcion.trim()
        );
    }


    // =========================================================
    // VALIDAR OBJECT ID
    // =========================================================

    private void validarObjectId(
            String id,
            String mensaje
    ) {

        if (id == null
                || id.isBlank()
                || !ObjectId.isValid(id)) {

            throw new IllegalArgumentException(
                    mensaje
            );
        }
    }
}