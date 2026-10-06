package org.example.DatosPorDefecto;

import org.example.mongoDB.controller.ControllerMongoDB;

import java.util.List;

public class DatosPruebaTotal {

    private final ControllerMongoDB mongo;
    private final DatosDePruebaCassandra cassandra;
    private CrearTablasMonitoreo tablascass;

    public DatosPruebaTotal() {
        this.mongo = new ControllerMongoDB();
        this.cassandra = new DatosDePruebaCassandra();
        this.tablascass = new CrearTablasMonitoreo();
    }

    // =====================================================
    // CARGAR DATOS DE PRUEBA
    // =====================================================

    public void cargarDatos() {

        tablascass.crearTablas();

        // Obtener IDs reales desde MongoDB
        List<String> idsMongoSensor =
                mongo.obtenerIdsSensores();

        List<String> idsMongoContenedor =
                mongo.obtenerIdsContenedores();

        // Mostrar cantidad de IDs encontrados
        System.out.println(
                "Sensores encontrados en MongoDB: "
                        + idsMongoSensor.size()
        );

        System.out.println(
                "Contenedores encontrados en MongoDB: "
                        + idsMongoContenedor.size()
        );

        // =================================================
        // GENERAR 60 LECTURAS EN CASSANDRA
        // =================================================

        cassandra.insertarDatos(
                idsMongoSensor,
                idsMongoContenedor
        );
    }
}