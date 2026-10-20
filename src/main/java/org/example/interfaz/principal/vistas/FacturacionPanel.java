
package org.example.interfaz.principal.vistas;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FacturacionPanel extends JPanel {

    private JTable tablaFacturas;

    private JButton btnActualizar;
    private JButton btnMostrarFormulario;
    private JButton btnModificarFactura;
    private JButton btnPagarFactura;
    private JButton btnGuardar;
    private JButton btnCancelar;

    private JComboBox<String> cmbCliente;
    private JTextField txtFechaEmision;
    private JTextField txtFechaVencimiento;
    private JTextField txtImporteTotal;

    // Se conserva para compatibilidad con el
    // controlador actual hasta actualizarlo.
    private JComboBox<String> cmbEstado;

    private JPanel panelFormulario;
    private JLabel lblEstado;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public FacturacionPanel() {
        construir();
    }

    // ==========================================
    // CONSTRUIR PANTALLA
    // ==========================================

    private void construir() {

        setLayout(new BorderLayout(0, 20));
        setBackground(Colores.FONDO);

        setBorder(
                BorderFactory.createEmptyBorder(
                        25, 30, 20, 30
                )
        );

        JLabel titulo = new JLabel("Facturación");
        titulo.setFont(Fuentes.TITULO);
        titulo.setForeground(Colores.TEXTO);

        add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel();

        centro.setLayout(
                new BoxLayout(centro, BoxLayout.Y_AXIS)
        );

        centro.setOpaque(false);

        centro.add(construirPanelTabla());
        centro.add(Box.createVerticalStrut(20));

        panelFormulario = construirFormulario();
        panelFormulario.setVisible(false);

        centro.add(panelFormulario);

        add(centro, BorderLayout.CENTER);

        lblEstado = new JLabel(
                "Facturas registradas: 0"
        );

        lblEstado.setFont(Fuentes.NORMAL);
        lblEstado.setForeground(
                Colores.TEXTO_SECUNDARIO
        );

        add(lblEstado, BorderLayout.SOUTH);
    }

    // ==========================================
    // TABLA DE FACTURAS
    // ==========================================

    private JPanel construirPanelTabla() {

        JPanel panel = new JPanel(
                new BorderLayout(0, 10)
        );

        panel.setBackground(Colores.SUPERFICIE);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Colores.BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                15, 15, 15, 15
                        )
                )
        );

        // ======================================
        // CABECERA
        // ======================================

        JPanel cabecera = new JPanel(
                new BorderLayout()
        );

        cabecera.setOpaque(false);

        JLabel titulo = new JLabel(
                "Facturas registradas"
        );

        titulo.setFont(Fuentes.LABEL);

        btnActualizar = new JButton("Actualizar");

        cabecera.add(
                titulo,
                BorderLayout.WEST
        );

        cabecera.add(
                btnActualizar,
                BorderLayout.EAST
        );

        panel.add(
                cabecera,
                BorderLayout.NORTH
        );

        // ======================================
        // MODELO DE TABLA
        // ======================================

        DefaultTableModel modelo =
                new DefaultTableModel(
                        new Object[]{
                                "Cliente",
                                "Fecha de emisión",
                                "Fecha de vencimiento",
                                "Importe total",
                                "Estado"
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

        tablaFacturas = new JTable(modelo);
        tablaFacturas.setRowHeight(30);
        tablaFacturas.setFont(Fuentes.NORMAL);

        tablaFacturas.getTableHeader()
                .setFont(Fuentes.LABEL);

        tablaFacturas.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scroll = new JScrollPane(
                tablaFacturas
        );

        scroll.setPreferredSize(
                new Dimension(800, 350)
        );

        panel.add(
                scroll,
                BorderLayout.CENTER
        );

        // ======================================
        // BOTONES DE ACCIÓN
        // ======================================

        btnMostrarFormulario = new JButton(
                "+ Agregar factura"
        );

        btnModificarFactura = new JButton(
                "Modificar factura"
        );

        btnPagarFactura = new JButton(
                "Pagar factura"
        );

        btnModificarFactura.setEnabled(false);
        btnPagarFactura.setEnabled(false);

        JPanel acciones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        acciones.setOpaque(false);

        acciones.add(btnModificarFactura);
        acciones.add(btnPagarFactura);
        acciones.add(btnMostrarFormulario);

        panel.add(
                acciones,
                BorderLayout.SOUTH
        );

        return panel;
    }

    // ==========================================
    // FORMULARIO NUEVA FACTURA
    // ==========================================

    private JPanel construirFormulario() {

        JPanel panel = new JPanel(
                new GridBagLayout()
        );

        panel.setBackground(Colores.SUPERFICIE);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Colores.BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                15, 15, 15, 15
                        )
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ======================================
        // TÍTULO
        // ======================================

        JLabel titulo = new JLabel(
                "Nueva factura"
        );

        titulo.setFont(Fuentes.LABEL);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;

        panel.add(titulo, gbc);

        gbc.gridwidth = 1;

        // ======================================
        // CLIENTE
        // ======================================

        agregarLabel(
                panel,
                gbc,
                "Cliente:",
                1
        );

        cmbCliente = new JComboBox<>();

        agregarCampo(
                panel,
                gbc,
                cmbCliente,
                1
        );

        // ======================================
        // FECHA DE EMISIÓN
        // ======================================

        agregarLabel(
                panel,
                gbc,
                "Fecha de emisión:",
                2
        );

        txtFechaEmision = new JTextField();

        agregarCampo(
                panel,
                gbc,
                txtFechaEmision,
                2
        );

        JLabel ayudaFecha = new JLabel(
                "Formato: dd/MM/yyyy"
        );

        ayudaFecha.setFont(Fuentes.NORMAL);

        ayudaFecha.setForeground(
                Colores.TEXTO_SECUNDARIO
        );

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1;

        panel.add(
                ayudaFecha,
                gbc
        );

        // ======================================
        // FECHA DE VENCIMIENTO
        // ======================================

        agregarLabel(
                panel,
                gbc,
                "Fecha de vencimiento:",
                4
        );

        txtFechaVencimiento = new JTextField();

        agregarCampo(
                panel,
                gbc,
                txtFechaVencimiento,
                4
        );

        // ======================================
        // IMPORTE TOTAL
        // ======================================

        agregarLabel(
                panel,
                gbc,
                "Importe total:",
                5
        );

        txtImporteTotal = new JTextField();

        agregarCampo(
                panel,
                gbc,
                txtImporteTotal,
                5
        );

        // ======================================
        // ESTADO AUTOMÁTICO
        // ======================================

        // El estado ya no se selecciona desde
        // el formulario. Una factura nueva
        // comienza como PENDIENTE.
        //
        // Conservamos este componente sin
        // mostrarlo para que el controlador
        // anterior siga siendo compatible.

        cmbEstado = new JComboBox<>(
                new String[]{"PENDIENTE"}
        );

        cmbEstado.setSelectedItem("PENDIENTE");

        // ======================================
        // BOTONES DEL FORMULARIO
        // ======================================

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        botones.setOpaque(false);

        btnCancelar = new JButton(
                "Cancelar"
        );

        btnGuardar = new JButton(
                "Guardar factura"
        );

        botones.add(btnCancelar);
        botones.add(btnGuardar);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weightx = 1;

        panel.add(
                botones,
                gbc
        );

        return panel;
    }

    // ==========================================
    // AGREGAR LABEL
    // ==========================================

    private void agregarLabel(
            JPanel panel,
            GridBagConstraints gbc,
            String texto,
            int fila
    ) {

        JLabel label = new JLabel(texto);
        label.setFont(Fuentes.NORMAL);

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0;

        panel.add(label, gbc);
    }

    // ==========================================
    // AGREGAR CAMPO
    // ==========================================

    private void agregarCampo(
            JPanel panel,
            GridBagConstraints gbc,
            JComponent componente,
            int fila
    ) {

        gbc.gridx = 1;
        gbc.gridy = fila;
        gbc.weightx = 1;

        panel.add(componente, gbc);
    }

    // ==========================================
    // MOSTRAR FORMULARIO
    // ==========================================

    public void mostrarFormulario() {

        panelFormulario.setVisible(true);

        revalidate();
        repaint();
    }

    // ==========================================
    // OCULTAR FORMULARIO
    // ==========================================

    public void ocultarFormulario() {

        panelFormulario.setVisible(false);

        revalidate();
        repaint();
    }

    // ==========================================
    // LIMPIAR FORMULARIO
    // ==========================================

    public void limpiarFormulario() {

        txtFechaEmision.setText("");
        txtFechaVencimiento.setText("");
        txtImporteTotal.setText("");

        if (cmbCliente.getItemCount() > 0) {
            cmbCliente.setSelectedIndex(0);
        }

        if (cmbEstado.getItemCount() > 0) {
            cmbEstado.setSelectedIndex(0);
        }
    }

    // ==========================================
    // GETTERS
    // ==========================================

    public JTable getTablaFacturas() {
        return tablaFacturas;
    }

    public JButton getBtnActualizar() {
        return btnActualizar;
    }

    public JButton getBtnMostrarFormulario() {
        return btnMostrarFormulario;
    }

    public JButton getBtnModificarFactura() {
        return btnModificarFactura;
    }

    public JButton getBtnPagarFactura() {
        return btnPagarFactura;
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public JButton getBtnCancelar() {
        return btnCancelar;
    }

    public JComboBox<String> getCmbCliente() {
        return cmbCliente;
    }

    public JTextField getTxtFechaEmision() {
        return txtFechaEmision;
    }

    public JTextField getTxtFechaVencimiento() {
        return txtFechaVencimiento;
    }

    public JTextField getTxtImporteTotal() {
        return txtImporteTotal;
    }

    public JComboBox<String> getCmbEstado() {
        return cmbEstado;
    }

    public JLabel getLblEstado() {
        return lblEstado;
    }
}
