
package org.example.DatosPorDefecto;

import com.datastax.oss.driver.api.core.CqlSession;
import com.mongodb.client.MongoDatabase;
import org.example.conecciones.CassandraSingleton;
import org.example.conecciones.MongoSingleton;
import org.example.conecciones.Neo4jSingleton;
import org.example.conecciones.RedisSingleton;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import redis.clients.jedis.Jedis;

public final class EliminarDatos {

    private EliminarDatos() {
    }

    public static void eliminarDatosDePrueba() {
        System.out.println("\n========== RESETEO TOTAL ==========");

        ejecutar("Neo4j", EliminarDatos::eliminarNeo4j);
        ejecutar("MongoDB", EliminarDatos::eliminarMongoDB);
        ejecutar("Redis", EliminarDatos::eliminarRedis);
        ejecutar("Cassandra", EliminarDatos::eliminarCassandra);

        System.out.println("========== RESETEO FINALIZADO ==========\n");
    }

    private static void ejecutar(String motor, Runnable tarea) {
        try {
            tarea.run();
            System.out.println("[OK] " + motor + ": operación completada.");
        } catch (Exception e) {
            System.err.println(
                    "[ERROR] " + motor + ": " + e.getMessage()
            );
        }
    }

    // =====================================================
    // NEO4J: ELIMINAR TODOS LOS NODOS Y RELACIONES
    // =====================================================

    private static void eliminarNeo4j() {
        Driver driver = Neo4jSingleton.getInstance();

        try (Session session = driver.session()) {
            session.run("MATCH (n) DETACH DELETE n").consume();
        }
    }

    // =====================================================
    // MONGODB: ELIMINAR TODA LA BASE datos2
    // =====================================================

    private static void eliminarMongoDB() {
        MongoDatabase database = MongoSingleton
                .getInstance()
                .getDatabase("datos2");

        database.drop();
    }

    // =====================================================
    // REDIS: ELIMINAR TODAS LAS CLAVES DE LA BASE LÓGICA
    // SELECCIONADA POR LA CONEXIÓN
    // =====================================================

    private static void eliminarRedis() {
        Jedis redis = RedisSingleton.getInstance();

        if (redis == null || !redis.isConnected()) {
            throw new IllegalStateException(
                    "Redis no está conectado."
            );
        }

        // Elimina todas las claves de la base lógica seleccionada.
        // No afecta a las demás bases lógicas de Redis.
        redis.flushDB();
    }

    // =====================================================
    // CASSANDRA: ELIMINAR Y RECREAR EL KEYSPACE VACÍO
    // =====================================================

    private static void eliminarCassandra() {
        CqlSession session = CassandraSingleton.getInstance();

        session.execute("DROP KEYSPACE IF EXISTS logistica");

        session.execute("""
                CREATE KEYSPACE logistica
                WITH replication = {
                    'class': 'SimpleStrategy',
                    'replication_factor': 1
                }
                """);
    }
}
