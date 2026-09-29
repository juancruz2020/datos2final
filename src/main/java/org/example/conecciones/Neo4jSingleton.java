package org.example.conecciones;

import io.github.cdimascio.dotenv.Dotenv;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class Neo4jSingleton {

    private static Driver driver;

    private Neo4jSingleton() {
        // Evita crear objetos de esta clase
    }

    public static Driver getInstance() {

        if (driver == null) {

            Dotenv dotenv = Dotenv.load();

            String uri = dotenv.get("NEO4J_URI");
            String user = dotenv.get("NEO4J_USER");
            String password = dotenv.get("NEO4J_PASSWORD");

            driver = GraphDatabase.driver(
                    uri,
                    AuthTokens.basic(user, password)
            );
        }

        return driver;
    }

    public static void close() {

        if (driver != null) {
            driver.close();
            driver = null;
        }
    }
}