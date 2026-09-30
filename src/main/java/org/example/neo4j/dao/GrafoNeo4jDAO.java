package org.example.neo4j.dao;

import org.example.conecciones.Neo4jSingleton;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.util.List;
import java.util.Map;

public class GrafoNeo4jDAO {

    private final Driver driver;

    public GrafoNeo4jDAO() {
        this.driver = Neo4jSingleton.getInstance();
    }

    // =====================================================
    // RELACIONES
    // =====================================================

    // Relaciona un cliente con un envío.
    public void clienteRealizaEnvio(
            String clienteId,
            String envioId
    ) {

        String cypher = """
                MATCH (c:Cliente {id: $clienteId})
                MATCH (e:Envio {id: $envioId})

                CREATE (c)-[:REALIZA]->(e)
                """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "clienteId", clienteId,
                            "envioId", envioId
                    )
            );
        }
    }

    // Relaciona un envío con uno de sus tramos.
    public void envioTieneTramo(
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

    // Relaciona un tramo con el vehículo utilizado.
    public void tramoUsaVehiculo(
            String tramoId,
            String vehiculoId
    ) {

        String cypher = """
                MATCH (t:Tramo {id: $tramoId})
                MATCH (v:Vehiculo {id: $vehiculoId})

                CREATE (t)-[:USA]->(v)
                """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "tramoId", tramoId,
                            "vehiculoId", vehiculoId
                    )
            );
        }
    }

    // Relaciona un tramo con su ubicación de salida.
    public void tramoSaleDe(
            String tramoId,
            String ubicacionId
    ) {

        String cypher = """
                MATCH (t:Tramo {id: $tramoId})
                MATCH (u:Ubicacion {id: $ubicacionId})

                CREATE (t)-[:SALE_DE]->(u)
                """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "tramoId", tramoId,
                            "ubicacionId", ubicacionId
                    )
            );
        }
    }

    // Relaciona un tramo con su ubicación de llegada.
    public void tramoLlegaA(
            String tramoId,
            String ubicacionId
    ) {

        String cypher = """
                MATCH (t:Tramo {id: $tramoId})
                MATCH (u:Ubicacion {id: $ubicacionId})

                CREATE (t)-[:LLEGA_A]->(u)
                """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "tramoId", tramoId,
                            "ubicacionId", ubicacionId
                    )
            );
        }
    }

    // Relaciona un operador con un vehículo que opera.
    public void operadorOperaVehiculo(
            String operadorId,
            String vehiculoId
    ) {

        String cypher = """
                MATCH (o:Operador {id: $operadorId})
                MATCH (v:Vehiculo {id: $vehiculoId})

                CREATE (o)-[:OPERA]->(v)
                """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "operadorId", operadorId,
                            "vehiculoId", vehiculoId
                    )
            );
        }
    }

    // Relaciona un proveedor con un vehículo que provee.
    public void proveedorProveeVehiculo(
            String proveedorId,
            String vehiculoId
    ) {

        String cypher = """
                MATCH (p:Proveedor {id: $proveedorId})
                MATCH (v:Vehiculo {id: $vehiculoId})

                CREATE (p)-[:PROVEE]->(v)
                """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "proveedorId", proveedorId,
                            "vehiculoId", vehiculoId
                    )
            );
        }
    }

    // Relaciona un envío con un contenedor que transporta.
    public void envioTransportaContenedor(
            String envioId,
            String contenedorId
    ) {

        String cypher = """
                MATCH (e:Envio {id: $envioId})
                MATCH (c:Contenedor {id: $contenedorId})

                CREATE (e)-[:TRANSPORTA]->(c)
                """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "envioId", envioId,
                            "contenedorId", contenedorId
                    )
            );
        }
    }

    // Relaciona un contenedor con un sensor.
    public void contenedorTieneSensor(
            String contenedorId,
            String sensorId
    ) {

        String cypher = """
                MATCH (c:Contenedor {id: $contenedorId})
                MATCH (s:Sensor {id: $sensorId})

                CREATE (c)-[:TIENE_SENSOR]->(s)
                """;

        try (Session session = driver.session()) {

            session.run(
                    cypher,
                    Map.of(
                            "contenedorId", contenedorId,
                            "sensorId", sensorId
                    )
            );
        }
    }


    // =====================================================
    // CONSULTAS
    // =====================================================

    /*
     * Obtiene la ruta de un envío.
     *
     * Busca el envío, recorre sus tramos y obtiene las
     * ubicaciones por las que pasa.
     */
    public List<String> obtenerRutaEnvio(
            String envioId
    ) {

        try (Session session = driver.session()) {

            return session.run("""
                    MATCH (e:Envio {id: $envioId})
                          -[:TIENE]->(t:Tramo)
                          -[:SALE_DE|LLEGA_A]->(u:Ubicacion)

                    RETURN DISTINCT u.ciudad AS ciudad
                    ORDER BY t.fechaSalida
                    """,
                    Map.of("envioId", envioId)
            ).list(record ->
                    record.get("ciudad").asString()
            );
        }
    }


    /*
     * Obtiene todos los envíos realizados por un cliente.
     */
    public List<String> obtenerEnviosDeCliente(
            String clienteId
    ) {

        try (Session session = driver.session()) {

            return session.run("""
                    MATCH (c:Cliente {id: $clienteId})
                          -[:REALIZA]->(e:Envio)

                    RETURN e.id AS envio
                    """,
                    Map.of("clienteId", clienteId)
            ).list(record ->
                    record.get("envio").asString()
            );
        }
    }


    /*
     * Obtiene todos los vehículos operados por un operador.
     */
    public List<String> obtenerVehiculosDeOperador(
            String operadorId
    ) {

        try (Session session = driver.session()) {

            return session.run("""
                    MATCH (o:Operador {id: $operadorId})
                          -[:OPERA]->(v:Vehiculo)

                    RETURN v.id AS vehiculo
                    """,
                    Map.of("operadorId", operadorId)
            ).list(record ->
                    record.get("vehiculo").asString()
            );
        }
    }


    /*
     * Obtiene todos los contenedores transportados por un envío.
     */
    public List<String> obtenerContenedoresDeEnvio(
            String envioId
    ) {

        try (Session session = driver.session()) {

            return session.run("""
                    MATCH (e:Envio {id: $envioId})
                          -[:TRANSPORTA]->(c:Contenedor)

                    RETURN c.id AS contenedor
                    """,
                    Map.of("envioId", envioId)
            ).list(record ->
                    record.get("contenedor").asString()
            );
        }
    }


    /*
     * Obtiene todos los envíos que pasan por una ubicación.
     */
    public List<String> obtenerEnviosPorUbicacion(
            String ubicacionId
    ) {

        try (Session session = driver.session()) {

            return session.run("""
                    MATCH (e:Envio)
                          -[:TIENE]->(t:Tramo)
                          -[:SALE_DE|LLEGA_A]->
                          (u:Ubicacion {id: $ubicacionId})

                    RETURN DISTINCT e.id AS envio
                    """,
                    Map.of("ubicacionId", ubicacionId)
            ).list(record ->
                    record.get("envio").asString()
            );
        }
    }


    // =====================================================
    // CONSULTAS AVANZADAS
    // =====================================================

    /*
     * Obtiene los operadores relacionados con un envío.
     *
     * Envío -> Tramo -> Vehículo -> Operador
     */
    public List<String> obtenerOperadorDeEnvio(
            String envioId
    ) {

        try (Session session = driver.session()) {

            return session.run("""
                    MATCH (e:Envio {id: $envioId})
                          -[:TIENE]->(t:Tramo)
                          -[:USA]->(v:Vehiculo)

                    MATCH (o:Operador)-[:OPERA]->(v)

                    RETURN DISTINCT o.id AS operador
                    """,
                    Map.of("envioId", envioId)
            ).list(record ->
                    record.get("operador").asString()
            );
        }
    }


    /*
     * Obtiene los proveedores relacionados con un envío.
     *
     * Envío -> Tramo -> Vehículo -> Proveedor
     */
    public List<String> obtenerProveedorDeEnvio(
            String envioId
    ) {

        try (Session session = driver.session()) {

            return session.run("""
                    MATCH (e:Envio {id: $envioId})
                          -[:TIENE]->(t:Tramo)
                          -[:USA]->(v:Vehiculo)

                    MATCH (p:Proveedor)-[:PROVEE]->(v)

                    RETURN DISTINCT p.id AS proveedor
                    """,
                    Map.of("envioId", envioId)
            ).list(record ->
                    record.get("proveedor").asString()
            );
        }
    }


    /*
     * Obtiene el recorrido completo de un contenedor.
     */
    public List<String> obtenerRecorridoContenedor(
            String contenedorId
    ) {

        try (Session session = driver.session()) {

            return session.run("""
                    MATCH (e:Envio)
                          -[:TRANSPORTA]->
                          (c:Contenedor {id: $contenedorId})

                    MATCH (e)-[:TIENE]->(t:Tramo)

                    MATCH (t)-[:SALE_DE]->(origen:Ubicacion)
                    MATCH (t)-[:LLEGA_A]->(destino:Ubicacion)

                    RETURN
                        t.id AS tramo,
                        origen.ciudad AS origen,
                        destino.ciudad AS destino,
                        t.medioTransporte AS transporte

                    ORDER BY t.fechaSalida
                    """,
                    Map.of("contenedorId", contenedorId)
            ).list(record -> {

                String tramo =
                        record.get("tramo").asString();

                String origen =
                        record.get("origen").asString();

                String destino =
                        record.get("destino").asString();

                String transporte =
                        record.get("transporte").asString();

                return tramo
                        + " | "
                        + origen
                        + " -> "
                        + destino
                        + " | "
                        + transporte;
            });
        }
    }


    /*
     * Obtiene la red completa relacionada con un envío.
     *
     * Incluye:
     *
     * Cliente
     * Envío
     * Tramos
     * Vehículos
     * Operadores
     * Proveedores
     * Contenedores
     * Sensores
     */
    public List<String> obtenerRedDeEnvio(
            String envioId
    ) {

        try (Session session = driver.session()) {

            return session.run("""
                    MATCH (c:Cliente)-[:REALIZA]->
                          (e:Envio {id: $envioId})

                    OPTIONAL MATCH
                        (e)-[:TIENE]->(t:Tramo)

                    OPTIONAL MATCH
                        (t)-[:USA]->(v:Vehiculo)

                    OPTIONAL MATCH
                        (o:Operador)-[:OPERA]->(v)

                    OPTIONAL MATCH
                        (p:Proveedor)-[:PROVEE]->(v)

                    OPTIONAL MATCH
                        (e)-[:TRANSPORTA]->(cont:Contenedor)

                    OPTIONAL MATCH
                        (cont)-[:TIENE_SENSOR]->(s:Sensor)

                    RETURN
                        c.id AS cliente,
                        e.id AS envio,
                        t.id AS tramo,
                        v.id AS vehiculo,
                        o.id AS operador,
                        p.id AS proveedor,
                        cont.id AS contenedor,
                        s.id AS sensor
                    """,
                    Map.of("envioId", envioId)
            ).list(record -> {

                String cliente =
                        record.get("cliente").isNull()
                                ? "-"
                                : record.get("cliente").asString();

                String envio =
                        record.get("envio").isNull()
                                ? "-"
                                : record.get("envio").asString();

                String tramo =
                        record.get("tramo").isNull()
                                ? "-"
                                : record.get("tramo").asString();

                String vehiculo =
                        record.get("vehiculo").isNull()
                                ? "-"
                                : record.get("vehiculo").asString();

                String operador =
                        record.get("operador").isNull()
                                ? "-"
                                : record.get("operador").asString();

                String proveedor =
                        record.get("proveedor").isNull()
                                ? "-"
                                : record.get("proveedor").asString();

                String contenedor =
                        record.get("contenedor").isNull()
                                ? "-"
                                : record.get("contenedor").asString();

                String sensor =
                        record.get("sensor").isNull()
                                ? "-"
                                : record.get("sensor").asString();

                return "Cliente=" + cliente
                        + " | Envio=" + envio
                        + " | Tramo=" + tramo
                        + " | Vehiculo=" + vehiculo
                        + " | Operador=" + operador
                        + " | Proveedor=" + proveedor
                        + " | Contenedor=" + contenedor
                        + " | Sensor=" + sensor;
            });
        }
    }
}