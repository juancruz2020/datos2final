package org.example.neo4j.model;

import java.util.ArrayList;
import java.util.List;

public class Envio {

    private String id;
    private String clienteId;
    private List<String> contenedoresIds;
    private String fechaCreacion;
    private String fechaSalida;
    private String fechaEntrega;
    private Ubicacion origen;
    private Ubicacion destino;
    private String estado;
    private String prioridad;
    private Double costoEnvio;

    // Constructor original: se conserva por compatibilidad.

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

    public Envio(
            String id,
            String clienteId,
            List<String> contenedoresIds,
            String fechaCreacion,
            String fechaSalida,
            String fechaEntrega,
            Ubicacion origen,
            Ubicacion destino,
            String estado,
            String prioridad
    ) {
        this(id, clienteId, contenedoresIds, fechaCreacion,
                origen, destino, estado, prioridad);
        this.fechaSalida = fechaSalida;
        this.fechaEntrega = fechaEntrega;
    }

    public String getId() {
        return id;
    }


    public String getClienteId() {
        return clienteId;
    }


    public List<String> getContenedoresIds() {
        return contenedoresIds;
    }


    public String getFechaCreacion() {
        return fechaCreacion;
    }


    public String getFechaSalida() {
        return fechaSalida;
    }


    public String getFechaEntrega() {
        return fechaEntrega;
    }


    public Ubicacion getOrigen() {
        return origen;
    }


    public Ubicacion getDestino() {
        return destino;
    }


    public String getEstado() {
        return estado;
    }


    public String getPrioridad() {
        return prioridad;
    }

    public Double getCostoEnvio() { return costoEnvio; }

    public void setCostoEnvio(Double costoEnvio) { this.costoEnvio = costoEnvio; }

}
