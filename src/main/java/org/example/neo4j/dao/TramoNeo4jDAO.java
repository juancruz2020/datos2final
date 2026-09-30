package org.example.neo4j.dao;

import org.example.conecciones.Neo4jSingleton;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.time.LocalDateTime;
import java.util.Map;

public class TramoNeo4jDAO {

    private final Driver driver;

    public TramoNeo4jDAO() {
        this.driver = Neo4jSingleton.getInstance();
    }

    public void crearTramo(
            String id,
            String medioTransporte,
            LocalDateTime fechaSalida,
            LocalDateTime fechaLlegada
    ) {

        String cypher = """
                CREATE (t:Tramo {
                    id: $id,
                    medioTransporte: $medioTransporte,
                    fechaSalida: $fechaSalida,
                    fechaLlegada: $fechaLlegada
                })
                """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "id", id,
                            "medioTransporte", medioTransporte,
                            "fechaSalida", fechaSalida.toString(),
                            "fechaLlegada", fechaLlegada.toString()
                    )
            );
        }
    }

    public void relacionarEnvioConTramo(
            String envioId,
            String tramoId
    ) {

        String cypher = """
                MATCH (e:Envio {id: $envioId})
                MATCH (t:Tramo {id: $tramoId})

                CREATE (e)-[:TIENE]->(t)
                """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "envioId", envioId,
                            "tramoId", tramoId
                    )
            );
        }
    }
}