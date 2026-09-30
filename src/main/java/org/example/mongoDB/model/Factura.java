package org.example.mongoDB.model;

import java.math.BigDecimal;
import java.util.Date;

public class Factura {

    private String id;
    private String clienteId;
    private Date fechaEmision;
    private BigDecimal importeTotal;
    private String estado;

    public Factura(String id, String clienteId, Date fechaEmision,
                   BigDecimal importeTotal, String estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.fechaEmision = fechaEmision;
        this.importeTotal = importeTotal;
        this.estado = estado;
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

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}