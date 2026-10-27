package org.example.mongoDB.model;

import java.math.BigDecimal;
import java.util.Date;

public class Pago {

    private String id;
    private String facturaId;
    private Date fecha;
    private BigDecimal monto;
    private String medioPago;

    public Pago(String id, String facturaId, Date fecha,
                BigDecimal monto, String medioPago) {
        this.id = id;
        this.facturaId = facturaId;
        this.fecha = fecha;
        this.monto = monto;
        this.medioPago = medioPago;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFacturaId() {
        return facturaId;
    }


    public Date getFecha() {
        return fecha;
    }


    public BigDecimal getMonto() {
        return monto;
    }


    public String getMedioPago() {
        return medioPago;
    }

}