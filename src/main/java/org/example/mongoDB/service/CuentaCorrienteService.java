
package org.example.mongoDB.service;

import com.mongodb.client.ClientSession;

import org.bson.Document;
import org.bson.types.Decimal128;

import org.example.conecciones.MongoSingleton;
import org.example.mongoDB.dao.CuentaCorrienteMongoDAO;
import org.example.mongoDB.dao.MovimientoCuentaCorrienteMongoDAO;
import org.example.mongoDB.dao.CierreMensualCuentaCorrienteMongoDAO;
import org.example.mongoDB.model.CuentaCorriente;
import org.example.mongoDB.model.MovimientoCuentaCorriente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

public class CuentaCorrienteService {

    private final CuentaCorrienteMongoDAO cuentaDAO;
    private final MovimientoCuentaCorrienteMongoDAO movimientoDAO;
    private final CierreMensualCuentaCorrienteMongoDAO cierreDAO;

    public CuentaCorrienteService() {

        this.cuentaDAO = new CuentaCorrienteMongoDAO();
        this.movimientoDAO = new MovimientoCuentaCorrienteMongoDAO();
        this.cierreDAO = new CierreMensualCuentaCorrienteMongoDAO();

        movimientoDAO.crearIndiceUnico();
    }

    // =========================================
    // CREAR CUENTA CORRIENTE
    // =========================================

    public CuentaCorriente crearCuenta(String clienteId) {

        if (clienteId == null || clienteId.isBlank()) {
            throw new IllegalArgumentException(
                    "El cliente es obligatorio."
            );
        }

        if (cuentaDAO.buscarPorCliente(clienteId) != null) {
            throw new IllegalArgumentException(
                    "El cliente ya tiene una cuenta corriente."
            );
        }

        CuentaCorriente cuenta = new CuentaCorriente(
                null,
                clienteId,
                BigDecimal.ZERO,
                new Date()
        );

        cuentaDAO.agregar(cuenta);

        return cuenta;
    }

    // =========================================
    // BUSCAR CUENTA POR CLIENTE
    // =========================================

    public Document buscarPorCliente(String clienteId) {

        if (clienteId == null || clienteId.isBlank()) {
            throw new IllegalArgumentException(
                    "El cliente es obligatorio."
            );
        }

        return cuentaDAO.buscarPorCliente(clienteId);
    }

    // =========================================
    // LISTAR CUENTAS CORRIENTES
    // =========================================

    public List<Document> listarCuentas() {
        return cuentaDAO.listarTodos();
    }

    // =========================================
    // CONSULTAR SALDO
    // =========================================

    public BigDecimal consultarSaldo(String clienteId) {

        Document cuenta = buscarPorCliente(clienteId);

        if (cuenta == null) {
            throw new IllegalArgumentException(
                    "El cliente no tiene una cuenta corriente."
            );
        }

        return cuenta.get("saldo", Decimal128.class)
                .bigDecimalValue();
    }

    // =========================================
    // REGISTRAR DÉBITO
    // =========================================

    public void registrarDebito(
            String clienteId,
            String referenciaId,
            String descripcion,
            BigDecimal importe
    ) {

        registrarMovimiento(
                clienteId,
                "DEBITO",
                referenciaId,
                descripcion,
                importe
        );
    }

    // =========================================
    // REGISTRAR CRÉDITO
    // =========================================

    public void registrarCredito(
            String clienteId,
            String referenciaId,
            String descripcion,
            BigDecimal importe
    ) {

        registrarMovimiento(
                clienteId,
                "CREDITO",
                referenciaId,
                descripcion,
                importe
        );
    }

    // =========================================
    // REGISTRAR MOVIMIENTO CON TRANSACCIÓN
    // =========================================

    private void registrarMovimiento(
            String clienteId,
            String tipo,
            String referenciaId,
            String descripcion,
            BigDecimal importe
    ) {

        if (importe == null ||
                importe.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El importe debe ser mayor a cero."
            );
        }

        if (referenciaId == null || referenciaId.isBlank()) {
            throw new IllegalArgumentException(
                    "La referencia es obligatoria."
            );
        }

        Document cuenta = buscarPorCliente(clienteId);

        if (cuenta == null) {
            throw new IllegalArgumentException(
                    "El cliente no tiene una cuenta corriente."
            );
        }

        String cuentaId = cuenta.getObjectId("_id").toHexString();

        try (ClientSession session =
                     MongoSingleton.getInstance().startSession()) {

            session.startTransaction();

            try {

                // =========================================
                // VALIDAR CIERRE DEL MES ANTERIOR
                // =========================================

                LocalDate hoy = LocalDate.now(ZoneOffset.UTC);

                LocalDate primerPeriodo = LocalDate.of(2026, 10, 1);

                LocalDate mesAnterior = hoy
                        .withDayOfMonth(1)
                        .minusMonths(1);

                if (!hoy.isBefore(LocalDate.of(2026, 11, 1))) {

                    Document cierreAnterior =
                            cierreDAO.buscarPorPeriodo(
                                    session,
                                    cuentaId,
                                    mesAnterior.getYear(),
                                    mesAnterior.getMonthValue()
                            );

                    if (cierreAnterior == null) {

                        throw new IllegalStateException(
                                "Primero debe cerrarse el mes anterior."
                        );
                    }

                    // Verificar todos los meses pendientes
                    LocalDate periodo = primerPeriodo;

                    while (periodo.isBefore(
                            hoy.withDayOfMonth(1))) {

                        Document cierre =
                                cierreDAO.buscarPorPeriodo(
                                        session,
                                        cuentaId,
                                        periodo.getYear(),
                                        periodo.getMonthValue()
                                );

                        if (cierre == null) {
                            throw new IllegalStateException(
                                    "Hay períodos anteriores sin cerrar: "
                                            + periodo.getMonthValue()
                                            + "/"
                                            + periodo.getYear()
                            );
                        }

                        periodo = periodo.plusMonths(1);
                    }
                }

                // =========================================
                // BUSCAR SALDO ACTUAL
                // =========================================

                Document cuentaActual = cuentaDAO.buscarPorId(
                        session,
                        cuentaId
                );

                if (cuentaActual == null) {
                    throw new IllegalArgumentException(
                            "No se encontró la cuenta corriente."
                    );
                }

                BigDecimal saldoActual = cuentaActual
                        .get("saldo", Decimal128.class)
                        .bigDecimalValue();

                BigDecimal nuevoSaldo;

                if (tipo.equals("DEBITO")) {
                    nuevoSaldo = saldoActual.add(importe);
                } else {
                    nuevoSaldo = saldoActual.subtract(importe);
                }

                // =========================================
                // CREAR MOVIMIENTO
                // =========================================

                MovimientoCuentaCorriente movimiento =
                        new MovimientoCuentaCorriente(
                                null,
                                cuentaId,
                                tipo,
                                referenciaId,
                                descripcion,
                                importe,
                                new Date()
                        );

                // =========================================
                // GUARDAR MOVIMIENTO Y ACTUALIZAR SALDO
                // =========================================

                movimientoDAO.agregar(session, movimiento);

                cuentaDAO.actualizarSaldo(
                        session,
                        cuentaId,
                        nuevoSaldo
                );

                session.commitTransaction();

            } catch (Exception e) {

                if (session.hasActiveTransaction()) {
                    session.abortTransaction();
                }

                throw e;
            }
        }
    }

    // =========================================
    // CONSULTAR HISTORIAL POR CLIENTE
    // =========================================

    public List<Document> consultarHistorial(
            String clienteId
    ) {

        Document cuenta = buscarPorCliente(clienteId);

        if (cuenta == null) {
            throw new IllegalArgumentException(
                    "El cliente no tiene una cuenta corriente."
            );
        }

        String cuentaId = cuenta.getObjectId("_id").toHexString();

        return movimientoDAO.buscarPorCuenta(cuentaId);
    }
}
