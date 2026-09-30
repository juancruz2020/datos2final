package org.example.neo4j;

import org.example.conecciones.Neo4jSingleton;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import java.util.Map;

public class GeneradorDatosPrueba {

    private GeneradorDatosPrueba() {
        // Clase utilitaria
    }

    public static void generar() {

        Driver driver = Neo4jSingleton.getInstance();

        try (Session session = driver.session()) {

            System.out.println("=================================");
            System.out.println(" GENERANDO DATOS DE PRUEBA NEO4J");
            System.out.println("=================================");

            crearClientes(session);
            crearEnvios(session);
            crearTramos(session);
            crearVehiculos(session);
            crearOperadores(session);
            crearProveedores(session);
            crearContenedores(session);
            crearUbicaciones(session);
            crearSensores(session);

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

        session.run("""
            CREATE (:Envio {
                id: 'ENV-001',
                estado: 'EN_TRANSITO',
                prioridad: 'ALTA'
            })
        """);

        session.run("""
            CREATE (:Envio {
                id: 'ENV-002',
                estado: 'DEMORADO',
                prioridad: 'ALTA'
            })
        """);

        session.run("""
            CREATE (:Envio {
                id: 'ENV-003',
                estado: 'ENTREGADO',
                prioridad: 'MEDIA'
            })
        """);
    }

    // =====================================================
    // TRAMOS
    // =====================================================

    private static void crearTramos(Session session) {

        session.run("""
            CREATE (:Tramo {
                id: 'TRA-001',
                medioTransporte: 'Camion',
                fechaSalida: datetime('2026-09-02T08:00:00'),
                fechaLlegada: datetime('2026-09-04T18:00:00')
            })
        """);

        session.run("""
            CREATE (:Tramo {
                id: 'TRA-002',
                medioTransporte: 'Camion',
                fechaSalida: datetime('2026-09-06T08:00:00'),
                fechaLlegada: datetime('2026-09-07T18:00:00')
            })
        """);

        session.run("""
            CREATE (:Tramo {
                id: 'TRA-003',
                medioTransporte: 'Maritimo',
                fechaSalida: datetime('2026-09-10T08:00:00'),
                fechaLlegada: datetime('2026-09-15T18:00:00')
            })
        """);
    }

    // =====================================================
    // VEHICULOS
    // =====================================================

    private static void crearVehiculos(Session session) {

        session.run("""
            CREATE (:Vehiculo {
                id: 'VEH-001',
                patente: 'AA123AA',
                tipo: 'Camion'
            })
        """);

        session.run("""
            CREATE (:Vehiculo {
                id: 'VEH-002',
                patente: 'AB456AB',
                tipo: 'Camion'
            })
        """);

        session.run("""
            CREATE (:Vehiculo {
                id: 'VEH-003',
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
                ciudad: 'Buenos Aires',
                pais: 'Argentina'
            })
        """);

        session.run("""
            CREATE (:Ubicacion {
                id: 'UBI-002',
                ciudad: 'Montevideo',
                pais: 'Uruguay'
            })
        """);

        session.run("""
            CREATE (:Ubicacion {
                id: 'UBI-003',
                ciudad: 'Santiago',
                pais: 'Chile'
            })
        """);

        session.run("""
            CREATE (:Ubicacion {
                id: 'UBI-004',
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

        // Cliente -> Envio
        session.run("""
            MATCH (c:Cliente {id: 'CLI-001'})
            MATCH (e:Envio {id: 'ENV-001'})
            CREATE (c)-[:REALIZA]->(e)
        """);

        session.run("""
            MATCH (c:Cliente {id: 'CLI-002'})
            MATCH (e:Envio {id: 'ENV-002'})
            CREATE (c)-[:REALIZA]->(e)
        """);

        session.run("""
            MATCH (c:Cliente {id: 'CLI-003'})
            MATCH (e:Envio {id: 'ENV-003'})
            CREATE (c)-[:REALIZA]->(e)
        """);


        // Envio -> Tramo
        session.run("""
            MATCH (e:Envio {id: 'ENV-001'})
            MATCH (t:Tramo {id: 'TRA-001'})
            CREATE (e)-[:TIENE]->(t)
        """);

        session.run("""
            MATCH (e:Envio {id: 'ENV-002'})
            MATCH (t:Tramo {id: 'TRA-002'})
            CREATE (e)-[:TIENE]->(t)
        """);

        session.run("""
            MATCH (e:Envio {id: 'ENV-003'})
            MATCH (t:Tramo {id: 'TRA-003'})
            CREATE (e)-[:TIENE]->(t)
        """);


        // Tramo -> Ubicacion origen
        session.run("""
            MATCH (t:Tramo {id: 'TRA-001'})
            MATCH (u:Ubicacion {id: 'UBI-001'})
            CREATE (t)-[:SALE_DE]->(u)
        """);

        session.run("""
            MATCH (t:Tramo {id: 'TRA-002'})
            MATCH (u:Ubicacion {id: 'UBI-001'})
            CREATE (t)-[:SALE_DE]->(u)
        """);

        session.run("""
            MATCH (t:Tramo {id: 'TRA-003'})
            MATCH (u:Ubicacion {id: 'UBI-001'})
            CREATE (t)-[:SALE_DE]->(u)
        """);


        // Tramo -> Ubicacion destino
        session.run("""
            MATCH (t:Tramo {id: 'TRA-001'})
            MATCH (u:Ubicacion {id: 'UBI-002'})
            CREATE (t)-[:LLEGA_A]->(u)
        """);

        session.run("""
            MATCH (t:Tramo {id: 'TRA-002'})
            MATCH (u:Ubicacion {id: 'UBI-003'})
            CREATE (t)-[:LLEGA_A]->(u)
        """);

        session.run("""
            MATCH (t:Tramo {id: 'TRA-003'})
            MATCH (u:Ubicacion {id: 'UBI-004'})
            CREATE (t)-[:LLEGA_A]->(u)
        """);


        // Tramo -> Vehiculo
        session.run("""
            MATCH (t:Tramo {id: 'TRA-001'})
            MATCH (v:Vehiculo {id: 'VEH-001'})
            CREATE (t)-[:USA]->(v)
        """);

        session.run("""
            MATCH (t:Tramo {id: 'TRA-002'})
            MATCH (v:Vehiculo {id: 'VEH-002'})
            CREATE (t)-[:USA]->(v)
        """);

        session.run("""
            MATCH (t:Tramo {id: 'TRA-003'})
            MATCH (v:Vehiculo {id: 'VEH-003'})
            CREATE (t)-[:USA]->(v)
        """);


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


        // Envio -> Contenedor
        session.run("""
            MATCH (e:Envio {id: 'ENV-001'})
            MATCH (c:Contenedor {id: 'CONT-001'})
            CREATE (e)-[:TRANSPORTA]->(c)
        """);

        session.run("""
            MATCH (e:Envio {id: 'ENV-002'})
            MATCH (c:Contenedor {id: 'CONT-002'})
            CREATE (e)-[:TRANSPORTA]->(c)
        """);

        session.run("""
            MATCH (e:Envio {id: 'ENV-003'})
            MATCH (c:Contenedor {id: 'CONT-003'})
            CREATE (e)-[:TRANSPORTA]->(c)
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
}