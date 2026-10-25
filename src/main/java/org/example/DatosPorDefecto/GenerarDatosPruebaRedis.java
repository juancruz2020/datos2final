package org.example.DatosPorDefecto;

import org.example.usuarios.dao.SesionDAO;
import org.example.logging.AppLog;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GenerarDatosPruebaRedis {

    private static final int DURACION_SESION_SEGUNDOS = 1800;

    private GenerarDatosPruebaRedis() {
        // Clase utilitaria.
    }

    /**
     * Crea sesiones Redis de prueba para usuarios que ya existen en MongoDB.
     * Se reutiliza SesionDAO para mantener exactamente el formato de usuarios.
     */
    public static void generar(List<String> idsUsuarios) {
        if (idsUsuarios == null || idsUsuarios.isEmpty()) {
            AppLog.warning(GenerarDatosPruebaRedis.class,
                    "Sin usuarios para crear sesiones Redis de prueba.");
            return;
        }

        SesionDAO sesionDAO = new SesionDAO();
        Set<String> idsValidos = new LinkedHashSet<>();

        for (String id : idsUsuarios) {
            if (id != null && !id.isBlank()) {
                idsValidos.add(id.trim());
            }
        }

        for (String id : idsValidos) {
            sesionDAO.crearSesion(id, DURACION_SESION_SEGUNDOS);
        }

        AppLog.info(GenerarDatosPruebaRedis.class,
                "Sesiones de prueba Redis generadas: " + idsValidos.size());
    }
}
