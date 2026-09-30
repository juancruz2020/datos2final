package org.example.neo4j.dao;

import org.example.conecciones.Neo4jSingleton;
import org.example.neo4j.model.Operador;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.util.Map;

public class OperadorNeo4jDAO {

    private final Driver driver;

    public OperadorNeo4jDAO() {
        this.driver = Neo4jSingleton.getInstance();
    }

    public void crear(Operador operador) {

        String cypher = """
                MERGE (o:Operador {id: $id})
                SET o.nombre = $nombre
                """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "id", operador.getId(),
                            "nombre", operador.getNombre()
                    )
            );
        }
    }
}