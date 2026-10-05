package org.example.usuarios.service;

import org.bson.Document;
import org.example.mongoDB.dao.UsuarioMongoDAO;

public class AutenticacionService {

    private final UsuarioMongoDAO usuarioDAO;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AutenticacionService() {

        this.usuarioDAO =
                new UsuarioMongoDAO();
    }


    // =========================================================
    // AUTENTICAR USUARIO
    // =========================================================

    public Document autenticar(
            String email,
            String contraseña
    ) {

        // -----------------------------------------------------
        // VALIDAR EMAIL
        // -----------------------------------------------------

        if (email == null
                || email.isBlank()) {

            throw new IllegalArgumentException(
                    "El email es obligatorio"
            );
        }


        // -----------------------------------------------------
        // VALIDAR CONTRASEÑA
        // -----------------------------------------------------

        if (contraseña == null
                || contraseña.isBlank()) {

            throw new IllegalArgumentException(
                    "La contraseña es obligatoria"
            );
        }


        // -----------------------------------------------------
        // BUSCAR USUARIO
        // -----------------------------------------------------

        Document usuario =
                usuarioDAO.buscarPorEmail(
                        email.trim().toLowerCase()
                );


        if (usuario == null) {

            return null;
        }


        // -----------------------------------------------------
        // VALIDAR ESTADO
        // -----------------------------------------------------

        String estado =
                usuario.getString(
                        "estado"
                );


        if (estado == null
                || !"ACTIVO".equalsIgnoreCase(
                        estado
                )) {

            throw new SecurityException(
                    "El usuario no se encuentra activo"
            );
        }


        // -----------------------------------------------------
        // OBTENER CONTRASEÑA GUARDADA
        // -----------------------------------------------------

        String contraseñaGuardada =
                usuario.getString(
                        "contraseña_encriptada"
                );


        if (contraseñaGuardada == null
                || contraseñaGuardada.isBlank()) {

            throw new IllegalStateException(
                    "El usuario no tiene una contraseña configurada"
            );
        }


        // -----------------------------------------------------
        // COMPARAR CONTRASEÑA
        // -----------------------------------------------------

        if (!contraseña.equals(
                contraseñaGuardada
        )) {

            return null;
        }


        return usuario;
    }


    // =========================================================
    // GUARDAR CONTRASEÑA
    // =========================================================

    public String encriptarContraseña(
            String contraseña
    ) {

        if (contraseña == null
                || contraseña.isBlank()) {

            throw new IllegalArgumentException(
                    "La contraseña es obligatoria"
            );
        }


        // Ya no se utiliza BCrypt.
        // Se devuelve la contraseña directamente.
        return contraseña;
    }
}