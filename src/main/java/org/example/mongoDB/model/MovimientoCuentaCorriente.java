
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCuentaCorrienteId() {
        return cuentaCorrienteId;
    }

    public void setCuentaCorrienteId(String cuentaCorrienteId) {
        this.cuentaCorrienteId = cuentaCorrienteId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getReferenciaId() {
        return referenciaId;
    }

    public void setReferenciaId(String referenciaId) {
        this.referenciaId = referenciaId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
