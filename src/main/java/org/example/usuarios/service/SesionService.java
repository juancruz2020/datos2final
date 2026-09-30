package org.example.usuarios.service;

import org.example.usuarios.dao.SesionDAO;

public class SesionService {

    private final SesionDAO sesionDAO;

    private static final int DURACION_SESION = 1800; // 30 minutos

    public SesionService() {
        this.sesionDAO = new SesionDAO();
    }

    public void iniciarSesion(String id) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID no puede estar vacío");
        }

        sesionDAO.crearSesion(id, DURACION_SESION);
    }

    public boolean sesionActiva(String id) {

        if (id == null || id.isBlank()) {
            return false;
        }

        return sesionDAO.existeSesion(id);
    }

    public void cerrarSesion(String id) {

        if (id == null || id.isBlank()) {
            return;
        }

        sesionDAO.eliminarSesion(id);
    }

    public void renovarSesion(String id) {

        if (!sesionActiva(id)) {
            throw new IllegalArgumentException("La sesión no existe o expiró");
        }

        sesionDAO.renovarSesion(id, DURACION_SESION);
    }

    public long tiempoRestante(String id) {

        if (!sesionActiva(id)) {
            return -1;
        }

        return sesionDAO.obtenerTiempoRestante(id);
    }
}