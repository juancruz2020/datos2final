package org.example.neo4j.dao;

import org.example.conecciones.Neo4jSingleton;
import org.example.neo4j.model.Vehiculo;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.util.Map;

public class VehiculoNeo4jDAO {

    private final Driver driver;

    public VehiculoNeo4jDAO() {
        this.driver = Neo4jSingleton.getInstance();
    }

    public void crear(Vehiculo vehiculo) {

        String cypher = """
                MERGE (v:Vehiculo {id: $id})
                SET v.patente = $patente,
                    v.tipo = $tipo
                """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "id", vehiculo.getId(),
                            "patente", vehiculo.getPatente(),
                            "tipo", vehiculo.getTipo()
                    )
            );
        }
    }
}