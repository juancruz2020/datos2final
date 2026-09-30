package org.example.neo4j.dao;

import org.example.conecciones.Neo4jSingleton;
import org.example.neo4j.model.Contenedor;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.util.Map;

public class ContenedorNeo4jDAO {

    private final Driver driver;

    public ContenedorNeo4jDAO() {
        this.driver = Neo4jSingleton.getInstance();
    }

    public void crear(Contenedor contenedor) {

        String cypher = """
                MERGE (c:Contenedor {id: $id})
                SET c.codigo = $codigo,
                    c.tipo = $tipo
                """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "id", contenedor.getId(),
                            "codigo", contenedor.getCodigo(),
                            "tipo", contenedor.getTipo()
                    )
            );
        }
    }
}