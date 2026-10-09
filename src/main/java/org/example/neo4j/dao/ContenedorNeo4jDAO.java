package org.example.neo4j.dao;

import org.example.conecciones.Neo4jSingleton;
import org.example.neo4j.model.Contenedor;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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

    public List<Map<String, Object>> listarTodos() {
        String cypher = """
                MATCH (c:Contenedor)
                RETURN c.id AS id,
                       c.codigo AS codigo,
                       c.tipo AS tipo
                ORDER BY c.codigo
                """;

        try (Session session = driver.session()) {
            var resultado = session.run(cypher);

            List<Map<String, Object>> contenedores = new ArrayList<>();

            while (resultado.hasNext()) {
                var record = resultado.next();

                Map<String, Object> contenedor = new LinkedHashMap<>();

                contenedor.put("id", record.get("id").asString(""));
                contenedor.put("codigo", record.get("codigo").asString(""));
                contenedor.put("tipo", record.get("tipo").asString(""));

                contenedores.add(contenedor);
            }

            return contenedores;
        }
    }

    public Map<String, Object> buscarPorId(String id) {
        String cypher = """
                MATCH (c:Contenedor {id: $id})
                RETURN c.id AS id,
                       c.codigo AS codigo,
                       c.tipo AS tipo
                """;

        try (Session session = driver.session()) {
            var resultado = session.run(
                    cypher,
                    Map.of("id", id)
            );

            if (!resultado.hasNext()) {
                return null;
            }

            var record = resultado.next();

            Map<String, Object> contenedor = new LinkedHashMap<>();

            contenedor.put("id", record.get("id").asString(""));
            contenedor.put("codigo", record.get("codigo").asString(""));
            contenedor.put("tipo", record.get("tipo").asString(""));

            return contenedor;
        }
    }
}
