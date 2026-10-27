package org.example.mongoDB.model;

import java.math.BigDecimal;
import java.util.Date;

public class CuentaCorriente {

    private String id;
    private String clienteId;
    private BigDecimal saldo;
    private Date fechaActualizacion;

    public CuentaCorriente(String id, String clienteId,
                           BigDecimal saldo, Date fechaActualizacion) {
        this.id = id;
        this.clienteId = clienteId;
        this.saldo = saldo;
        this.fechaActualizacion = fechaActualizacion;
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


    public BigDecimal getSaldo() {
        return saldo;
    }


    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

}