
package org.example.mongoDB.model;

import java.math.BigDecimal;
import java.util.Date;

public class CierreMensualCuentaCorriente {

    private String id;
    private String cuentaCorrienteId;
    private int anio;
    private int mes;
    private BigDecimal saldoInicial;
    private BigDecimal totalDebitos;
    private BigDecimal totalCreditos;
    private BigDecimal saldoFinal;
    private Date fechaCierre;

    public CierreMensualCuentaCorriente(
            String id,
            String cuentaCorrienteId,
            int anio,
            int mes,
            BigDecimal saldoInicial,
            BigDecimal totalDebitos,
            BigDecimal totalCreditos,
            BigDecimal saldoFinal,
            Date fechaCierre
    ) {
        this.id = id;
        this.cuentaCorrienteId = cuentaCorrienteId;
        this.anio = anio;
        this.mes = mes;
        this.saldoInicial = saldoInicial;
        this.totalDebitos = totalDebitos;
        this.totalCreditos = totalCreditos;
        this.saldoFinal = saldoFinal;
        this.fechaCierre = fechaCierre;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getCuentaCorrienteId() {
        return cuentaCorrienteId;
    }


    public int getAnio() {
        return anio;
    }


    public int getMes() {
        return mes;
    }


    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }


    public BigDecimal getTotalDebitos() {
        return totalDebitos;
    }


    public BigDecimal getTotalCreditos() {
        return totalCreditos;
    }


    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }


    public Date getFechaCierre() {
        return fechaCierre;
    }

}
