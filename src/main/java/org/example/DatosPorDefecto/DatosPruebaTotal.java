package org.example.DatosPorDefecto;

import org.example.mongoDB.controller.ControllerMongoDB;

import java.util.List;
import org.example.logging.AppLog;

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

        List<String> collections = mongo.mostrarCollections();

        if (collections == null || collections.isEmpty()) {

            // =====================================================
            // 1. GENERAR DATOS EN MONGODB
            // =====================================================

            GeneradorDatosPruebaMongo generador =
                    new GeneradorDatosPruebaMongo();

            generador.generarDatos();

            // Los envíos conservan ENV-001 a ENV-008 y sus referencias
            // se vinculan a los IDs reales de MongoDB.
            GeneradorDatosPruebaNeo.generar();

            // =====================================================
            // 2. OBTENER IDS REALES DESDE MONGODB
            // =====================================================

            List<String> idsMongoSensor =
                    mongo.obtenerIdsSensores();

            List<String> idsMongoContenedor =
                    mongo.obtenerIdsContenedores();

            List<String> idsMongoUsuario =
                    mongo.obtenerIdsUsuarios();

            // =====================================================
            // 3. GENERAR SESIONES DE PRUEBA EN REDIS
            // =====================================================

            GenerarDatosPruebaRedis.generar(idsMongoUsuario);

            // =====================================================
            // 4. VALIDAR QUE EXISTAN SENSORES Y CONTENEDORES
            // =====================================================

            if (idsMongoSensor == null || idsMongoSensor.isEmpty()) {

                AppLog.warning(DatosPruebaTotal.class,
                        "No se encontraron sensores MongoDB para preparar Cassandra.");

                return;
            }

            if (idsMongoContenedor == null || idsMongoContenedor.isEmpty()) {

                AppLog.warning(DatosPruebaTotal.class,
                        "No se encontraron contenedores MongoDB para preparar Cassandra.");

                return;
            }

            // =====================================================
            // 4. MOSTRAR CANTIDAD DE IDS ENCONTRADOS
            // =====================================================

            AppLog.info(DatosPruebaTotal.class,
                    "Sensores disponibles para datos de prueba: "
                            + idsMongoSensor.size());

            AppLog.info(DatosPruebaTotal.class,
                    "Contenedores disponibles para datos de prueba: "
                            + idsMongoContenedor.size());

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

            AppLog.info(DatosPruebaTotal.class,
                    "Datos de prueba coordinados cargados correctamente.");
        } else {
            // También reparar los datos de prueba cargados con versiones anteriores.
            AppLog.info(DatosPruebaTotal.class,
                    "Hay datos existentes; se sincronizarán las referencias de Neo4j.");
            GeneradorDatosPruebaNeo.sincronizarReferenciasMongo();
        }
        new org.example.neo4j.dao.EnvioNeo4jDAO().normalizarEnviosExistentes();
    }
}
