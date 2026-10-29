
package org.example.mongoDB.model;

import java.math.BigDecimal;
import java.util.Date;

public class FacturaProveedor {

    private String id;
    private String proveedorId;
    private String numeroFactura;
    private String concepto;
    private Date fechaEmision;
    private Date fechaVencimiento;
    private BigDecimal importeTotal;
    private String estado;

    public FacturaProveedor(
            String id,
            String proveedorId,
            String numeroFactura,
            String concepto,
            Date fechaEmision,
            Date fechaVencimiento,
            BigDecimal importeTotal,
            String estado
    ) {
        this.id = id;
        this.proveedorId = proveedorId;
        this.numeroFactura = numeroFactura;
        this.concepto = concepto;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.importeTotal = importeTotal;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProveedorId() {
        return proveedorId;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public String getConcepto() {
        return concepto;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
