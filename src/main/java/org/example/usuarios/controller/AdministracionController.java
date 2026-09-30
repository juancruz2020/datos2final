package org.example.usuarios.controller;

import org.example.usuarios.service.AdministracionSesionService;

import java.util.Map;

public class AdministracionController {

    private final AdministracionSesionService service;

    public AdministracionController() {

        this.service =
                new AdministracionSesionService();
    }

    // =========================================================
    // OBTENER SESIONES ACTIVAS
    // =========================================================

    public Map<String, Long> obtenerSesionesActivas() {

        return service.obtenerSesionesActivas();
    }

    // =========================================================
    // CERRAR SESIÓN
    // =========================================================

    public void cerrarSesion(
            String usuario
    ) {

        service.cerrarSesion(
                usuario
        );
    }

    // =========================================================
    // OBTENER TTL
    // =========================================================

    public long obtenerTiempoRestante(
            String usuario
    ) {

        return service.obtenerTiempoRestante(
                usuario
        );
    }
}