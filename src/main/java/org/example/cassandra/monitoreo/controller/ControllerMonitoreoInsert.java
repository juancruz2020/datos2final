package org.example.cassandra.monitoreo.controller;

import org.example.cassandra.monitoreo.service.MonitoreoInsertService;

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

        System.out.println(
                "Métricas de región y país actualizadas automáticamente."
        );
    }
}