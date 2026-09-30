package org.example.neo4j.dao;

import org.example.conecciones.Neo4jSingleton;
import org.example.neo4j.model.Proveedor;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.util.Map;

public class ProveedorNeo4jDAO {

    private final Driver driver;

    public ProveedorNeo4jDAO() {
        this.driver = Neo4jSingleton.getInstance();
    }

    public void crear(Proveedor proveedor) {

        String cypher = """
                MERGE (p:Proveedor {id: $id})
                SET p.nombre = $nombre
                """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "id", proveedor.getId(),
                            "nombre", proveedor.getNombre()
                    )
            );
        }
    }
}