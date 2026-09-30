package org.example.usuarios.service;

import org.example.usuarios.dao.SesionDAO;

import java.util.Map;

public class AdministracionSesionService {

    private final SesionDAO sesionDAO;

    public AdministracionSesionService() {

        this.sesionDAO =
                new SesionDAO();
    }

    // =========================================================
    // OBTENER TODAS LAS SESIONES
    // =========================================================

    public Map<String, Long> obtenerSesionesActivas() {

        return sesionDAO.obtenerSesionesActivas();
    }

    // =========================================================
    // CERRAR SESIÓN DE CUALQUIER USUARIO
    // =========================================================

    public void cerrarSesion(
            String usuario
    ) {

        if (usuario == null ||
                usuario.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El usuario es obligatorio."
            );
        }

        sesionDAO.eliminarSesion(
                usuario.trim()
        );
    }

    // =========================================================
    // OBTENER TTL
    // =========================================================

    public long obtenerTiempoRestante(
            String usuario
    ) {

        return sesionDAO.obtenerTiempoRestante(
                usuario
        );
    }
}