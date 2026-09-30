package org.example.mongoDB.model;

import java.util.Date;
import java.util.List;

public class Envio {

    private String id;
    private String clienteId;
    private List<String> contenedoresIds;
    private Date fechaCreacion;
    private Ubicacion origen;
    private Ubicacion destino;
    private String estado;
    private String prioridad;
    private List<Tramo> tramos;

    public Envio(
            String id,
            String clienteId,
            List<String> contenedoresIds,
            Date fechaCreacion,
            Ubicacion origen,
            Ubicacion destino,
            String estado,
            String prioridad,
            List<Tramo> tramos
    ) {
        this.id = id;
        this.clienteId = clienteId;
        this.contenedoresIds = contenedoresIds;
        this.fechaCreacion = fechaCreacion;
        this.origen = origen;
        this.destino = destino;
        this.estado = estado;
        this.prioridad = prioridad;
        this.tramos = tramos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public List<String> getContenedoresIds() {
        return contenedoresIds;
    }

    public void setContenedoresIds(List<String> contenedoresIds) {
        this.contenedoresIds = contenedoresIds;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Ubicacion getOrigen() {
        return origen;
    }

    public void setOrigen(Ubicacion origen) {
        this.origen = origen;
    }

    public Ubicacion getDestino() {
        return destino;
    }

    public void setDestino(Ubicacion destino) {
        this.destino = destino;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public List<Tramo> getTramos() {
        return tramos;
    }

    public void setTramos(List<Tramo> tramos) {
        this.tramos = tramos;
    }
}