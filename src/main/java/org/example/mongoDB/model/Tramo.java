package org.example.mongoDB.model;

import java.util.Date;

public class Tramo {

    private String medioTransporte;
    private String origen;
    private String destino;
    private Date fechaSalida;
    private Date fechaLlegadaEstimada;

    public Tramo(
            String medioTransporte,
            String origen,
            String destino,
            Date fechaSalida,
            Date fechaLlegadaEstimada
    ) {
        this.medioTransporte = medioTransporte;
        this.origen = origen;
        this.destino = destino;
        this.fechaSalida = fechaSalida;
        this.fechaLlegadaEstimada = fechaLlegadaEstimada;
    }

    public String getMedioTransporte() {
        return medioTransporte;
    }

    public void setMedioTransporte(String medioTransporte) {
        this.medioTransporte = medioTransporte;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public Date getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(Date fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public Date getFechaLlegadaEstimada() {
        return fechaLlegadaEstimada;
    }

    public void setFechaLlegadaEstimada(Date fechaLlegadaEstimada) {
        this.fechaLlegadaEstimada = fechaLlegadaEstimada;
    }
}