package org.example.neo4j.dao;

import org.example.conecciones.Neo4jSingleton;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.util.List;
import java.util.Map;

/** Relaciones directas del grafo logístico. No utiliza nodos Tramo. */
public class GrafoNeo4jDAO {
    private final Driver driver = Neo4jSingleton.getInstance();

    public void clienteRealizaEnvio(String clienteId, String envioId) {
        ejecutar("MERGE (c:Cliente {id:$clienteId}) WITH c MATCH (e:Envio {id:$envioId}) MERGE (c)-[:REALIZA]->(e)", Map.of("clienteId", clienteId, "envioId", envioId));
    }

    public void envioUsaVehiculo(String envioId, String vehiculoId) {
        ejecutar("MATCH (e:Envio {id:$envioId}) MERGE (v:Vehiculo {id:$vehiculoId}) MERGE (e)-[:UTILIZA]->(v)", Map.of("envioId", envioId, "vehiculoId", vehiculoId));
    }

    public void envioSaleDe(String envioId, String ciudad, String pais) {
        String clave = claveUbicacion(ciudad, pais);
        ejecutar("MATCH (e:Envio {id:$envioId}) MERGE (u:Ubicacion {clave:$clave}) SET u.ciudad=$ciudad, u.pais=$pais MERGE (e)-[:SALE_DE]->(u)", Map.of("envioId", envioId, "clave", clave, "ciudad", ciudad, "pais", pais));
    }

    public void envioLlegaA(String envioId, String ciudad, String pais) {
        String clave = claveUbicacion(ciudad, pais);
        ejecutar("MATCH (e:Envio {id:$envioId}) MERGE (u:Ubicacion {clave:$clave}) SET u.ciudad=$ciudad, u.pais=$pais MERGE (e)-[:LLEGA_A]->(u)", Map.of("envioId", envioId, "clave", clave, "ciudad", ciudad, "pais", pais));
    }

    public void operadorOperaVehiculo(String operadorId, String vehiculoId) {
        ejecutar("MERGE (o:Operador {id:$operadorId}) MERGE (v:Vehiculo {id:$vehiculoId}) MERGE (o)-[:OPERA]->(v)", Map.of("operadorId", operadorId, "vehiculoId", vehiculoId));
    }

    public void proveedorProveeVehiculo(String proveedorId, String vehiculoId) {
        ejecutar("MERGE (p:Proveedor {id:$proveedorId}) MERGE (v:Vehiculo {id:$vehiculoId}) MERGE (p)-[:PROVEE]->(v)", Map.of("proveedorId", proveedorId, "vehiculoId", vehiculoId));
    }

    public void envioTransportaContenedor(String envioId, String contenedorId) {
        ejecutar("MATCH (e:Envio {id:$envioId}) MERGE (c:Contenedor {id:$contenedorId}) MERGE (e)-[:TRANSPORTA]->(c)", Map.of("envioId", envioId, "contenedorId", contenedorId));
    }

    public void contenedorTieneSensor(String contenedorId, String sensorId) {
        ejecutar("MERGE (c:Contenedor {id:$contenedorId}) MERGE (s:Sensor {id:$sensorId}) MERGE (c)-[:TIENE_SENSOR]->(s)", Map.of("contenedorId", contenedorId, "sensorId", sensorId));
    }

    public List<String> obtenerRutaEnvio(String envioId) {
        try (Session s = driver.session()) {
            return s.run("MATCH (e:Envio {id:$id})-[:SALE_DE|LLEGA_A]->(u:Ubicacion) RETURN DISTINCT u.ciudad AS ciudad ORDER BY ciudad", Map.of("id", envioId)).list(r -> r.get("ciudad").asString());
        }
    }
    public List<String> obtenerEnviosDeCliente(String id) {
        return consulta("MATCH (:Cliente {id:$id})-[:REALIZA]->(e:Envio) RETURN e.id AS valor", id);
    }
    public List<String> obtenerVehiculosDeOperador(String id) {
        return consulta("MATCH (:Operador {id:$id})-[:OPERA]->(v:Vehiculo) RETURN v.id AS valor", id);
    }
    public List<String> obtenerContenedoresDeEnvio(String id) {
        return consulta("MATCH (:Envio {id:$id})-[:TRANSPORTA]->(c:Contenedor) RETURN c.id AS valor", id);
    }
    public List<String> obtenerEnviosPorUbicacion(String id) {
        return consulta("MATCH (e:Envio)-[:SALE_DE|LLEGA_A]->(u:Ubicacion) WHERE u.id=$id OR u.clave=$id RETURN DISTINCT e.id AS valor", id);
    }
    public List<String> obtenerOperadorDeEnvio(String id) {
        return consulta("MATCH (:Envio {id:$id})-[:UTILIZA]->(v:Vehiculo)<-[:OPERA]-(o:Operador) RETURN DISTINCT o.id AS valor", id);
    }
    public List<String> obtenerProveedorDeEnvio(String id) {
        return consulta("MATCH (:Envio {id:$id})-[:UTILIZA]->(v:Vehiculo)<-[:PROVEE]-(p:Proveedor) RETURN DISTINCT p.id AS valor", id);
    }
    public List<String> obtenerRecorridoContenedor(String id) {
        try (Session s = driver.session()) {
            return s.run("MATCH (e:Envio)-[:TRANSPORTA]->(:Contenedor {id:$id}) OPTIONAL MATCH (e)-[:SALE_DE]->(o:Ubicacion) OPTIONAL MATCH (e)-[:LLEGA_A]->(d:Ubicacion) RETURN e.id + ' | ' + coalesce(o.ciudad,'?') + ' -> ' + coalesce(d.ciudad,'?') AS valor ORDER BY e.id", Map.of("id", id)).list(r -> r.get("valor").asString());
        }
    }
    public List<String> obtenerRedDeEnvio(String id) {
        try (Session s = driver.session()) {
            return s.run("MATCH (e:Envio {id:$id}) OPTIONAL MATCH (c:Cliente)-[:REALIZA]->(e) OPTIONAL MATCH (e)-[:UTILIZA]->(v:Vehiculo) OPTIONAL MATCH (e)-[:TRANSPORTA]->(co:Contenedor) OPTIONAL MATCH (co)-[:TIENE_SENSOR]->(se:Sensor) OPTIONAL MATCH (e)-[:SALE_DE]->(o:Ubicacion) OPTIONAL MATCH (e)-[:LLEGA_A]->(d:Ubicacion) RETURN 'Envio=' + e.id + ', Cliente=' + coalesce(c.id,'-') + ', Vehiculo=' + coalesce(v.id,'-') + ', Contenedor=' + coalesce(co.id,'-') + ', Sensor=' + coalesce(se.id,'-') + ', Origen=' + coalesce(o.ciudad,'-') + ', Destino=' + coalesce(d.ciudad,'-') AS valor", Map.of("id", id)).list(r -> r.get("valor").asString());
        }
    }

    private List<String> consulta(String cypher, String id) {
        try (Session s = driver.session()) { return s.run(cypher, Map.of("id", id)).list(r -> r.get("valor").asString()); }
    }
    private void ejecutar(String cypher, Map<String,Object> params) {
        try (Session s = driver.session()) { s.run(cypher, params); }
    }
    private String claveUbicacion(String ciudad, String pais) {
        return (ciudad == null ? "" : ciudad.trim().toLowerCase()) + "|" + (pais == null ? "" : pais.trim().toLowerCase());
    }
}
