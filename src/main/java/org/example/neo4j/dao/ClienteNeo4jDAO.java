package org.example.neo4j.dao;

import org.example.conecciones.Neo4jSingleton;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

public class ClienteNeo4jDAO {

    private final Driver driver;

    public ClienteNeo4jDAO() {
        this.driver = Neo4jSingleton.getInstance();
    }

    public void crearCliente(
            String id,
            String razonSocial,
            String cuit
    ) {

        String cypher = """
                CREATE (c:Cliente {
                    id: $id,
                    razonSocial: $razonSocial,
                    cuit: $cuit
                })
                """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    java.util.Map.of(
                            "id", id,
                            "razonSocial", razonSocial,
                            "cuit", cuit
                    )
            );
        }
    }
}