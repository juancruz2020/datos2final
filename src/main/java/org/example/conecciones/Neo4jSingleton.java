package org.example.conecciones;

import io.github.cdimascio.dotenv.Dotenv;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.example.logging.AppLog;

import java.util.concurrent.TimeUnit;

public class Neo4jSingleton {

    private static Driver driver;

    private Neo4jSingleton() {
        // Evita crear objetos de esta clase
    }

    public static synchronized Driver getInstance() {

        if (driver == null) {

            Dotenv dotenv = Dotenv.load();

            String uri = dotenv.get("NEO4J_URI");
            String user = dotenv.get("NEO4J_USER");
            String password = dotenv.get("NEO4J_PASSWORD");

            Config config = Config.builder()
                    .withConnectionTimeout(30, TimeUnit.SECONDS)
                    .withConnectionAcquisitionTimeout(60, TimeUnit.SECONDS)
                    .withMaxConnectionLifetime(600, TimeUnit.MINUTES)
                    .build();

            try {
                driver = GraphDatabase.driver(
                        uri,
                        AuthTokens.basic(user, password),
                        config
                );
                AppLog.info(Neo4jSingleton.class,
                        "Driver Neo4j creado; las conexiones se establecen bajo demanda.");
            } catch (RuntimeException e) {
                AppLog.error(Neo4jSingleton.class,
                        "No se pudo inicializar el driver Neo4j.", e);
                throw e;
            }
        }

        return driver;
    }

    public static synchronized void close() {
        if (driver != null) {
            driver.close();
            driver = null;
            AppLog.info(Neo4jSingleton.class, "Driver Neo4j cerrado.");
        }
    }
}
