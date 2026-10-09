package org.example.neo4j.dao;

import org.example.conecciones.Neo4jSingleton;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EnvioNeo4jDAO {

    private final Driver driver;

    public EnvioNeo4jDAO() {
        this.driver = Neo4jSingleton.getInstance();
    }

    // =====================================================
    // LISTAR TODOS LOS ENVÍOS
    // =====================================================

    public List<Map<String, Object>> listarTodos() {

        String cypher = """
                MATCH (e:Envio)
                OPTIONAL MATCH (cliente:Cliente)-[:REALIZA]->(e)
                OPTIONAL MATCH (e)-[:TRANSPORTA]->(co:Contenedor)
                OPTIONAL MATCH (e)-[:UTILIZA]->(v:Vehiculo)
                RETURN
                    e.id AS id,
                    e.clienteId AS clienteId,
                    cliente.razonSocial AS cliente,
                    e.fechaCreacion AS fechaCreacion,
                    e.ciudadOrigen AS ciudadOrigen,
                    e.paisOrigen AS paisOrigen,
                    e.ciudadDestino AS ciudadDestino,
                    e.paisDestino AS paisDestino,
                    e.estado AS estado,
                    e.prioridad AS prioridad,
                    v.id AS vehiculoId,
                    v.identificacion AS vehiculo,
                    v.tipo AS vehiculoTipo,
                    collect(DISTINCT co.id) AS contenedoresIds,
                    collect(DISTINCT co.codigo) AS contenedores
                ORDER BY e.fechaCreacion DESC
                """;

        List<Map<String, Object>> envios = new ArrayList<>();

        try (Session session = driver.session()) {
            var resultado = session.run(cypher);

            while (resultado.hasNext()) {
                envios.add(convertirRegistro(resultado.next()));
            }
        }

        return envios;
    }

    // =====================================================
    // BUSCAR ENVÍO POR ID
    // =====================================================

    public Map<String, Object> buscarPorId(String id) {

        String cypher = """
                MATCH (e:Envio {id: $id})
                OPTIONAL MATCH (cliente:Cliente)-[:REALIZA]->(e)
                OPTIONAL MATCH (e)-[:TRANSPORTA]->(co:Contenedor)
                OPTIONAL MATCH (e)-[:UTILIZA]->(v:Vehiculo)
                RETURN
                    e.id AS id,
                    e.clienteId AS clienteId,
                    cliente.razonSocial AS cliente,
                    e.fechaCreacion AS fechaCreacion,
                    e.ciudadOrigen AS ciudadOrigen,
                    e.paisOrigen AS paisOrigen,
                    e.ciudadDestino AS ciudadDestino,
                    e.paisDestino AS paisDestino,
                    e.estado AS estado,
                    e.prioridad AS prioridad,
                    v.id AS vehiculoId,
                    v.identificacion AS vehiculo,
                    v.tipo AS vehiculoTipo,
                    collect(DISTINCT co.id) AS contenedoresIds,
                    collect(DISTINCT co.codigo) AS contenedores
                """;

        try (Session session = driver.session()) {
            var resultado = session.run(cypher, Map.of("id", id));

            if (resultado.hasNext()) {
                return convertirRegistro(resultado.next());
            }
        }

        return null;
    }

    // =====================================================
    // CREAR O ACTUALIZAR ENVÍO
    // =====================================================

    public void crearEnvio(
            String id,
            String clienteId,
            String fechaCreacion,
            String ciudadOrigen,
            String paisOrigen,
            String ciudadDestino,
            String paisDestino,
            String estado,
            String prioridad
    ) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "El ID del envío es obligatorio."
            );
        }

        if (clienteId == null || clienteId.isBlank()) {
            throw new IllegalArgumentException(
                    "El ID del cliente es obligatorio."
            );
        }

        String cypher = """
                MERGE (e:Envio {id: $id})
                SET e.clienteId = $clienteId,
                    e.fechaCreacion = $fechaCreacion,
                    e.ciudadOrigen = $ciudadOrigen,
                    e.paisOrigen = $paisOrigen,
                    e.ciudadDestino = $ciudadDestino,
                    e.paisDestino = $paisDestino,
                    e.estado = $estado,
                    e.prioridad = $prioridad
                """;

        Map<String, Object> parametros = new LinkedHashMap<>();

        parametros.put("id", id);
        parametros.put("clienteId", clienteId);
        parametros.put("fechaCreacion", fechaCreacion);
        parametros.put("ciudadOrigen", ciudadOrigen);
        parametros.put("paisOrigen", paisOrigen);
        parametros.put("ciudadDestino", ciudadDestino);
        parametros.put("paisDestino", paisDestino);
        parametros.put("estado", estado);
        parametros.put("prioridad", prioridad);

        try (Session session = driver.session()) {
            session.run(cypher, parametros);
        }
    }

    // =====================================================
    // RELACIONAR CLIENTE CON ENVÍO
    // =====================================================

    public void relacionarClienteConEnvio(
            String clienteId,
            String envioId
    ) {

        String cypher = """
                MATCH (cliente:Cliente {id: $clienteId})
                MATCH (e:Envio {id: $envioId})
                MERGE (cliente)-[:REALIZA]->(e)
                """;

        try (Session session = driver.session()) {
            session.run(cypher, Map.of(
                    "clienteId", clienteId,
                    "envioId", envioId
            ));
        }
    }

    // =====================================================
    // RELACIONAR ENVÍO CON CONTENEDOR
    // =====================================================

    public void relacionarContenedor(
            String envioId,
            String contenedorId
    ) {

        String cypher = """
                MATCH (e:Envio {id: $envioId})
                MATCH (co:Contenedor {id: $contenedorId})
                MERGE (e)-[:TRANSPORTA]->(co)
                """;

        try (Session session = driver.session()) {
            session.run(cypher, Map.of(
                    "envioId", envioId,
                    "contenedorId", contenedorId
            ));
        }
    }

    // =====================================================
    // ELIMINAR RELACIONES CON CONTENEDORES
    // =====================================================

    public void eliminarRelacionesContenedores(String envioId) {

        String cypher = """
                MATCH (e:Envio {id: $id})-[r:TRANSPORTA]->(:Contenedor)
                DELETE r
                """;

        try (Session session = driver.session()) {
            session.run(cypher, Map.of("id", envioId));
        }
    }

    // =====================================================
    // ELIMINAR RELACIÓN CON CLIENTE
    // =====================================================

    public void eliminarRelacionCliente(String envioId) {

        String cypher = """
                MATCH (:Cliente)-[r:REALIZA]->(e:Envio {id: $id})
                DELETE r
                """;

        try (Session session = driver.session()) {
            session.run(cypher, Map.of("id", envioId));
        }
    }

    /** Elimina la relación del envío con cualquier vehículo asignado. */
    public void eliminarRelacionesVehiculo(String envioId) {
        String cypher = """
                MATCH (e:Envio {id: $id})-[r:UTILIZA]->(:Vehiculo)
                DELETE r
                """;

        try (Session session = driver.session()) {
            session.run(cypher, Map.of("id", envioId));
        }
    }

    /** Elimina las relaciones de origen y destino para evitar ubicaciones obsoletas. */
    public void eliminarRelacionesUbicacion(String envioId) {
        String cypher = """
                MATCH (e:Envio {id: $id})-[r:SALE_DE|LLEGA_A]->(:Ubicacion)
                DELETE r
                """;

        try (Session session = driver.session()) {
            session.run(cypher, Map.of("id", envioId));
        }
    }

    // =====================================================
    // ELIMINAR ENVÍO
    // =====================================================

    public void eliminarEnvio(String id) {

        String cypher = """
                MATCH (e:Envio {id: $id})
                DETACH DELETE e
                """;

        try (Session session = driver.session()) {
            session.run(cypher, Map.of("id", id));
        }

        eliminarNodosHuerfanos();
    }

    /**
     * Elimina únicamente nodos logísticos aislados. Los nodos que todavía
     * tengan cualquier relación con el grafo se conservan.
     */
    public void eliminarNodosHuerfanos() {
        String cypher = """
                MATCH (n)
                WHERE (n:Vehiculo OR n:Contenedor OR n:Ubicacion)
                  AND NOT (n)--()
                DELETE n
                """;

        try (Session session = driver.session()) {
            session.run(cypher);
        }
    }

    // =====================================================
    // CONVERTIR REGISTRO A MAP
    // =====================================================

    private Map<String, Object> convertirRegistro(Record record) {

        Map<String, Object> envio = new LinkedHashMap<>();

        envio.put("id", texto(record.get("id")));
        envio.put("clienteId", texto(record.get("clienteId")));
        envio.put("cliente", texto(record.get("cliente")));
        envio.put("fechaCreacion", texto(record.get("fechaCreacion")));

        envio.put("ciudadOrigen", texto(record.get("ciudadOrigen")));
        envio.put("paisOrigen", texto(record.get("paisOrigen")));
        envio.put("ciudadDestino", texto(record.get("ciudadDestino")));
        envio.put("paisDestino", texto(record.get("paisDestino")));

        envio.put("estado", texto(record.get("estado")));
        envio.put("prioridad", texto(record.get("prioridad")));

        // Vehículo asignado al envío.
        envio.put("vehiculoId", texto(record.get("vehiculoId")));
        envio.put("vehiculo", texto(record.get("vehiculo")));
        envio.put("vehiculoTipo", texto(record.get("vehiculoTipo")));

        // Contenedores vinculados al envío.
        envio.put(
                "contenedoresIds",
                listaTextos(record.get("contenedoresIds"))
        );

        envio.put(
                "contenedores",
                listaTextos(record.get("contenedores"))
        );

        return envio;
    }

    private String texto(Value value) {
        return value == null || value.isNull()
                ? null
                : value.asString();
    }

    private List<String> listaTextos(Value value) {

        List<String> lista = new ArrayList<>();

        if (value == null || value.isNull()) {
            return lista;
        }

        for (Value elemento : value.values()) {
            if (!elemento.isNull()) {
                lista.add(elemento.asString());
            }
        }

        return lista;
    }
}
