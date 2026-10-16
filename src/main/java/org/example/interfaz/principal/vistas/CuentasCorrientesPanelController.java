
package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import org.example.mongoDB.controller.ClienteController;
import org.example.mongoDB.service.CuentaCorrienteService;
import org.example.mongoDB.service.CierreMensualCuentaCorrienteService;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CuentasCorrientesPanelController {

    private final CuentasCorrientesPanel view;

    private final CuentaCorrienteService cuentaService;
    private final CierreMensualCuentaCorrienteService cierreService;
    private final ClienteController clienteController;

    private final List<String> cuentasIds;

    // =========================================
    // CONSTRUCTOR
    // =========================================

    public CuentasCorrientesPanelController(
            CuentasCorrientesPanel view
    ) {

        this.view = view;

        cuentaService = new CuentaCorrienteService();
        cierreService = new CierreMensualCuentaCorrienteService();
        clienteController = new ClienteController();

        cuentasIds = new ArrayList<>();

        configurarEventos();
        cargarCuentas();
    }

    // =========================================
    // EVENTOS
    // =========================================

    private void configurarEventos() {

        view.getBtnActualizar().addActionListener(
                e -> cargarCuentas()
        );

        view.getBtnRegistrarDebito().addActionListener(
                e -> registrarMovimiento("DEBITO")
        );

        view.getBtnRegistrarCredito().addActionListener(
                e -> registrarMovimiento("CREDITO")
        );

        view.getBtnVerMovimientos().addActionListener(
                e -> mostrarMovimientos()
        );

        view.getBtnVerCierres().addActionListener(
                e -> mostrarCierres()
        );

        view.getBtnGenerarCierre().addActionListener(
                e -> generarCierreMensual()
        );
    }

    // =========================================
    // CARGAR CUENTAS
    // =========================================

    private void cargarCuentas() {

        try {

            List<Document> cuentas = cuentaService.listarCuentas();

            DefaultTableModel modelo =
                    (DefaultTableModel) view
                            .getTablaCuentas()
                            .getModel();

            modelo.setRowCount(0);
            cuentasIds.clear();

            for (Document cuenta : cuentas) {

                ObjectId cuentaId = cuenta.getObjectId("_id");
                ObjectId clienteId = cuenta.getObjectId("cliente_id");

                if (cuentaId == null) {
                    continue;
                }

                modelo.addRow(new Object[]{
                        obtenerNombreCliente(clienteId),
                        obtenerImporte(cuenta.get("saldo")),
                        formatearFecha(
                                cuenta.getDate("fecha_actualizacion")
                        )
                });

                cuentasIds.add(cuentaId.toHexString());
            }

            view.getLblEstado().setText(
                    "Cuentas registradas: " + cuentasIds.size()
            );

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar las cuentas.",
                    e
            );
        }
    }

    // =========================================
    // REGISTRAR DÉBITO O CRÉDITO
    // =========================================

    private void registrarMovimiento(String tipo) {

        String cuentaId = obtenerCuentaSeleccionada();

        if (cuentaId == null) {
            return;
        }

        try {

            Document cuenta = buscarCuentaPorId(cuentaId);

            if (cuenta == null) {
                throw new IllegalStateException(
                        "No se encontró la cuenta seleccionada."
                );
            }

            ObjectId clienteId = cuenta.getObjectId("cliente_id");

            if (clienteId == null) {
                throw new IllegalStateException(
                        "La cuenta no tiene un cliente asociado."
                );
            }

            String nombreCliente = obtenerNombreCliente(clienteId);

            JTextField txtImporte = new JTextField(20);
            JTextField txtDescripcion = new JTextField(20);
            JTextField txtReferencia = new JTextField(20);

            JPanel formulario = new JPanel(
                    new GridLayout(0, 1, 5, 5)
            );

            formulario.add(
                    new JLabel("Cliente: " + nombreCliente)
            );

            formulario.add(
                    new JLabel(
                            "Tipo: " +
                                    (tipo.equals("DEBITO")
                                            ? "Débito"
                                            : "Crédito")
                    )
            );

            formulario.add(new JLabel("Importe:"));
            formulario.add(txtImporte);

            formulario.add(new JLabel("Descripción:"));
            formulario.add(txtDescripcion);

            formulario.add(new JLabel("Referencia única:"));
            formulario.add(txtReferencia);

            int opcion = JOptionPane.showConfirmDialog(
                    view,
                    formulario,
                    "Registrar " +
                            (tipo.equals("DEBITO")
                                    ? "débito"
                                    : "crédito"),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (opcion != JOptionPane.OK_OPTION) {
                return;
            }

            String importeTexto = txtImporte.getText()
                    .trim()
                    .replace(",", ".");

            String descripcion = txtDescripcion.getText().trim();
            String referencia = txtReferencia.getText().trim();

            BigDecimal importe;

            try {

                importe = new BigDecimal(importeTexto);

            } catch (NumberFormatException e) {

                JOptionPane.showMessageDialog(
                        view,
                        "Ingresá un importe válido.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (importe.compareTo(BigDecimal.ZERO) <= 0) {

                JOptionPane.showMessageDialog(
                        view,
                        "El importe debe ser mayor que cero.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (descripcion.isEmpty() || referencia.isEmpty()) {

                JOptionPane.showMessageDialog(
                        view,
                        "La descripción y la referencia son obligatorias.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (tipo.equals("DEBITO")) {

                cuentaService.registrarDebito(
                        clienteId.toHexString(),
                        referencia,
                        descripcion,
                        importe
                );

            } else {

                cuentaService.registrarCredito(
                        clienteId.toHexString(),
                        referencia,
                        descripcion,
                        importe
                );
            }

            JOptionPane.showMessageDialog(
                    view,
                    "Movimiento registrado correctamente.",
                    "Cuentas corrientes",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cargarCuentas();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo registrar el movimiento.",
                    e
            );
        }
    }

    // =========================================
    // MOSTRAR MOVIMIENTOS
    // =========================================

    private void mostrarMovimientos() {

        String cuentaId = obtenerCuentaSeleccionada();

        if (cuentaId == null) {
            return;
        }

        try {

            Document cuenta = buscarCuentaPorId(cuentaId);

            if (cuenta == null) {
                throw new IllegalStateException(
                        "No se encontró la cuenta seleccionada."
                );
            }

            ObjectId clienteId = cuenta.getObjectId("cliente_id");

            if (clienteId == null) {
                throw new IllegalStateException(
                        "La cuenta no tiene un cliente asociado."
                );
            }

            List<Document> movimientos =
                    cuentaService.consultarHistorial(
                            clienteId.toHexString()
                    );

            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[]{
                            "Fecha",
                            "Tipo",
                            "Descripción",
                            "Referencia",
                            "Importe"
                    },
                    0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Document movimiento : movimientos) {

                modelo.addRow(new Object[]{
                        formatearFecha(
                                movimiento.getDate("fecha")
                        ),
                        movimiento.getString("tipo"),
                        movimiento.getString("descripcion"),
                        movimiento.getString("referencia_id"),
                        obtenerImporte(movimiento.get("importe"))
                });
            }

            mostrarTabla(
                    "Movimientos de cuenta corriente",
                    modelo
            );

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron consultar los movimientos.",
                    e
            );
        }
    }

    // =========================================
    // MOSTRAR CIERRES MENSUALES
    // =========================================

    private void mostrarCierres() {

        String cuentaId = obtenerCuentaSeleccionada();

        if (cuentaId == null) {
            return;
        }

        try {

            List<Document> cierres =
                    cierreService.consultarCierres(cuentaId);

            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[]{
                            "Período",
                            "Saldo inicial",
                            "Débitos",
                            "Créditos",
                            "Saldo final",
                            "Fecha de cierre"
                    },
                    0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Document cierre : cierres) {

                int mes = cierre.getInteger("mes");
                int anio = cierre.getInteger("anio");

                modelo.addRow(new Object[]{
                        String.format("%02d/%d", mes, anio),
                        obtenerImporte(
                                cierre.get("saldo_inicial")
                        ),
                        obtenerImporte(
                                cierre.get("total_debitos")
                        ),
                        obtenerImporte(
                                cierre.get("total_creditos")
                        ),
                        obtenerImporte(
                                cierre.get("saldo_final")
                        ),
                        formatearFecha(
                                cierre.getDate("fecha_cierre")
                        )
                });
            }

            mostrarTabla(
                    "Cierres mensuales",
                    modelo
            );

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron consultar los cierres.",
                    e
            );
        }
    }

    // =========================================
    // GENERAR CIERRE MENSUAL
    // =========================================

    private void generarCierreMensual() {

        String cuentaId = obtenerCuentaSeleccionada();

        if (cuentaId == null) {
            return;
        }

        try {

            Document cuenta = buscarCuentaPorId(cuentaId);

            if (cuenta == null) {
                throw new IllegalStateException(
                        "No se encontró la cuenta seleccionada."
                );
            }

            String cliente = obtenerNombreCliente(
                    cuenta.getObjectId("cliente_id")
            );

            JComboBox<String> cmbMes = new JComboBox<>(
                    new String[]{
                            "Enero", "Febrero", "Marzo",
                            "Abril", "Mayo", "Junio",
                            "Julio", "Agosto", "Septiembre",
                            "Octubre", "Noviembre", "Diciembre"
                    }
            );

            JSpinner spinnerAnio = new JSpinner(
                    new SpinnerNumberModel(
                            2026, 2026, 2100, 1
                    )
            );

            cmbMes.setSelectedIndex(9);

            JPanel formulario = new JPanel(
                    new GridLayout(0, 1, 5, 5)
            );

            formulario.add(
                    new JLabel("Cliente: " + cliente)
            );

            formulario.add(new JLabel("Mes:"));
            formulario.add(cmbMes);

            formulario.add(new JLabel("Año:"));
            formulario.add(spinnerAnio);

            int opcion = JOptionPane.showConfirmDialog(
                    view,
                    formulario,
                    "Generar cierre mensual",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (opcion != JOptionPane.OK_OPTION) {
                return;
            }

            int mes = cmbMes.getSelectedIndex() + 1;
            int anio = (Integer) spinnerAnio.getValue();

            YearMonth periodo = YearMonth.of(anio, mes);
            YearMonth actual = YearMonth.now();

            if (!periodo.isBefore(actual)) {

                JOptionPane.showMessageDialog(
                        view,
                        "Solo se pueden cerrar meses que ya terminaron.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (periodo.isBefore(
                    YearMonth.of(2026, 10)
            )) {

                JOptionPane.showMessageDialog(
                        view,
                        "El primer período contable es octubre de 2026.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            int confirmacion = JOptionPane.showConfirmDialog(
                    view,
                    "¿Confirmás el cierre de " +
                            String.format("%02d/%d", mes, anio) +
                            " para " + cliente + "?\n\n" +
                            "El cierre quedará registrado en MongoDB.",
                    "Confirmar cierre mensual",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }

            cierreService.cerrarMes(
                    cuentaId,
                    anio,
                    mes
            );

            JOptionPane.showMessageDialog(
                    view,
                    "Cierre mensual generado correctamente.",
                    "Cuentas corrientes",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cargarCuentas();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo generar el cierre mensual.",
                    e
            );
        }
    }

    // =========================================
    // BUSCAR CUENTA POR ID
    // =========================================

    private Document buscarCuentaPorId(String cuentaId) {

        for (Document cuenta : cuentaService.listarCuentas()) {

            ObjectId id = cuenta.getObjectId("_id");

            if (id != null &&
                    id.toHexString().equals(cuentaId)) {

                return cuenta;
            }
        }

        return null;
    }

    // =========================================
    // OBTENER CUENTA SELECCIONADA
    // =========================================

    private String obtenerCuentaSeleccionada() {

        int filaVista = view.getTablaCuentas().getSelectedRow();

        if (filaVista == -1) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná una cuenta corriente.",
                    "Cuentas corrientes",
                    JOptionPane.WARNING_MESSAGE
            );

            return null;
        }

        int filaModelo = view.getTablaCuentas()
                .convertRowIndexToModel(filaVista);

        if (filaModelo < 0 || filaModelo >= cuentasIds.size()) {
            return null;
        }

        return cuentasIds.get(filaModelo);
    }

    // =========================================
    // OBTENER NOMBRE DEL CLIENTE
    // =========================================

    private String obtenerNombreCliente(ObjectId clienteId) {

        if (clienteId == null) {
            return "";
        }

        try {

            Document cliente = clienteController.buscarPorId(
                    clienteId.toHexString()
            );

            if (cliente != null) {

                String nombre = cliente.getString("razon_social");

                if (nombre != null && !nombre.isBlank()) {
                    return nombre;
                }
            }

        } catch (Exception ignored) {
            // Si falla la consulta, mostramos el ID.
        }

        return clienteId.toHexString();
    }

    // =========================================
    // FORMATEAR IMPORTE
    // =========================================

    private String obtenerImporte(Object valor) {

        if (valor == null) {
            return "0.00";
        }

        if (valor instanceof Decimal128) {

            return ((Decimal128) valor)
                    .bigDecimalValue()
                    .toPlainString();
        }

        return valor.toString();
    }

    // =========================================
    // FORMATEAR FECHA
    // =========================================

    private String formatearFecha(Date fecha) {

        if (fecha == null) {
            return "";
        }

        return new SimpleDateFormat(
                "dd/MM/yyyy HH:mm"
        ).format(fecha);
    }

    // =========================================
    // MOSTRAR TABLA
    // =========================================

    private void mostrarTabla(
            String titulo,
            DefaultTableModel modelo
    ) {

        JTable tabla = new JTable(modelo);

        tabla.setRowHeight(28);

        JScrollPane scroll = new JScrollPane(tabla);

        scroll.setPreferredSize(
                new Dimension(850, 350)
        );

        JOptionPane.showMessageDialog(
                view,
                scroll,
                titulo,
                JOptionPane.PLAIN_MESSAGE
        );
    }

    // =========================================
    // MOSTRAR ERROR
    // =========================================

    private void mostrarError(
            String mensaje,
            Exception e
    ) {

        JOptionPane.showMessageDialog(
                view,
                mensaje + "\n\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
