package org.example.usuarios.controller;

import org.example.usuarios.service.SesionService;

public class SesionController {

    private final SesionService sesionService;

    public SesionController() {
        this.sesionService = new SesionService();
    }

    public void iniciarSesion(String id) {
        sesionService.iniciarSesion(id);
    }

    public boolean verificarSesion(String id) {
        return sesionService.sesionActiva(id);
    }

    public void cerrarSesion(String id) {
        sesionService.cerrarSesion(id);
    }

    public void renovarSesion(String id) {
        sesionService.renovarSesion(id);
    }

    public long obtenerTiempoRestante(String id) {
        return sesionService.tiempoRestante(id);
    }
}