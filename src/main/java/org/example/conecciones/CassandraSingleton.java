package org.example.conecciones;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.InetSocketAddress;
import java.time.Duration;

public class CassandraSingleton {

    private static CqlSession session;

    private CassandraSingleton() {
        // Evita que se creen objetos de esta clase desde afuera
    }

    public static CqlSession getInstance() {

        if (session == null) {

            Dotenv dotenv = Dotenv.load();

            String host = dotenv.get("CASSANDRA_HOST");
            int port = Integer.parseInt(dotenv.get("CASSANDRA_PORT"));
            String datacenter = dotenv.get("CASSANDRA_DATACENTER");

            session = new CqlSessionBuilder()
                    .addContactPoint(
                            new InetSocketAddress(host, port)
                    )
                    .withLocalDatacenter(datacenter)
                    .withConfigLoader(
                            DriverConfigLoader.programmaticBuilder()
                                    .withDuration(
                                            DefaultDriverOption.REQUEST_TIMEOUT,
                                            Duration.ofSeconds(10)
                                    )
                                    .build()
                    )
                    .build();

            crearKeyspace();
        }

        return session;
    }

    private static void crearKeyspace() {

        session.execute("""
            CREATE KEYSPACE IF NOT EXISTS logistica
            WITH replication = {
                'class': 'SimpleStrategy',
                'replication_factor': 1
            }
            """);
    }

    public static void close() {

        if (session != null) {
            session.close();
            session = null;
        }
    }
}