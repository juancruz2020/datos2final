package org.example.neo4j.dao;

import org.example.conecciones.Neo4jSingleton;
import org.example.neo4j.model.Ubicacion;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.util.Map;

public class UbicacionNeo4jDAO {

    private final Driver driver;

    public UbicacionNeo4jDAO() {
        this.driver = Neo4jSingleton.getInstance();
    }

    public void crear(Ubicacion ubicacion) {

        String cypher = """
                MERGE (u:Ubicacion {id: $id})
                SET u.ciudad = $ciudad,
                    u.pais = $pais
                """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "id", ubicacion.getId(),
                            "ciudad", ubicacion.getCiudad(),
                            "pais", ubicacion.getPais()
                    )
            );
        }
    }
}