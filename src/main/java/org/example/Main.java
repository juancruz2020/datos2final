package org.example;

import com.datastax.oss.driver.api.core.CqlSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.example.conecciones.CassandraSingleton;
import org.example.conecciones.MongoSingleton;
import org.example.conecciones.Neo4jSingleton;
import org.example.conecciones.RedisSingleton;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import redis.clients.jedis.Jedis;

public class Main {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("   PRUEBA DE BASES DE DATOS");
        System.out.println("=================================\n");

        probarCassandra();
        probarMongo();
        probarNeo4j();
        probarRedis();

        System.out.println("\n=================================");
        System.out.println("       PRUEBAS FINALIZADAS");
        System.out.println("=================================");

        cerrarConexiones();
    }

    private static void probarCassandra() {

        try {
            CqlSession session = CassandraSingleton.getInstance();

            session.execute("SELECT release_version FROM system.local");

            System.out.println("Cassandra: OK");

        } catch (Exception e) {

            System.out.println("Cassandra: ERROR");
            System.out.println("   " + e.getMessage());
        }
    }

    private static void probarMongo() {

        try {
            MongoClient client = MongoSingleton.getInstance();

            MongoDatabase database = client.getDatabase("datos2");

            database.runCommand(
                    new org.bson.Document("ping", 1)
            );

            System.out.println("MongoDB: OK");

        } catch (Exception e) {

            System.out.println("MongoDB: ERROR");
            System.out.println("   " + e.getMessage());
        }
    }

    private static void probarNeo4j() {

        try {
            Driver driver = Neo4jSingleton.getInstance();

            try (Session session = driver.session()) {

                session.run("RETURN 1").consume();
            }

            System.out.println("Neo4j: OK");

        } catch (Exception e) {

            System.out.println("Neo4j: ERROR");
            System.out.println("   " + e.getMessage());
        }
    }

    private static void probarRedis() {

        try {
            Jedis jedis = RedisSingleton.getInstance();

            String respuesta = jedis.ping();

            if ("PONG".equalsIgnoreCase(respuesta)) {
                System.out.println("Redis: OK");
            } else {
                System.out.println("Redis: ERROR");
                System.out.println("   Respuesta: " + respuesta);
            }

        } catch (Exception e) {

            System.out.println("Redis: ERROR");
            System.out.println("   " + e.getMessage());
        }
    }

    private static void cerrarConexiones() {

        CassandraSingleton.close();
        MongoSingleton.close();
        Neo4jSingleton.close();
        RedisSingleton.close();
    }
}