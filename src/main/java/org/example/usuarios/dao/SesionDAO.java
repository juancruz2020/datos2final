package org.example.usuarios.dao;

import org.example.conecciones.RedisSingleton;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SesionDAO {

    private static final String PREFIJO_SESION = "sesion:";

    private final Jedis redis;

    public SesionDAO() {

        this.redis = RedisSingleton.getInstance();
    }

    // =========================================================
    // CREAR SESIÓN
    // =========================================================

    public void crearSesion(
            String id,
            int segundosExpiracion
    ) {

        String key =
                PREFIJO_SESION + id;

        redis.setex(
                key,
                segundosExpiracion,
                "activa"
        );
    }

    // =========================================================
    // EXISTE SESIÓN
    // =========================================================

    public boolean existeSesion(
            String id
    ) {

        String key =
                PREFIJO_SESION + id;

        return redis.exists(key);
    }

    // =========================================================
    // ELIMINAR SESIÓN
    // =========================================================

    public void eliminarSesion(
            String id
    ) {

        String key =
                PREFIJO_SESION + id;

        redis.del(key);
    }

    // =========================================================
    // RENOVAR SESIÓN
    // =========================================================

    public void renovarSesion(
            String id,
            int segundosExpiracion
    ) {

        String key =
                PREFIJO_SESION + id;

        redis.expire(
                key,
                segundosExpiracion
        );
    }

    // =========================================================
    // OBTENER TTL
    // =========================================================

    public long obtenerTiempoRestante(
            String id
    ) {

        String key =
                PREFIJO_SESION + id;

        return redis.ttl(key);
    }

    // =========================================================
    // OBTENER TODAS LAS SESIONES ACTIVAS
    // =========================================================

    public Map<String, Long> obtenerSesionesActivas() {

        Map<String, Long> sesiones =
                new LinkedHashMap<>();

        String cursor = ScanParams.SCAN_POINTER_START;

        ScanParams parametros =
                new ScanParams()
                        .match(PREFIJO_SESION + "*")
                        .count(100);

        do {

            var resultado =
                    redis.scan(
                            cursor,
                            parametros
                    );

            cursor =
                    resultado.getCursor();

            List<String> claves =
                    resultado.getResult();

            for (String clave : claves) {

                if (!clave.startsWith(PREFIJO_SESION)) {
                    continue;
                }

                String usuario =
                        clave.substring(
                                PREFIJO_SESION.length()
                        );

                long ttl =
                        redis.ttl(clave);

                if (ttl >= 0) {

                    sesiones.put(
                            usuario,
                            ttl
                    );
                }
            }

        } while (
                !cursor.equals(
                        ScanParams.SCAN_POINTER_START
                )
        );

        return sesiones;
    }

    // =========================================================
    // OBTENER USUARIOS CON SESIÓN ACTIVA
    // =========================================================

    public List<String> obtenerUsuariosConSesionActiva() {

        return new ArrayList<>(
                obtenerSesionesActivas().keySet()
        );
    }
}