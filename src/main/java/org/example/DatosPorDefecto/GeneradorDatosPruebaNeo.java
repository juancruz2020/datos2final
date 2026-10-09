package org.example.DatosPorDefecto;

import org.example.conecciones.Neo4jSingleton;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.util.Map;

public class GeneradorDatosPruebaNeo {

    private GeneradorDatosPruebaNeo() {
        // Clase utilitaria
    }

    public static void generar() {

        Driver driver = Neo4jSingleton.getInstance();

        try (Session session = driver.session()) {

            System.out.println("=================================");
            System.out.println(" GENERANDO DATOS DE PRUEBA NEO4J");
            System.out.println("=================================");

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

            System.out.println();
            System.out.println("Datos de prueba generados correctamente.");
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

        crearEnvio(session, "ENV-001", "CLI-001", "CONT-001", "VEH-001", "2026-09-01T08:00:00Z", "Buenos Aires", "Argentina", "Montevideo", "Uruguay", "EN_TRANSITO", "ALTA");
        crearEnvio(session, "ENV-002", "CLI-002", "CONT-002", "VEH-002", "2026-09-02T08:00:00Z", "Buenos Aires", "Argentina", "Santiago", "Chile", "DEMORADO", "ALTA");
        crearEnvio(session, "ENV-003", "CLI-003", "CONT-003", "VEH-003", "2026-09-03T08:00:00Z", "Sao Paulo", "Brasil", "Buenos Aires", "Argentina", "ENTREGADO", "MEDIA");
        crearEnvio(session, "ENV-004", "CLI-001", "CONT-002", "VEH-001", "2026-09-04T08:00:00Z", "Montevideo", "Uruguay", "Sao Paulo", "Brasil", "PENDIENTE", "BAJA");
        crearEnvio(session, "ENV-005", "CLI-002", "CONT-003", "VEH-002", "2026-09-05T08:00:00Z", "Santiago", "Chile", "Buenos Aires", "Argentina", "EN_TRANSITO", "MEDIA");
        crearEnvio(session, "ENV-006", "CLI-003", "CONT-001", "VEH-003", "2026-09-06T08:00:00Z", "Buenos Aires", "Argentina", "Sao Paulo", "Brasil", "PENDIENTE", "ALTA");
        crearEnvio(session, "ENV-007", "CLI-001", "CONT-003", "VEH-001", "2026-09-07T08:00:00Z", "Sao Paulo", "Brasil", "Montevideo", "Uruguay", "CANCELADO", "BAJA");
        crearEnvio(session, "ENV-008", "CLI-002", "CONT-002", "VEH-002", "2026-09-08T08:00:00Z", "Montevideo", "Uruguay", "Santiago", "Chile", "EN_TRANSITO", "ALTA");
    }

    private static void crearEnvio(
            Session session,
            String envioId,
            String clienteId,
            String contenedorId,
            String vehiculoId,
            String fechaCreacion,
            String ciudadOrigen,
            String paisOrigen,
            String ciudadDestino,
            String paisDestino,
            String estado,
            String prioridad
    ) {
        session.run("""
                MERGE (e:Envio {id: $envioId})
                SET e.clienteId = $clienteId,
                    e.fechaCreacion = $fechaCreacion,
                    e.ciudadOrigen = $ciudadOrigen,
                    e.paisOrigen = $paisOrigen,
                    e.ciudadDestino = $ciudadDestino,
                    e.paisDestino = $paisDestino,
                    e.estado = $estado,
                    e.prioridad = $prioridad
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
                Map.entry("ciudadOrigen", ciudadOrigen),
                Map.entry("paisOrigen", paisOrigen),
                Map.entry("ciudadDestino", ciudadDestino),
                Map.entry("paisDestino", paisDestino),
                Map.entry("estado", estado),
                Map.entry("prioridad", prioridad),
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
