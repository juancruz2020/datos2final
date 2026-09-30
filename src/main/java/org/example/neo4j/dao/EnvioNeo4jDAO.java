package org.example.neo4j.dao;

import org.example.conecciones.Neo4jSingleton;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.util.Map;

public class EnvioNeo4jDAO {

    private final Driver driver;

    public EnvioNeo4jDAO() {
        this.driver = Neo4jSingleton.getInstance();
    }

    public void crearEnvio(
            String id,
            String estado,
            String prioridad
    ) {

        String cypher = """
                CREATE (e:Envio {
                    id: $id,
                    estado: $estado,
                    prioridad: $prioridad
                })
                """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "id", id,
                            "estado", estado,
                            "prioridad", prioridad
                    )
            );
        }
    }

    public void relacionarClienteConEnvio(
            String clienteId,
            String envioId
    ) {

        String cypher = """
                MATCH (c:Cliente {id: $clienteId})
                MATCH (e:Envio {id: $envioId})

                CREATE (c)-[:REALIZA]->(e)
                """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "clienteId", clienteId,
                            "envioId", envioId
                    )
            );
        }
    }
}