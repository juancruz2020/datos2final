package org.example.DatosPorDefecto;

import org.example.mongoDB.controller.ControllerMongoDB;

import org.example.conecciones.Neo4jSingleton;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.util.Map;
import java.util.List;
import org.bson.Document;
import org.example.logging.AppLog;

public class GeneradorDatosPruebaNeo {

    private GeneradorDatosPruebaNeo() {
        // Clase utilitaria
    }

    public static void generar() {

        Driver driver = Neo4jSingleton.getInstance();

        try (Session session = driver.session()) {

            AppLog.info(GeneradorDatosPruebaNeo.class,
                    "Generando datos de prueba de Neo4j.");

            crearClientes(session);
            crearVehiculos(session);
            crearOperadores(session);
            crearProveedores(session);
            crearContenedores(session);
            crearUbicaciones(session);
            crearSensores(session);
            // Los envíos se crean después de sus nodos relacionados,
            // porque sus relaciones usan MATCH sobre esos IDs.
            crearEnvios(session);

            crearRelaciones(session);
            sincronizarReferenciasMongo();

            AppLog.info(GeneradorDatosPruebaNeo.class,
                    "Datos de prueba Neo4j generados correctamente.");
        }
    }

    /** Repara únicamente las referencias antiguas de los datos de prueba.
     * Conserva los nodos y sus relaciones, usando los IDs reales de MongoDB.
     */
    public static void sincronizarReferenciasMongo() {
        try (Session session = Neo4jSingleton.getInstance().session()) {
            // No consultar MongoDB si no quedan referencias de prueba antiguas.
            if (!session.run("""
                    MATCH (n)
                    WHERE (n:Cliente AND n.id IN ['CLI-001', 'CLI-002', 'CLI-003'])
                       OR (n:Contenedor AND n.id IN ['CONT-001', 'CONT-002', 'CONT-003', 'CONT-004', 'CONT-005', 'CONT-006', 'CONT-007', 'CONT-008'])
                       OR (n:Vehiculo AND n.id IN ['VEH-001', 'VEH-002', 'VEH-003'])
                    RETURN n LIMIT 1
                    """).hasNext()) {
                return;
            }
            sincronizarReferencias(session, "Cliente",
                    new ControllerMongoDB().listarClientes(), "cuit",
                    List.of("30-71111111-1", "30-72222222-2", "30-73333333-3"), "CLI-",
                    "SET n.razonSocial = $nombre, n.cuit = $clave", "razon_social");
            sincronizarReferencias(session, "Contenedor",
                    new ControllerMongoDB().listarContenedores(), "codigo_internacional",
                    List.of("CONT-001", "CONT-002", "CONT-003", "CONT-004",
                            "CONT-005", "CONT-006", "CONT-007", "CONT-008"), "CONT-",
                    "SET n.codigo = $clave", "codigo_internacional");
            sincronizarReferencias(session, "Vehiculo",
                    new ControllerMongoDB().listarVehiculos(), "identificacion",
                    List.of("AA123BB", "AB456CD", "AC789EF"), "VEH-",
                    "SET n.identificacion = $clave, n.patente = $clave, n.tipo = $nombre", "tipo");
        }
    }

    private static void sincronizarReferencias(
            Session session, String etiqueta, List<Document> documentos,
            String campoClave, List<String> claves, String prefijo,
            String actualizarPropiedades, String campoNombre) {
        for (int i = 0; i < claves.size(); i++) {
            String clave = claves.get(i);
            String idAnterior = prefijo + String.format("%03d", i + 1);
            if (!session.run("MATCH (n:" + etiqueta + " {id: $id}) RETURN n LIMIT 1",
                    Map.of("id", idAnterior)).hasNext()) {
                continue;
            }
            List<Document> coincidencias = documentos.stream()
                    .filter(d -> clave.equals(d.getString(campoClave)))
                    .toList();
            // Evitar asociar un envío a un registro ambiguo o inexistente.
            if (coincidencias.size() != 1) {
                throw new IllegalStateException("No se puede vincular el dato de prueba "
                        + prefijo + String.format("%03d", i + 1)
                        + ": se esperaba un único registro MongoDB para " + clave);
            }
            Document documento = coincidencias.get(0);
            String idMongo = documento.getObjectId("_id").toHexString();
            session.executeWrite(tx -> {
                tx.run("MATCH (n:" + etiqueta + " {id: $anterior}) "
                                + "SET n.id = $id " + actualizarPropiedades,
                        Map.of("anterior", idAnterior, "id", idMongo,
                                "clave", clave, "nombre", documento.getString(campoNombre))).consume();
                if ("Cliente".equals(etiqueta)) {
                    tx.run("""
                            MATCH (e:Envio {clienteId: $anterior})
                            MATCH (c:Cliente {id: $id})
                            MERGE (c)-[:REALIZA]->(e)
                            REMOVE e.clienteId
                            """, Map.of("anterior", idAnterior, "id", idMongo)).consume();
                }
                return null;
            });
        }
    }

    // =====================================================
    // CLIENTES
    // =====================================================

    private static void crearClientes(Session session) {

        session.run("""
            CREATE (:Cliente {
                id: 'CLI-001',
                razonSocial: 'Logistica del Sur S.A.',
                cuit: '30-12345678-9'
            })
        """);

        session.run("""
            CREATE (:Cliente {
                id: 'CLI-002',
                razonSocial: 'Comercio Internacional S.R.L.',
                cuit: '30-23456789-0'
            })
        """);

        session.run("""
            CREATE (:Cliente {
                id: 'CLI-003',
                razonSocial: 'Importadora Global S.A.',
                cuit: '30-34567890-1'
            })
        """);
    }

    // =====================================================
    // ENVIOS
    // =====================================================

    private static void crearEnvios(Session session) {

        crearEnvio(session, "ENV-001", "CLI-001", "CONT-001", "VEH-001", "2026-09-01T08:00:00Z", "2026-09-01", "2026-09-05", "Buenos Aires", "Argentina", "Montevideo", "Uruguay", "EN_TRANSITO", "ALTA", 185000.0);
        crearEnvio(session, "ENV-002", "CLI-002", "CONT-002", "VEH-002", "2026-09-02T08:00:00Z", "2026-09-02", "2026-09-07", "Buenos Aires", "Argentina", "Santiago", "Chile", "DEMORADO", "ALTA", 230000.0);
        crearEnvio(session, "ENV-003", "CLI-003", "CONT-003", "VEH-003", "2026-09-03T08:00:00Z", "2026-09-03", "2026-09-08", "Sao Paulo", "Brasil", "Buenos Aires", "Argentina", "ENTREGADO", "MEDIA", 275000.0);
        crearEnvio(session, "ENV-004", "CLI-001", "CONT-004", "VEH-001", "2026-09-04T08:00:00Z", "2026-09-04", "2026-09-09", "Montevideo", "Uruguay", "Sao Paulo", "Brasil", "PENDIENTE", "BAJA", 160000.0);
        crearEnvio(session, "ENV-005", "CLI-002", "CONT-005", "VEH-002", "2026-09-05T08:00:00Z", "2026-09-05", "2026-09-10", "Santiago", "Chile", "Buenos Aires", "Argentina", "EN_TRANSITO", "MEDIA", 210000.0);
        crearEnvio(session, "ENV-006", "CLI-003", "CONT-006", "VEH-003", "2026-09-06T08:00:00Z", "2026-09-06", "2026-09-11", "Buenos Aires", "Argentina", "Sao Paulo", "Brasil", "PENDIENTE", "ALTA", 255000.0);
        crearEnvio(session, "ENV-007", "CLI-001", "CONT-007", "VEH-001", "2026-09-07T08:00:00Z", "2026-09-07", "2026-09-12", "Sao Paulo", "Brasil", "Montevideo", "Uruguay", "CANCELADO", "BAJA", 145000.0);
        crearEnvio(session, "ENV-008", "CLI-002", "CONT-008", "VEH-002", "2026-09-08T08:00:00Z", "2026-09-08", "2026-09-13", "Montevideo", "Uruguay", "Santiago", "Chile", "EN_TRANSITO", "ALTA", 220000.0);
    }

    private static void crearEnvio(
            Session session,
            String envioId,
            String clienteId,
            String contenedorId,
            String vehiculoId,
            String fechaCreacion,
            String fechaSalida,
            String fechaEntrega,
            String ciudadOrigen,
            String paisOrigen,
            String ciudadDestino,
            String paisDestino,
            String estado,
            String prioridad,
            double costoEnvio
    ) {
        session.run("""
                MERGE (e:Envio {id: $envioId})
                SET e.fechaCreacion = $fechaCreacion,
                    e.fechaSalida = $fechaSalida,
                    e.fechaEntrega = $fechaEntrega,
                    e.estado = $estado,
                    e.prioridad = $prioridad,
                    e.costoEnvio = $costoEnvio
                REMOVE e.clienteId, e.ciudadOrigen, e.paisOrigen,
                       e.ciudadDestino, e.paisDestino
                WITH e
                MATCH (c:Cliente {id: $clienteId})
                MERGE (c)-[:REALIZA]->(e)
                WITH e
                MATCH (co:Contenedor {id: $contenedorId})
                MERGE (e)-[:TRANSPORTA]->(co)
                WITH e
                MATCH (v:Vehiculo {id: $vehiculoId})
                MERGE (e)-[:UTILIZA]->(v)
                WITH e
                MERGE (origen:Ubicacion {clave: $claveOrigen})
                SET origen.ciudad = $ciudadOrigen, origen.pais = $paisOrigen
                MERGE (e)-[:SALE_DE]->(origen)
                WITH e
                MERGE (destino:Ubicacion {clave: $claveDestino})
                SET destino.ciudad = $ciudadDestino, destino.pais = $paisDestino
                MERGE (e)-[:LLEGA_A]->(destino)
                """, Map.ofEntries(
                Map.entry("envioId", envioId),
                Map.entry("clienteId", clienteId),
                Map.entry("contenedorId", contenedorId),
                Map.entry("vehiculoId", vehiculoId),
                Map.entry("fechaCreacion", fechaCreacion),
                Map.entry("fechaSalida", fechaSalida),
                Map.entry("fechaEntrega", fechaEntrega),
                Map.entry("ciudadOrigen", ciudadOrigen),
                Map.entry("paisOrigen", paisOrigen),
                Map.entry("ciudadDestino", ciudadDestino),
                Map.entry("paisDestino", paisDestino),
                Map.entry("estado", estado),
                Map.entry("prioridad", prioridad),
                Map.entry("costoEnvio", costoEnvio),
                Map.entry("claveOrigen", claveUbicacion(ciudadOrigen, paisOrigen)),
                Map.entry("claveDestino", claveUbicacion(ciudadDestino, paisDestino))
        ));
    }

    // =====================================================
    // VEHICULOS
    // =====================================================

    private static void crearVehiculos(Session session) {

        session.run("""
            CREATE (:Vehiculo {
                id: 'VEH-001',
                identificacion: 'AA123AA',
                patente: 'AA123AA',
                tipo: 'Camion'
            })
        """);

        session.run("""
            CREATE (:Vehiculo {
                id: 'VEH-002',
                identificacion: 'AB456AB',
                patente: 'AB456AB',
                tipo: 'Camion'
            })
        """);

        session.run("""
            CREATE (:Vehiculo {
                id: 'VEH-003',
                identificacion: 'AC789AC',
                patente: 'AC789AC',
                tipo: 'Camion'
            })
        """);
    }

    // =====================================================
    // OPERADORES
    // =====================================================

    private static void crearOperadores(Session session) {

        session.run("""
            CREATE (:Operador {
                id: 'OP-001',
                nombre: 'Carlos Rodriguez'
            })
        """);

        session.run("""
            CREATE (:Operador {
                id: 'OP-002',
                nombre: 'Miguel Fernandez'
            })
        """);
    }

    // =====================================================
    // PROVEEDORES
    // =====================================================

    private static void crearProveedores(Session session) {

        session.run("""
            CREATE (:Proveedor {
                id: 'PROV-001',
                nombre: 'Transportes del Sur'
            })
        """);

        session.run("""
            CREATE (:Proveedor {
                id: 'PROV-002',
                nombre: 'Transportes Internacionales'
            });
        """);
    }

    // =====================================================
    // CONTENEDORES
    // =====================================================

    private static void crearContenedores(Session session) {

        session.run("""
            CREATE (:Contenedor {
                id: 'CONT-001',
                codigo: 'CONT-001',
                tipo: 'Refrigerado'
            })
        """);

        session.run("""
            CREATE (:Contenedor {
                id: 'CONT-002',
                codigo: 'CONT-002',
                tipo: 'Seco'
            })
        """);

        session.run("""
            CREATE (:Contenedor {
                id: 'CONT-003',
                codigo: 'CONT-003',
                tipo: 'Open Top'
            })
        """);
        for (int i = 4; i <= 8; i++) {
            String codigo = "CONT-" + String.format("%03d", i);
            session.run("MERGE (c:Contenedor {id: $id}) SET c.codigo = $id, c.tipo = 'Seco'",
                    Map.of("id", codigo));
        }
    }

    // =====================================================
    // UBICACIONES
    // =====================================================

    private static void crearUbicaciones(Session session) {

        session.run("""
            CREATE (:Ubicacion {
                id: 'UBI-001',
                clave: 'buenos aires|argentina',
                ciudad: 'Buenos Aires',
                pais: 'Argentina'
            })
        """);

        session.run("""
            CREATE (:Ubicacion {
                id: 'UBI-002',
                clave: 'montevideo|uruguay',
                ciudad: 'Montevideo',
                pais: 'Uruguay'
            })
        """);

        session.run("""
            CREATE (:Ubicacion {
                id: 'UBI-003',
                clave: 'santiago|chile',
                ciudad: 'Santiago',
                pais: 'Chile'
            })
        """);

        session.run("""
            CREATE (:Ubicacion {
                id: 'UBI-004',
                clave: 'sao paulo|brasil',
                ciudad: 'Sao Paulo',
                pais: 'Brasil'
            })
        """);
    }

    // =====================================================
    // SENSORES
    // =====================================================

    private static void crearSensores(Session session) {

        session.run("""
            CREATE (:Sensor {
                id: 'SEN-001',
                tipo: 'Temperatura'
            })
        """);

        session.run("""
            CREATE (:Sensor {
                id: 'SEN-002',
                tipo: 'GPS'
            })
        """);

        session.run("""
            CREATE (:Sensor {
                id: 'SEN-003',
                tipo: 'Humedad'
            })
        """);
    }

    // =====================================================
    // RELACIONES
    // =====================================================

    private static void crearRelaciones(Session session) {
        // Operador -> Vehiculo
        session.run("""
            MATCH (o:Operador {id: 'OP-001'})
            MATCH (v:Vehiculo {id: 'VEH-001'})
            CREATE (o)-[:OPERA]->(v)
        """);

        session.run("""
            MATCH (o:Operador {id: 'OP-002'})
            MATCH (v:Vehiculo {id: 'VEH-002'})
            CREATE (o)-[:OPERA]->(v)
        """);


        // Proveedor -> Vehiculo
        session.run("""
            MATCH (p:Proveedor {id: 'PROV-001'})
            MATCH (v:Vehiculo {id: 'VEH-001'})
            CREATE (p)-[:PROVEE]->(v)
        """);

        session.run("""
            MATCH (p:Proveedor {id: 'PROV-002'})
            MATCH (v:Vehiculo {id: 'VEH-002'})
            CREATE (p)-[:PROVEE]->(v)
        """);
        // Contenedor -> Sensor
        session.run("""
            MATCH (c:Contenedor {id: 'CONT-001'})
            MATCH (s:Sensor {id: 'SEN-001'})
            CREATE (c)-[:TIENE_SENSOR]->(s)
        """);

        session.run("""
            MATCH (c:Contenedor {id: 'CONT-002'})
            MATCH (s:Sensor {id: 'SEN-002'})
            CREATE (c)-[:TIENE_SENSOR]->(s)
        """);

        session.run("""
            MATCH (c:Contenedor {id: 'CONT-003'})
            MATCH (s:Sensor {id: 'SEN-003'})
            CREATE (c)-[:TIENE_SENSOR]->(s)
        """);
    }

    private static String claveUbicacion(String ciudad, String pais) {
        return ciudad.trim().toLowerCase() + "|" + pais.trim().toLowerCase();
    }
}
