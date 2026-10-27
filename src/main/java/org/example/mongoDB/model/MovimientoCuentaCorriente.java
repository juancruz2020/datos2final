
package org.example.mongoDB.model;

import java.math.BigDecimal;
import java.util.Date;

public class MovimientoCuentaCorriente {

    private String id;
    private String cuentaCorrienteId;
    private String tipo;
    private String referenciaId;
    private String descripcion;
    private BigDecimal importe;
    private Date fecha;

    public MovimientoCuentaCorriente(
            String id,
            String cuentaCorrienteId,
            String tipo,
            String referenciaId,
            String descripcion,
            BigDecimal importe,
            Date fecha
    ) {
        this.id = id;
        this.cuentaCorrienteId = cuentaCorrienteId;
        this.tipo = tipo;
        this.referenciaId = referenciaId;
        this.descripcion = descripcion;
        this.importe = importe;
        this.fecha = fecha;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getCuentaCorrienteId() {
        return cuentaCorrienteId;
    }


    public String getTipo() {
        return tipo;
    }


    public String getReferenciaId() {
        return referenciaId;
    }


    public String getDescripcion() {
        return descripcion;
    }


    public BigDecimal getImporte() {
        return importe;
    }


    public Date getFecha() {
        return fecha;
    }

}
