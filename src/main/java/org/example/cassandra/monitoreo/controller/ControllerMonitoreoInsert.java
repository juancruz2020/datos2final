package org.example.cassandra.monitoreo.controller;

import org.example.cassandra.monitoreo.service.MonitoreoInsertService;
import org.example.logging.AppLog;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

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
            String sensorId,
            LocalDate fechaDia,
            Instant fechaHora,
            String contenedorId,
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

        AppLog.debug(ControllerMonitoreoInsert.class,
                "Lectura de monitoreo y métricas guardadas en Cassandra.");
    }
}
