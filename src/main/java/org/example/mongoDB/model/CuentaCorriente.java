
package org.example.mongoDB.model;

import java.math.BigDecimal;
import java.util.Date;

public class CuentaCorriente {

    private String id;
    private String clienteId;
    private String proveedorId;
    private BigDecimal saldo;
    private Date fechaActualizacion;

    // =========================
    // CONSTRUCTOR ORIGINAL
    // CUENTA DE CLIENTE
    // =========================

    public CuentaCorriente(
            String id,
            String clienteId,
            BigDecimal saldo,
            Date fechaActualizacion
    ) {
        this.id = id;
        this.clienteId = clienteId;
        this.proveedorId = null;
        this.saldo = saldo;
        this.fechaActualizacion = fechaActualizacion;
    }

    // =========================
    // CONSTRUCTOR PARA PROVEEDOR
    // =========================

    public CuentaCorriente(
            String id,
            String clienteId,
            String proveedorId,
            BigDecimal saldo,
            Date fechaActualizacion
    ) {
        this.id = id;
        this.clienteId = clienteId;
        this.proveedorId = proveedorId;
        this.saldo = saldo;
        this.fechaActualizacion = fechaActualizacion;
    }

    // =========================
    // GETTERS Y SETTERS
    // =========================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public String getProveedorId() {
        return proveedorId;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }
}
