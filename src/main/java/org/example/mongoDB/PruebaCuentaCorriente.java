
package org.example.mongoDB;

import org.example.mongoDB.service.CuentaCorrienteService;

import java.math.BigDecimal;

public class PruebaCuentaCorriente {

    public static void main(String[] args) {

        CuentaCorrienteService service =
                new CuentaCorrienteService();

        String clienteId = "6ace5fc67bff0717d45aa12b";

        System.out.println(
                "Saldo anterior: $" +
                service.consultarSaldo(clienteId)
        );

        service.registrarCredito(
                clienteId,
                "PRUEBA-CREDITO-001",
                "Crédito de prueba para compensar el débito",
                new BigDecimal("1500.00")
        );

        System.out.println(
                "Saldo actualizado: $" +
                service.consultarSaldo(clienteId)
        );
    }
}
