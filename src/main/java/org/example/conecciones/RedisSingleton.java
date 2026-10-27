package org.example.conecciones;

import io.github.cdimascio.dotenv.Dotenv;
import redis.clients.jedis.Jedis;
import org.example.logging.AppLog;

public class RedisSingleton {

    private static Jedis jedis;

    private RedisSingleton() {
        // Evita crear objetos de esta clase
    }

    public static Jedis getInstance() {

        if (jedis == null) {

            Dotenv dotenv = Dotenv.load();

            String host = dotenv.get("REDIS_HOST");
            int port = Integer.parseInt(dotenv.get("REDIS_PORT"));
            String password = dotenv.get("REDIS_PASSWORD");

            try {
                jedis = new Jedis(host, port);
                jedis.auth(password);
                AppLog.info(RedisSingleton.class, "Conexión Redis inicializada.");
            } catch (RuntimeException e) {
                if (jedis != null) {
                    jedis.close();
                    jedis = null;
                }
                AppLog.error(RedisSingleton.class,
                        "No se pudo inicializar la conexión Redis.", e);
                throw e;
            }
        }

        return jedis;
    }

    public static void close() {

        if (jedis != null) {
            jedis.close();
            jedis = null;
            AppLog.info(RedisSingleton.class, "Conexión Redis cerrada.");
        }
    }
}
