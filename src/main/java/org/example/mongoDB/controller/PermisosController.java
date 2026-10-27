package org.example.mongoDB.controller;

import org.example.mongoDB.service.PermisosService;

public class PermisosController {

    private final PermisosService permisosService;


    // =========================
    // CONSTRUCTOR
    // =========================

    public PermisosController() {

        this.permisosService =
                new PermisosService();
    }


    // =========================
    // VERIFICAR ACCESO
    // A UNA VISTA
    // =========================

    public boolean puedeAccederVista(
            String usuarioId,
            String vista
    ) {

        return permisosService
                .puedeAccederVista(
                        usuarioId,
                        vista
                );
    }


}