package org.example.neo4j.dao;

import org.example.conecciones.Neo4jSingleton;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.util.Map;

/** Relaciones directas del grafo logístico. No utiliza nodos Tramo. */
public class GrafoNeo4jDAO {
    private final Driver driver = Neo4jSingleton.getInstance();


    public void envioUsaVehiculo(String envioId, String vehiculoId) {
        ejecutar("MATCH (e:Envio {id:$envioId}) MERGE (v:Vehiculo {id:$vehiculoId}) MERGE (e)-[:UTILIZA]->(v)", Map.of("envioId", envioId, "vehiculoId", vehiculoId));
    }


    private void ejecutar(String cypher, Map<String,Object> params) {
        try (Session s = driver.session()) { s.run(cypher, params); }
    }
}
