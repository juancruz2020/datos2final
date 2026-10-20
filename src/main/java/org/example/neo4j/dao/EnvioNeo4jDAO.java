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

    /** Guarda el envío y sus relaciones en una única transacción Neo4j. */
    public void guardarCompleto(org.example.neo4j.model.Envio envio) {
        Map<String, Object> parametros = new LinkedHashMap<>();
        parametros.put("id", envio.getId());
        parametros.put("cliente", envio.getClienteId());
        parametros.put("fecha", envio.getFechaCreacion());
        parametros.put("fechaSalida", envio.getFechaSalida());
        parametros.put("fechaEntrega", envio.getFechaEntrega());
        parametros.put("estado", envio.getEstado());
        parametros.put("prioridad", envio.getPrioridad());
        parametros.put("costoEnvio", envio.getCostoEnvio());
        parametros.put("contenedores", envio.getContenedoresIds());
        parametros.put("ciudadOrigen", envio.getOrigen().getCiudad());
        parametros.put("paisOrigen", envio.getOrigen().getPais());
        parametros.put("ciudadDestino", envio.getDestino().getCiudad());
        parametros.put("paisDestino", envio.getDestino().getPais());
        parametros.put("claveOrigen", envio.getOrigen().getCiudad().trim().toLowerCase()
                + "|" + envio.getOrigen().getPais().trim().toLowerCase());
        parametros.put("claveDestino", envio.getDestino().getCiudad().trim().toLowerCase()
                + "|" + envio.getDestino().getPais().trim().toLowerCase());
        try (Session session = driver.session()) {
            session.executeWrite(tx -> {
                tx.run("""
                        MERGE (e:Envio {id: $id})
                        SET e.fechaCreacion = $fecha,
                            e.fechaSalida = $fechaSalida,
                            e.fechaEntrega = $fechaEntrega,
                            e.estado = $estado,
                            e.prioridad = $prioridad,
                            e.costoEnvio = $costoEnvio
                        REMOVE e.clienteId, e.ciudadOrigen, e.paisOrigen,
                               e.ciudadDestino, e.paisDestino
                        WITH e
                        OPTIONAL MATCH (e)-[r:TRANSPORTA|SALE_DE|LLEGA_A|UTILIZA]-()
                        DELETE r
                        """, parametros).consume();
                tx.run("MATCH (:Cliente)-[r:REALIZA]->(:Envio {id: $id}) DELETE r", parametros).consume();
                tx.run("""
                        MATCH (e:Envio {id: $id})
                        MERGE (c:Cliente {id: $cliente}) MERGE (c)-[:REALIZA]->(e)
                        MERGE (o:Ubicacion {clave: $claveOrigen})
                        SET o.ciudad = $ciudadOrigen, o.pais = $paisOrigen
                        MERGE (e)-[:SALE_DE]->(o)
                        MERGE (d:Ubicacion {clave: $claveDestino})
                        SET d.ciudad = $ciudadDestino, d.pais = $paisDestino
                        MERGE (e)-[:LLEGA_A]->(d)
                        WITH e UNWIND $contenedores AS idContenedor
                        MERGE (co:Contenedor {id: idContenedor})
                        MERGE (e)-[:TRANSPORTA]->(co)
                        """, parametros).consume();
                return null;
            });
        }
    }

    public boolean contenedorOcupadoPorOtro(String contenedorId, String envioId) {
        try (Session session = driver.session()) {
            return session.run("""
                    MATCH (e:Envio)-[:TRANSPORTA]->(:Contenedor {id: $contenedor})
                    WHERE e.id <> $envio AND e.estado IN ['EN_TRANSITO', 'DEMORADO']
                    RETURN e.id LIMIT 1
                    """, Map.of("contenedor", contenedorId, "envio", envioId)).hasNext();
        }
    }

    /** Migra los atributos antiguos antes de eliminarlos, conservando sus relaciones. */
    public void normalizarEnviosExistentes() {
        try (Session session = driver.session()) {
            session.executeWrite(tx -> {
                tx.run("""
                        MATCH (e:Envio)
                        WHERE e.ciudadOrigen IS NOT NULL AND e.paisOrigen IS NOT NULL
                          AND NOT EXISTS { MATCH (e)-[:SALE_DE]->(:Ubicacion) }
                        MERGE (o:Ubicacion {clave: toLower(trim(e.ciudadOrigen)) + '|' + toLower(trim(e.paisOrigen))})
                        SET o.ciudad = e.ciudadOrigen, o.pais = e.paisOrigen
                        MERGE (e)-[:SALE_DE]->(o)
                        """).consume();
                tx.run("""
                        MATCH (e:Envio)
                        WHERE e.ciudadDestino IS NOT NULL AND e.paisDestino IS NOT NULL
                          AND NOT EXISTS { MATCH (e)-[:LLEGA_A]->(:Ubicacion) }
                        MERGE (d:Ubicacion {clave: toLower(trim(e.ciudadDestino)) + '|' + toLower(trim(e.paisDestino))})
                        SET d.ciudad = e.ciudadDestino, d.pais = e.paisDestino
                        MERGE (e)-[:LLEGA_A]->(d)
                        """).consume();
                tx.run("""
                        MATCH (e:Envio)
                        WHERE e.clienteId IS NOT NULL
                          AND NOT EXISTS { MATCH (:Cliente)-[:REALIZA]->(e) }
                        MERGE (c:Cliente {id: e.clienteId})
                        MERGE (c)-[:REALIZA]->(e)
                        """).consume();
                tx.run("""
                        MATCH (e:Envio)-[:SALE_DE]->(:Ubicacion)
                        REMOVE e.ciudadOrigen, e.paisOrigen
                        """).consume();
                tx.run("""
                        MATCH (e:Envio)-[:LLEGA_A]->(:Ubicacion)
                        REMOVE e.ciudadDestino, e.paisDestino
                        """).consume();
                tx.run("""
                        MATCH (:Cliente)-[:REALIZA]->(e:Envio)
                        REMOVE e.clienteId
                        """).consume();
                return null;
            });
        }
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
                OPTIONAL MATCH (e)-[:SALE_DE]->(origen:Ubicacion)
                OPTIONAL MATCH (e)-[:LLEGA_A]->(destino:Ubicacion)
                RETURN
                    e.id AS id,
                    cliente.id AS clienteId,
                    cliente.razonSocial AS cliente,
                    e.fechaCreacion AS fechaCreacion,
                    e.fechaSalida AS fechaSalida,
                    e.fechaEntrega AS fechaEntrega,
                    origen.ciudad AS ciudadOrigen,
                    origen.pais AS paisOrigen,
                    destino.ciudad AS ciudadDestino,
                    destino.pais AS paisDestino,
                    e.estado AS estado,
                    e.prioridad AS prioridad,
                    e.costoEnvio AS costoEnvio,
                    v.id AS vehiculoId,
                    coalesce(v.identificacion, v.patente) AS vehiculo,
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
                OPTIONAL MATCH (e)-[:SALE_DE]->(origen:Ubicacion)
                OPTIONAL MATCH (e)-[:LLEGA_A]->(destino:Ubicacion)
                RETURN
                    e.id AS id,
                    cliente.id AS clienteId,
                    cliente.razonSocial AS cliente,
                    e.fechaCreacion AS fechaCreacion,
                    e.fechaSalida AS fechaSalida,
                    e.fechaEntrega AS fechaEntrega,
                    origen.ciudad AS ciudadOrigen,
                    origen.pais AS paisOrigen,
                    destino.ciudad AS ciudadDestino,
                    destino.pais AS paisDestino,
                    e.estado AS estado,
                    e.prioridad AS prioridad,
                    e.costoEnvio AS costoEnvio,
                    v.id AS vehiculoId,
                    coalesce(v.identificacion, v.patente) AS vehiculo,
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
            String fechaSalida,
            String fechaEntrega,
            String ciudadOrigen,
            String paisOrigen,
            String ciudadDestino,
            String paisDestino,
            String estado,
            String prioridad
    ) {

        guardarCompleto(new org.example.neo4j.model.Envio(
                id, clienteId, List.of(), fechaCreacion, fechaSalida, fechaEntrega,
                new org.example.neo4j.model.Ubicacion(null, ciudadOrigen, paisOrigen),
                new org.example.neo4j.model.Ubicacion(null, ciudadDestino, paisDestino),
                estado, prioridad));
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
        envio.put("fechaSalida", texto(record.get("fechaSalida")));
        envio.put("fechaEntrega", texto(record.get("fechaEntrega")));

        envio.put("ciudadOrigen", texto(record.get("ciudadOrigen")));
        envio.put("paisOrigen", texto(record.get("paisOrigen")));
        envio.put("ciudadDestino", texto(record.get("ciudadDestino")));
        envio.put("paisDestino", texto(record.get("paisDestino")));

        envio.put("estado", texto(record.get("estado")));
        envio.put("prioridad", texto(record.get("prioridad")));
        envio.put("costoEnvio", record.get("costoEnvio").isNull()
                ? null : record.get("costoEnvio").asDouble());

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
