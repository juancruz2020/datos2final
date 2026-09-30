package org.example.usuarios.dao;

import org.example.conecciones.RedisSingleton;
import redis.clients.jedis.Jedis;

public class SesionDAO {

    private final Jedis redis;

    public SesionDAO() {
        this.redis = RedisSingleton.getInstance();
    }

    public void crearSesion(String id, int segundosExpiracion) {
        String key = "sesion:" + id;

        redis.setex(key, segundosExpiracion, "activa");
    }

    public boolean existeSesion(String id) {
        String key = "sesion:" + id;

        return redis.exists(key);
    }

    public void eliminarSesion(String id) {
        String key = "sesion:" + id;

        redis.del(key);
    }

    public void renovarSesion(String id, int segundosExpiracion) {
        String key = "sesion:" + id;

        redis.expire(key, segundosExpiracion);
    }

    public long obtenerTiempoRestante(String id) {
        String key = "sesion:" + id;

        return redis.ttl(key);
    }
}