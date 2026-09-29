package org.example.conecciones;

import io.github.cdimascio.dotenv.Dotenv;
import redis.clients.jedis.Jedis;

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

            jedis = new Jedis(host, port);
        }

        return jedis;
    }

    public static void close() {

        if (jedis != null) {
            jedis.close();
            jedis = null;
        }
    }
}