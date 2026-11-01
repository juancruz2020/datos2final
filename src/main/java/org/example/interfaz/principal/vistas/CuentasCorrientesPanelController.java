
package org.example.interfaz.principal.vistas;

import org.example.mongoDB.controller.ControllerMongoDB;
import org.example.mongoDB.dao.MovimientoCuentaCorrienteMongoDAO;
import org.example.mongoDB.service.CuentaCorrienteService;
import org.example.mongoDB.service.CierreMensualCuentaCorrienteService;
import org.example.mongoDB.service.PermisosService;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CuentasCorrientesPanelController {

    private static final ZoneId ZONA =
            ZoneId.of("America/Argentina/Buenos_Aires");

    private final ControllerMongoDB mongo = new ControllerMongoDB();

    private final CuentasCorrientesPanel view;
    private final String usuarioId;

    private final PermisosService permisosService;
    private final CuentaCorrienteService cuentaService;
    private final CierreMensualCuentaCorrienteService cierreService;
    private final MovimientoCuentaCorrienteMongoDAO movimientoDAO;

    private final List<String> cuentasIds;
    private final List<Boolean> cuentasInactivas;

    private final boolean proveedor;
    private final String proveedorId;

    // =========================================
    // CONSTRUCTOR
    // =========================================

    public CuentasCorrientesPanelController(
            CuentasCorrientesPanel view,
            String usuarioId
    ) {

        this.view = view;
        this.usuarioId = usuarioId;

        permisosService = new PermisosService();
        cuentaService = new CuentaCorrienteService();
        cierreService = new CierreMensualCuentaCorrienteService();
        movimientoDAO = new MovimientoCuentaCorrienteMongoDAO();

        cuentasIds = new ArrayList<>();
        cuentasInactivas = new ArrayList<>();

        if (!permisosService.puedeAccederVista(
                usuarioId,
                "CUENTAS_CORRIENTES"
        )) {
            throw new SecurityException(
                    "No tenés permisos para acceder a Cuentas Corrientes."
            );
        }

        proveedor = permisosService.esProveedor(usuarioId);

        proveedorId = proveedor
                ? permisosService.obtenerProveedorId(usuarioId)
                : null;

        if (proveedor) {
            view.getCmbTipoCuenta().setSelectedIndex(1);
            view.getCmbTipoCuenta().setEnabled(false);
        }

        configurarTabla();
        configurarEventos();
        actualizarBotones();
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

        view.getCmbTipoCuenta().addActionListener(e -> {
            actualizarBotones();
            cargarCuentas();
        });

        view.getTablaCuentas()
                .getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        actualizarBotones();
                    }
                });
    }

    // =========================================
    // CONFIGURAR COLORES DE LA TABLA
    // =========================================

    private void configurarTabla() {

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable tabla,
                            Object valor,
                            boolean seleccionado,
                            boolean tieneFoco,
                            int fila,
                            int columna
                    ) {

                        Component componente =
                                super.getTableCellRendererComponent(
                                        tabla,
                                        valor,
                                        seleccionado,
                                        tieneFoco,
                                        fila,
                                        columna
                                );

                        int filaModelo =
                                tabla.convertRowIndexToModel(fila);

                        boolean inactiva =
                                filaModelo >= 0
                                && filaModelo < cuentasInactivas.size()
                                && cuentasInactivas.get(filaModelo);

                        if (seleccionado) {

                            componente.setBackground(
                                    tabla.getSelectionBackground()
                            );

                            componente.setForeground(
                                    tabla.getSelectionForeground()
                            );

                        } else if (inactiva) {

                            componente.setBackground(
                                    new Color(235, 235, 235)
                            );

                            componente.setForeground(
                                    new Color(130, 130, 130)
                            );

                        } else {

                            componente.setBackground(
                                    tabla.getBackground()
                            );

                            componente.setForeground(
                                    tabla.getForeground()
                            );
                        }

                        return componente;
                    }
                };

        view.getTablaCuentas().setDefaultRenderer(
                Object.class,
                renderer
        );
    }

    // =========================================
    // ACTUALIZAR BOTONES
    // =========================================

    private void actualizarBotones() {

        boolean mostrandoProveedores =
                view.mostrandoProveedores();

        boolean puedeModificar = !proveedor;

        // Los movimientos de proveedores se registran
        // exclusivamente desde la pantalla Pagos.

        view.getBtnRegistrarDebito().setVisible(
                puedeModificar && !mostrandoProveedores
        );

        view.getBtnRegistrarCredito().setVisible(
                puedeModificar && !mostrandoProveedores
        );

        view.getBtnGenerarCierre().setVisible(
                puedeModificar
        );

        view.getBtnRegistrarDebito().setText(
                "+ Registrar débito"
        );

        view.getBtnRegistrarCredito().setText(
                "+ Registrar crédito"
        );

        view.getBtnVerMovimientos().setVisible(true);
        view.getBtnVerCierres().setVisible(true);

        boolean cuentaActiva = !cuentaSeleccionadaInactiva();

        view.getBtnRegistrarDebito().setEnabled(
                puedeModificar
                        && !mostrandoProveedores
                        && cuentaActiva
        );

        view.getBtnRegistrarCredito().setEnabled(
                puedeModificar
                        && !mostrandoProveedores
                        && cuentaActiva
        );

        view.getBtnGenerarCierre().setEnabled(
                puedeModificar && cuentaActiva
        );

        view.revalidate();
        view.repaint();
    }

    // =========================================
    // VERIFICAR CUENTA INACTIVA SELECCIONADA
    // =========================================

    private boolean cuentaSeleccionadaInactiva() {

        int filaVista =
                view.getTablaCuentas().getSelectedRow();

        if (filaVista < 0) {
            return false;
        }

        int filaModelo =
                view.getTablaCuentas()
                        .convertRowIndexToModel(filaVista);

        if (filaModelo < 0
                || filaModelo >= cuentasInactivas.size()) {
            return false;
        }

        return cuentasInactivas.get(filaModelo);
    }

    // =========================================
    // VERIFICAR ESTADO DEL TITULAR
    // =========================================

    private boolean titularInactivo(
            ObjectId id,
            boolean esProveedor
    ) {

        if (id == null) {
            throw new IllegalStateException(
                    "La cuenta no tiene un titular válido."
            );
        }

        Document titular = esProveedor
                ? mongo.buscarProveedorPorId(id.toHexString())
                : mongo.consultarClientePorId(id.toHexString());

        if (titular == null) {
            throw new IllegalStateException(
                    "No se encontró el titular de la cuenta."
            );
        }

        String estado = titular.getString("estado");

        if (estado == null || estado.isBlank()) {
            throw new IllegalStateException(
                    "No se pudo verificar el estado del titular."
            );
        }

        return !"ACTIVO".equalsIgnoreCase(estado.trim());
    }

    // =========================================
    // CARGAR CUENTAS
    // =========================================

    private void cargarCuentas() {

        DefaultTableModel modelo =
                (DefaultTableModel)
                        view.getTablaCuentas().getModel();

        modelo.setRowCount(0);
        cuentasIds.clear();
        cuentasInactivas.clear();

        try {

            boolean mostrandoProveedores =
                    view.mostrandoProveedores();

            if (proveedor && !mostrandoProveedores) {
                throw new SecurityException(
                        "No tenés permisos para consultar cuentas de clientes."
                );
            }

            List<Document> cuentas =
                    cuentaService.listarCuentasParaUsuario(usuarioId);

            view.getTablaCuentas()
                    .getColumnModel()
                    .getColumn(0)
                    .setHeaderValue(
                            mostrandoProveedores
                                    ? "Proveedor"
                                    : "Cliente"
                    );

            view.getTablaCuentas()
                    .getTableHeader()
                    .repaint();

            for (Document cuenta : cuentas) {

                ObjectId cuentaId =
                        cuenta.getObjectId("_id");

                ObjectId clienteId =
                        cuenta.getObjectId("cliente_id");

                ObjectId cuentaProveedorId =
                        cuenta.getObjectId("proveedor_id");

                if (cuentaId == null) {
                    continue;
                }

                String nombre;
                boolean inactiva;

                if (mostrandoProveedores) {

                    if (cuentaProveedorId == null) {
                        continue;
                    }

                    if (proveedor
                            && !cuentaProveedorId.toHexString()
                                    .equals(proveedorId)) {
                        continue;
                    }

                    nombre = obtenerNombreProveedor(
                            cuentaProveedorId
                    );

                    inactiva = titularInactivo(
                            cuentaProveedorId,
                            true
                    );

                } else {

                    if (clienteId == null) {
                        continue;
                    }

                    nombre = obtenerNombreCliente(
                            clienteId
                    );

                    inactiva = titularInactivo(
                            clienteId,
                            false
                    );
                }

                modelo.addRow(new Object[]{
                        nombre,
                        obtenerImporte(cuenta.get("saldo")),
                        formatearFecha(
                                cuenta.getDate("fecha_actualizacion")
                        )
                });

                cuentasIds.add(cuentaId.toHexString());
                cuentasInactivas.add(inactiva);
            }

            view.getLblEstado().setText(
                    (mostrandoProveedores
                            ? "Cuentas de proveedores: "
                            : "Cuentas de clientes: ")
                            + cuentasIds.size()
            );

            view.getTablaCuentas().clearSelection();
            actualizarBotones();
            view.getTablaCuentas().repaint();

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar las cuentas.",
                    e
            );
        }
    }

    // =========================================
    // REGISTRAR MOVIMIENTOS DE CLIENTES
    // =========================================

    private void registrarMovimiento(String tipo) {

        try {

            // No se permiten movimientos de proveedores
            // desde la pantalla Cuentas Corrientes.

            if (view.mostrandoProveedores() || proveedor) {
                throw new SecurityException(
                        "Las facturas y pagos de proveedores "
                                + "se registran desde la pantalla Pagos."
                );
            }

            permisosService.exigirAdminUOperador(usuarioId);

            String cuentaId = obtenerCuentaSeleccionada();

            if (cuentaId == null) {
                return;
            }

            Document cuenta = buscarCuentaPorId(cuentaId);

            if (cuenta == null) {
                throw new IllegalStateException(
                        "No se encontró la cuenta seleccionada."
                );
            }

            ObjectId clienteId =
                    cuenta.getObjectId("cliente_id");

            if (clienteId == null) {
                throw new IllegalStateException(
                        "La cuenta no tiene un cliente asociado."
                );
            }

            // Comprobar el estado real en MongoDB,
            // aunque la tabla no se haya actualizado.

            if (titularInactivo(clienteId, false)) {
                throw new IllegalStateException(
                        "No se pueden registrar movimientos "
                                + "porque el cliente está INACTIVO."
                );
            }

            String nombre =
                    obtenerNombreCliente(clienteId);

            String nombreMovimiento =
                    "DEBITO".equals(tipo)
                            ? "débito"
                            : "crédito";

            JTextField txtImporte = new JTextField(20);
            JTextField txtDescripcion = new JTextField(20);
            JTextField txtReferencia = new JTextField(20);

            JPanel formulario = new JPanel(
                    new GridLayout(0, 1, 5, 5)
            );

            formulario.add(new JLabel(
                    "Cliente: " + nombre
            ));

            formulario.add(new JLabel(
                    "Tipo: " + nombreMovimiento
            ));

            formulario.add(new JLabel("Importe:"));
            formulario.add(txtImporte);

            formulario.add(new JLabel("Descripción:"));
            formulario.add(txtDescripcion);

            formulario.add(new JLabel("Referencia única:"));
            formulario.add(txtReferencia);

            int opcion = JOptionPane.showConfirmDialog(
                    view,
                    formulario,
                    "Registrar " + nombreMovimiento,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (opcion != JOptionPane.OK_OPTION) {
                return;
            }

            // Verificar nuevamente antes de registrar.

            if (titularInactivo(clienteId, false)) {
                throw new IllegalStateException(
                        "No se puede registrar el movimiento "
                                + "porque el cliente está INACTIVO."
                );
            }

            String importeTexto = txtImporte
                    .getText()
                    .trim()
                    .replace(",", ".");

            String descripcion =
                    txtDescripcion.getText().trim();

            String referencia =
                    txtReferencia.getText().trim();

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

            if (descripcion.isEmpty()
                    || referencia.isEmpty()) {

                JOptionPane.showMessageDialog(
                        view,
                        "La descripción y la referencia son obligatorias.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if ("DEBITO".equals(tipo)) {

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

        try {

            String cuentaId = obtenerCuentaSeleccionada();

            if (cuentaId == null) {
                return;
            }

            Document cuenta = buscarCuentaPorId(cuentaId);

            if (cuenta == null) {
                throw new IllegalStateException(
                        "No se encontró la cuenta seleccionada."
                );
            }

            boolean mostrandoProveedores =
                    view.mostrandoProveedores();

            List<Document> movimientos;

            if (mostrandoProveedores) {

                if (cuenta.getObjectId("proveedor_id") == null) {
                    throw new IllegalStateException(
                            "La cuenta no tiene un proveedor asociado."
                    );
                }

                movimientos =
                        movimientoDAO.buscarPorCuenta(cuentaId);

            } else {

                ObjectId clienteId =
                        cuenta.getObjectId("cliente_id");

                if (clienteId == null) {
                    throw new IllegalStateException(
                            "La cuenta no tiene un cliente asociado."
                    );
                }

                movimientos =
                        cuentaService.consultarHistorial(
                                clienteId.toHexString()
                        );
            }

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
                public boolean isCellEditable(
                        int row,
                        int column
                ) {
                    return false;
                }
            };

            for (Document movimiento : movimientos) {

                String tipo = movimiento.getString("tipo");

                // En proveedores:
                // DEBITO = factura recibida
                // CREDITO = pago realizado

                if (mostrandoProveedores) {

                    if ("DEBITO".equals(tipo)) {
                        tipo = "FACTURA";

                    } else if ("CREDITO".equals(tipo)) {
                        tipo = "PAGO";
                    }
                }

                modelo.addRow(new Object[]{
                        formatearFecha(
                                movimiento.getDate("fecha")
                        ),
                        tipo,
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

        try {

            String cuentaId = obtenerCuentaSeleccionada();

            if (cuentaId == null) {
                return;
            }

            Document cuenta = buscarCuentaPorId(cuentaId);

            if (cuenta == null) {
                throw new IllegalStateException(
                        "No se encontró la cuenta seleccionada."
                );
            }

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
                public boolean isCellEditable(
                        int row,
                        int column
                ) {
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

        try {

            permisosService.exigirAdminUOperador(usuarioId);

            String cuentaId = obtenerCuentaSeleccionada();

            if (cuentaId == null) {
                return;
            }

            Document cuenta = buscarCuentaPorId(cuentaId);

            if (cuenta == null) {
                throw new IllegalStateException(
                        "No se encontró la cuenta seleccionada."
                );
            }

            boolean mostrandoProveedores =
                    view.mostrandoProveedores();

            ObjectId titularId = cuenta.getObjectId(
                    mostrandoProveedores
                            ? "proveedor_id"
                            : "cliente_id"
            );

            if (titularId == null) {
                throw new IllegalStateException(
                        "La cuenta no tiene un titular asociado."
                );
            }

            // Bloquear nuevos cierres de cuentas inactivas.

            if (titularInactivo(
                    titularId,
                    mostrandoProveedores
            )) {

                throw new IllegalStateException(
                        "No se puede generar un cierre mensual "
                                + "porque el titular está INACTIVO."
                );
            }

            String nombre = mostrandoProveedores
                    ? obtenerNombreProveedor(titularId)
                    : obtenerNombreCliente(titularId);

            YearMonth actual = YearMonth.now(ZONA);
            YearMonth anterior = actual.minusMonths(1);

            JComboBox<String> cmbMes = new JComboBox<>(
                    new String[]{
                            "Enero",
                            "Febrero",
                            "Marzo",
                            "Abril",
                            "Mayo",
                            "Junio",
                            "Julio",
                            "Agosto",
                            "Septiembre",
                            "Octubre",
                            "Noviembre",
                            "Diciembre"
                    }
            );

            JSpinner spinnerAnio = new JSpinner(
                    new SpinnerNumberModel(
                            anterior.getYear(),
                            2026,
                            2100,
                            1
                    )
            );

            cmbMes.setSelectedIndex(
                    anterior.getMonthValue() - 1
            );

            JPanel formulario = new JPanel(
                    new GridLayout(0, 1, 5, 5)
            );

            formulario.add(new JLabel(
                    (mostrandoProveedores
                            ? "Proveedor: "
                            : "Cliente: ") + nombre
            ));

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

            // Verificar nuevamente antes de cerrar.

            if (titularInactivo(
                    titularId,
                    mostrandoProveedores
            )) {

                throw new IllegalStateException(
                        "No se puede generar el cierre porque "
                                + "el titular está INACTIVO."
                );
            }

            int mes = cmbMes.getSelectedIndex() + 1;
            int anio = (Integer) spinnerAnio.getValue();

            YearMonth periodo = YearMonth.of(anio, mes);

            if (!periodo.isBefore(actual)) {

                JOptionPane.showMessageDialog(
                        view,
                        "Solo se pueden cerrar meses que ya terminaron.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (periodo.isBefore(YearMonth.of(2026, 10))) {

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
                    "¿Confirmás el cierre de "
                            + String.format("%02d/%d", mes, anio)
                            + " para " + nombre + "?\n\n"
                            + "El cierre quedará registrado en MongoDB.",
                    "Confirmar cierre mensual",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }

            cierreService.cerrarMes(cuentaId, anio, mes);

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
    // BUSCAR CUENTA POR ID Y VALIDAR ACCESO
    // =========================================

    private Document buscarCuentaPorId(String cuentaId) {

        if (!permisosService.puedeAccederVista(
                usuarioId,
                "CUENTAS_CORRIENTES"
        )) {
            throw new SecurityException(
                    "No tenés permisos para consultar cuentas corrientes."
            );
        }

        if (proveedor) {

            Document cuenta =
                    cuentaService.buscarPorProveedor(proveedorId);

            if (cuenta == null) {
                return null;
            }

            ObjectId id = cuenta.getObjectId("_id");

            if (id != null
                    && id.toHexString().equals(cuentaId)) {
                return cuenta;
            }

            throw new SecurityException(
                    "No tenés permisos para consultar esta cuenta corriente."
            );
        }

        for (Document cuenta :
                cuentaService.listarCuentasParaUsuario(usuarioId)) {

            ObjectId id = cuenta.getObjectId("_id");

            if (id != null
                    && id.toHexString().equals(cuentaId)) {
                return cuenta;
            }
        }

        return null;
    }

    // =========================================
    // OBTENER CUENTA SELECCIONADA
    // =========================================

    private String obtenerCuentaSeleccionada() {

        int filaVista =
                view.getTablaCuentas().getSelectedRow();

        if (filaVista == -1) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná una cuenta corriente.",
                    "Cuentas corrientes",
                    JOptionPane.WARNING_MESSAGE
            );

            return null;
        }

        int filaModelo =
                view.getTablaCuentas()
                        .convertRowIndexToModel(filaVista);

        if (filaModelo < 0
                || filaModelo >= cuentasIds.size()) {
            return null;
        }

        return cuentasIds.get(filaModelo);
    }

    // =========================================
    // NOMBRE DEL CLIENTE
    // =========================================

    private String obtenerNombreCliente(ObjectId clienteId) {

        if (clienteId == null) {
            return "";
        }

        try {

            Document cliente = mongo.consultarClientePorId(
                    clienteId.toHexString()
            );

            if (cliente != null) {

                String nombre =
                        cliente.getString("razon_social");

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
    // NOMBRE DEL PROVEEDOR
    // =========================================

    private String obtenerNombreProveedor(ObjectId proveedorId) {

        if (proveedorId == null) {
            return "";
        }

        try {

            Document proveedor =
                    mongo.buscarProveedorPorId(
                            proveedorId.toHexString()
                    );

            if (proveedor != null) {

                String nombre =
                        proveedor.getString("razon_social");

                if (nombre != null && !nombre.isBlank()) {
                    return nombre;
                }
            }

        } catch (Exception ignored) {
            // Si falla la consulta, mostramos el ID.
        }

        return proveedorId.toHexString();
    }

    // =========================================
    // FORMATEAR VALORES
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
