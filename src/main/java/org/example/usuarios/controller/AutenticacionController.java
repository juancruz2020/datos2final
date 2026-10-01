package org.example.usuarios.controller;

import org.bson.Document;
import org.example.usuarios.service.AutenticacionService;

public class AutenticacionController {

    private final AutenticacionService autenticacionService;


    // =========================
    // CONSTRUCTOR
    // =========================

    public AutenticacionController() {
        this.autenticacionService =
                new AutenticacionService();
    }


    // =========================
    // AUTENTICAR USUARIO
    // =========================

    public Document autenticar(
            String email,
            String contraseña
    ) {

        return autenticacionService.autenticar(
                email,
                contraseña
        );
    }
}