package org.example.neo4j.model;

import java.time.LocalDateTime;

public class Tramo {

    private String id;
    private String medioTransporte;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaLlegadaEstimada;

    public Tramo(
            String id,
            String medioTransporte,
            LocalDateTime fechaSalida,
            LocalDateTime fechaLlegadaEstimada
    ) {
        this.id = id;
        this.medioTransporte = medioTransporte;
        this.fechaSalida = fechaSalida;
        this.fechaLlegadaEstimada = fechaLlegadaEstimada;
    }

    public String getId() {
        return id;
    }

    public String getMedioTransporte() {
        return medioTransporte;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public LocalDateTime getFechaLlegadaEstimada() {
        return fechaLlegadaEstimada;
    }
}