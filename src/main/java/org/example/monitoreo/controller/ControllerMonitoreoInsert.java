package org.example.monitoreo.controller;

import org.example.monitoreo.service.MonitoreoInsertService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class ControllerMonitoreoInsert {

    private final MonitoreoInsertService insertService;

    public ControllerMonitoreoInsert() {

        this.insertService =
                new MonitoreoInsertService();
    }


    // =====================================================
    // INSERTAR LECTURA DE SENSOR
    // =====================================================

    public void insertarLecturaSensor(
            UUID sensorId,
            LocalDate fechaDia,
            Instant fechaHora,
            UUID contenedorId,
            BigDecimal temperatura,
            BigDecimal humedad,
            BigDecimal vibracion,
            BigDecimal latitud,
            BigDecimal longitud,
            BigDecimal bateria,
            String pais,
            String region) {

        insertService.insertarLecturaSensor(
                sensorId,
                fechaDia,
                fechaHora,
                contenedorId,
                temperatura,
                humedad,
                vibracion,
                latitud,
                longitud,
                bateria,
                pais,
                region
        );

        System.out.println(
                "Lectura del sensor insertada correctamente."
        );
    }


    // =====================================================
    // INSERTAR MÉTRICA POR REGIÓN
    // =====================================================

    public void insertarMetricaRegion(
            String region,
            LocalDate fechaDia,
            BigDecimal temperaturaMin,
            BigDecimal temperaturaMax,
            BigDecimal humedadPromedio,
            long cantidadLecturas) {

        insertService.insertarMetricaRegion(
                region,
                fechaDia,
                temperaturaMin,
                temperaturaMax,
                humedadPromedio,
                cantidadLecturas
        );

        System.out.println(
                "Métrica de región insertada correctamente."
        );
    }


    // =====================================================
    // INSERTAR MÉTRICA POR PAÍS
    // =====================================================

    public void insertarMetricaPais(
            String pais,
            LocalDate fechaDia,
            BigDecimal humedadPromedio,
            BigDecimal temperaturaPromedio,
            long cantidadLecturas) {

        insertService.insertarMetricaPais(
                pais,
                fechaDia,
                humedadPromedio,
                temperaturaPromedio,
                cantidadLecturas
        );

        System.out.println(
                "Métrica de país insertada correctamente."
        );
    }
}