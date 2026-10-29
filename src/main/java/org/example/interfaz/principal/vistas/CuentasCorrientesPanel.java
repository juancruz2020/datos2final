
package org.example.interfaz.principal.vistas;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CuentasCorrientesPanel extends JPanel {

    private JTable tablaCuentas;

    private JButton btnActualizar;
    private JButton btnRegistrarDebito;
    private JButton btnRegistrarCredito;
    private JButton btnVerMovimientos;
    private JButton btnVerCierres;
    private JButton btnGenerarCierre;

    private JComboBox<String> cmbTipoCuenta;

    private JLabel lblEstado;

    // =========================================
    // CONSTRUCTOR
    // =========================================

    public CuentasCorrientesPanel() {
        construir();
    }

    // =========================================
    // CONSTRUIR
    // =========================================

    private void construir() {

        setLayout(new BorderLayout(0, 20));

        setBackground(Colores.FONDO);

        setBorder(
                BorderFactory.createEmptyBorder(
                        25, 30, 20, 30
                )
        );

        // =========================================
        // TÍTULO
        // =========================================

        JLabel titulo = new JLabel("Cuentas corrientes");

        titulo.setFont(Fuentes.TITULO);
        titulo.setForeground(Colores.TEXTO);

        add(titulo, BorderLayout.NORTH);

        // =========================================
        // SELECTOR CLIENTES / PROVEEDORES
        // =========================================

        JPanel contenido = new JPanel(
                new BorderLayout(0, 15)
        );

        contenido.setOpaque(false);

        JPanel selector = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        selector.setOpaque(false);

        JLabel lblTipo = new JLabel(
                "Mostrar cuentas de:"
        );

        lblTipo.setFont(Fuentes.NORMAL);
        lblTipo.setForeground(Colores.TEXTO);

        cmbTipoCuenta = new JComboBox<>(
                new String[]{
                        "Clientes",
                        "Proveedores"
                }
        );

        cmbTipoCuenta.setFont(Fuentes.NORMAL);

        cmbTipoCuenta.setPreferredSize(
                new Dimension(180, 30)
        );

        selector.add(lblTipo);
        selector.add(cmbTipoCuenta);

        contenido.add(
                selector,
                BorderLayout.NORTH
        );

        // Una sola tabla para clientes y proveedores.

        contenido.add(
                construirPanelTabla(),
                BorderLayout.CENTER
        );

        add(
                contenido,
                BorderLayout.CENTER
        );

        // =========================================
        // ESTADO
        // =========================================

        lblEstado = new JLabel(
                "Cuentas registradas: 0"
        );

        lblEstado.setFont(Fuentes.NORMAL);

        lblEstado.setForeground(
                Colores.TEXTO_SECUNDARIO
        );

        add(
                lblEstado,
                BorderLayout.SOUTH
        );
    }

    // =========================================
    // CONSTRUIR PANEL TABLA
    // =========================================

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

        // =========================================
        // CABECERA
        // =========================================

        JPanel cabecera = new JPanel(
                new BorderLayout()
        );

        cabecera.setOpaque(false);

        JLabel titulo = new JLabel(
                "Cuentas registradas"
        );

        titulo.setFont(Fuentes.LABEL);
        titulo.setForeground(Colores.TEXTO);

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

        // =========================================
        // MODELO DE TABLA
        // =========================================

        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{
                        "Cliente",
                        "Saldo actual",
                        "Última actualización"
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

        // =========================================
        // TABLA DE CUENTAS
        // =========================================

        tablaCuentas = new JTable(modelo);

        tablaCuentas.setRowHeight(30);
        tablaCuentas.setFont(Fuentes.NORMAL);

        tablaCuentas.getTableHeader().setFont(
                Fuentes.LABEL
        );

        tablaCuentas.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scroll = new JScrollPane(
                tablaCuentas
        );

        scroll.setPreferredSize(
                new Dimension(800, 350)
        );

        panel.add(
                scroll,
                BorderLayout.CENTER
        );

        // =========================================
        // BOTONES
        // =========================================

        JPanel acciones = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        10,
                        5
                )
        );

        acciones.setOpaque(false);

        btnRegistrarDebito = new JButton(
                "+ Registrar débito"
        );

        btnRegistrarCredito = new JButton(
                "+ Registrar crédito"
        );

        btnVerMovimientos = new JButton(
                "Ver movimientos"
        );

        btnVerCierres = new JButton(
                "Ver cierres mensuales"
        );

        btnGenerarCierre = new JButton(
                "Generar cierre mensual"
        );

        acciones.add(btnRegistrarDebito);
        acciones.add(btnRegistrarCredito);
        acciones.add(btnVerMovimientos);
        acciones.add(btnVerCierres);
        acciones.add(btnGenerarCierre);

        panel.add(
                acciones,
                BorderLayout.SOUTH
        );

        return panel;
    }

    // =========================================
    // TIPO DE CUENTA SELECCIONADO
    // =========================================

    public boolean mostrandoProveedores() {
        return cmbTipoCuenta.getSelectedIndex() == 1;
    }

    public JComboBox<String> getCmbTipoCuenta() {
        return cmbTipoCuenta;
    }

    // =========================================
    // GETTERS ORIGINALES
    // =========================================

    public JTable getTablaCuentas() {
        return tablaCuentas;
    }

    public JButton getBtnActualizar() {
        return btnActualizar;
    }

    public JButton getBtnRegistrarDebito() {
        return btnRegistrarDebito;
    }

    public JButton getBtnRegistrarCredito() {
        return btnRegistrarCredito;
    }

    public JButton getBtnVerMovimientos() {
        return btnVerMovimientos;
    }

    public JButton getBtnVerCierres() {
        return btnVerCierres;
    }

    public JButton getBtnGenerarCierre() {
        return btnGenerarCierre;
    }

    public JLabel getLblEstado() {
        return lblEstado;
    }
}
