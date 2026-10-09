package org.example.neo4j.dao;

import org.example.conecciones.Neo4jSingleton;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
                MERGE (c:Cliente {id: $id})
                SET c.razonSocial = $razonSocial,
                    c.cuit = $cuit
                """;

        try (Session session = driver.session()) {
            session.run(
                    cypher,
                    Map.of(
                            "id", id,
                            "razonSocial", razonSocial,
                            "cuit", cuit
                    )
            );
        }
    }

    public List<Map<String, Object>> listarTodos() {
        String cypher = """
                MATCH (c:Cliente)
                RETURN c.id AS id,
                       c.razonSocial AS razonSocial,
                       c.cuit AS cuit
                ORDER BY c.razonSocial
                """;

        try (Session session = driver.session()) {
            var resultado = session.run(cypher);

            List<Map<String, Object>> clientes = new ArrayList<>();

            while (resultado.hasNext()) {
                var record = resultado.next();

                Map<String, Object> cliente = new LinkedHashMap<>();

                cliente.put("id", record.get("id").asString(""));
                cliente.put(
                        "razonSocial",
                        record.get("razonSocial").asString("")
                );
                cliente.put("cuit", record.get("cuit").asString(""));

                clientes.add(cliente);
            }

            return clientes;
        }
    }

    public Map<String, Object> buscarPorId(String id) {
        String cypher = """
                MATCH (c:Cliente {id: $id})
                RETURN c.id AS id,
                       c.razonSocial AS razonSocial,
                       c.cuit AS cuit
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

            Map<String, Object> cliente = new LinkedHashMap<>();

            cliente.put("id", record.get("id").asString(""));
            cliente.put(
                    "razonSocial",
                    record.get("razonSocial").asString("")
            );
            cliente.put("cuit", record.get("cuit").asString(""));

            return cliente;
        }
    }
}
