
package org.example.mongoDB.model;

import java.math.BigDecimal;
import java.util.Date;

public class PagoProveedor {

    private String id;
    private String facturaProveedorId;
    private Date fecha;
    private BigDecimal importe;
    private String metodoPago;

    public PagoProveedor(
            String id,
            String facturaProveedorId,
            Date fecha,
            BigDecimal importe,
            String metodoPago
    ) {
        this.id = id;
        this.facturaProveedorId = facturaProveedorId;
        this.fecha = fecha;
        this.importe = importe;
        this.metodoPago = metodoPago;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFacturaProveedorId() {
        return facturaProveedorId;
    }

    public Date getFecha() {
        return fecha;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public String getMetodoPago() {
        return metodoPago;
    }
}
