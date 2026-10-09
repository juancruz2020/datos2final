package org.example.neo4j.model;

import java.util.ArrayList;
import java.util.List;

public class Envio {

    private String id;
    private String clienteId;
    private List<String> contenedoresIds;
    private String fechaCreacion;
    private Ubicacion origen;
    private Ubicacion destino;
    private String estado;
    private String prioridad;

    // Constructor original: se conserva por compatibilidad.
    public Envio(
            String id,
            String estado,
            String prioridad
    ) {
        this.id = id;
        this.estado = estado;
        this.prioridad = prioridad;
        this.contenedoresIds = new ArrayList<>();
    }

    // Constructor completo.
    public Envio(
            String id,
            String clienteId,
            List<String> contenedoresIds,
            String fechaCreacion,
            Ubicacion origen,
            Ubicacion destino,
            String estado,
            String prioridad
    ) {
        this.id = id;
        this.clienteId = clienteId;
        this.contenedoresIds = contenedoresIds != null
                ? new ArrayList<>(contenedoresIds)
                : new ArrayList<>();
        this.fechaCreacion = fechaCreacion;
        this.origen = origen;
        this.destino = destino;
        this.estado = estado;
        this.prioridad = prioridad;
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
        this.contenedoresIds = contenedoresIds != null
                ? new ArrayList<>(contenedoresIds)
                : new ArrayList<>();
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
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
}
