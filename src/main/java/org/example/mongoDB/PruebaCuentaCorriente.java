
package org.example.mongoDB;

import org.example.mongoDB.service.CuentaCorrienteService;
import org.example.logging.AppLog;

import java.math.BigDecimal;

public class PruebaCuentaCorriente {

    public static void main(String[] args) {

        CuentaCorrienteService service =
                new CuentaCorrienteService();

        String clienteId = "6ace5fc67bff0717d45aa12b";

        AppLog.info(PruebaCuentaCorriente.class,
                "Consulta de prueba de saldo completada.");

        service.registrarCredito(
                clienteId,
                "PRUEBA-CREDITO-001",
                "Crédito de prueba para compensar el débito",
                new BigDecimal("1500.00")
        );

        AppLog.info(PruebaCuentaCorriente.class,
                "Actualización y consulta de saldo de prueba completadas.");
    }
}
