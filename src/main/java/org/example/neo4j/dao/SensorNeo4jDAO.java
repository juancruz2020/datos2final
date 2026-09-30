package org.example.neo4j.dao;

import org.example.conecciones.Neo4jSingleton;
import org.example.neo4j.model.Sensor;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.util.Map;

public class SensorNeo4jDAO {

    private final Driver driver;

    public SensorNeo4jDAO() {
        this.driver = Neo4jSingleton.getInstance();
    }

    public void crear(Sensor sensor) {

        String cypher = """
                MERGE (s:Sensor {id: $id})
                SET s.tipo = $tipo
                """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "id", sensor.getId(),
                            "tipo", sensor.getTipo()
                    )
            );
        }
    }
}