package org.example.DatosPorDefecto;

import org.example.mongoDB.controller.ControllerMongoDB;

import java.util.List;

public class DatosPruebaTotal {

    private final ControllerMongoDB mongo;
    private final DatosDePruebaCassandra cassandra;
    private final CrearTablasMonitoreo tablascass;

    public DatosPruebaTotal() {
        this.mongo = new ControllerMongoDB();
        this.cassandra = new DatosDePruebaCassandra();
        this.tablascass = new CrearTablasMonitoreo();
    }

    // =====================================================
    // CARGAR DATOS DE PRUEBA
    // =====================================================

    public void cargarDatos() {

        // =====================================================
        // 1. GENERAR DATOS EN MONGODB
        // =====================================================

        GeneradorDatosPruebaMongo generador =
                new GeneradorDatosPruebaMongo();

        generador.generarDatos();

        // =====================================================
        // 2. OBTENER IDS REALES DESDE MONGODB
        // =====================================================

        List<String> idsMongoSensor =
                mongo.obtenerIdsSensores();

        List<String> idsMongoContenedor =
                mongo.obtenerIdsContenedores();

        // =====================================================
        // 3. VALIDAR QUE EXISTAN SENSORES Y CONTENEDORES
        // =====================================================

        if (idsMongoSensor == null || idsMongoSensor.isEmpty()) {

            System.out.println(
                    "No se encontraron sensores en MongoDB."
            );

            return;
        }

        if (idsMongoContenedor == null || idsMongoContenedor.isEmpty()) {

            System.out.println(
                    "No se encontraron contenedores en MongoDB."
            );

            return;
        }

        // =====================================================
        // 4. MOSTRAR CANTIDAD DE IDS ENCONTRADOS
        // =====================================================

        System.out.println(
                "Sensores encontrados en MongoDB: "
                        + idsMongoSensor.size()
        );

        System.out.println(
                "Contenedores encontrados en MongoDB: "
                        + idsMongoContenedor.size()
        );

        // =====================================================
        // 5. CREAR TABLAS DE CASSANDRA
        // =====================================================
        // Si ya existen, CREATE TABLE IF NOT EXISTS
        // no las vuelve a crear.

        tablascass.crearTablas();

        // =====================================================
        // 6. GENERAR LECTURAS EN CASSANDRA
        // =====================================================

        cassandra.insertarDatos(
                idsMongoSensor,
                idsMongoContenedor
        );

        // =====================================================
        // 7. FINALIZADO
        // =====================================================

        System.out.println(
                "Datos de prueba cargados correctamente."
        );
    }
}