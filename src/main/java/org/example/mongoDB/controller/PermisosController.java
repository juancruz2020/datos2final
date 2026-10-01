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
    // OBTENER ROL
    // =========================

    public String obtenerRolUsuario(
            String usuarioId
    ) {

        return permisosService
                .obtenerRolUsuario(
                        usuarioId
                );
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


    // =========================
    // VERIFICAR ADMIN
    // =========================

    public boolean esAdmin(
            String usuarioId
    ) {

        return permisosService
                .esAdmin(
                        usuarioId
                );
    }


    // =========================
    // VERIFICAR OPERADOR
    // =========================

    public boolean esOperador(
            String usuarioId
    ) {

        return permisosService
                .esOperador(
                        usuarioId
                );
    }


    // =========================
    // VERIFICAR CLIENTE
    // =========================

    public boolean esCliente(
            String usuarioId
    ) {

        return permisosService
                .esCliente(
                        usuarioId
                );
    }


    // =========================
    // OBTENER CLIENTE
    // =========================

    public String obtenerClienteId(
            String usuarioId
    ) {

        return permisosService
                .obtenerClienteId(
                        usuarioId
                );
    }
}