package org.example.interfaz.principal.vistas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReportesPanel extends JPanel {

    private final JComboBox<String> comboUsuario;
    private final JTextField txtTipo;
    private final JComboBox<String> comboFormato;

    private final JButton btnGenerar;
    private final JButton btnActualizar;

    private final JTable tablaReportes;
    private final DefaultTableModel modeloTabla;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ReportesPanel() {

        setLayout(
                new BorderLayout(
                        15,
                        15
                )
        );

        setBorder(
                new EmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );


        // =====================================================
        // TÍTULO
        // =====================================================

        JLabel titulo =
                new JLabel(
                        "Reportes"
                );

        titulo.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        24
                )
        );

        add(
                titulo,
                BorderLayout.NORTH
        );


        // =====================================================
        // PANEL CENTRAL
        // =====================================================

        JPanel panelCentral =
                new JPanel(
                        new BorderLayout(
                                15,
                                15
                        )
                );


        // =====================================================
        // FORMULARIO
        // =====================================================

        JPanel formulario =
                new JPanel(
                        new GridBagLayout()
                );

        formulario.setBorder(
                BorderFactory.createTitledBorder(
                        "Generar reporte"
                )
        );


        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        5,
                        5,
                        5,
                        5
                );

        gbc.fill =
                GridBagConstraints.HORIZONTAL;


        // -----------------------------------------------------
        // USUARIO
        // -----------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        formulario.add(
                new JLabel("Usuario:"),
                gbc
        );


        comboUsuario =
                new JComboBox<>();

        gbc.gridx = 1;
        gbc.weightx = 1;

        formulario.add(
                comboUsuario,
                gbc
        );


        // -----------------------------------------------------
        // TIPO
        // -----------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;

        formulario.add(
                new JLabel("Tipo:"),
                gbc
        );


        txtTipo =
                new JTextField();

        gbc.gridx = 1;
        gbc.weightx = 1;

        formulario.add(
                txtTipo,
                gbc
        );


        // -----------------------------------------------------
        // FORMATO
        // -----------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;

        formulario.add(
                new JLabel("Formato:"),
                gbc
        );


        comboFormato =
                new JComboBox<>(
                        new String[]{
                                "PDF",
                                "CSV",
                                "XLSX"
                        }
                );

        gbc.gridx = 1;
        gbc.weightx = 1;

        formulario.add(
                comboFormato,
                gbc
        );


        // -----------------------------------------------------
        // GENERAR
        // -----------------------------------------------------

        btnGenerar =
                new JButton(
                        "Generar reporte"
                );

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0;

        formulario.add(
                btnGenerar,
                gbc
        );


        panelCentral.add(
                formulario,
                BorderLayout.NORTH
        );


        // =====================================================
        // TABLA
        // =====================================================

        modeloTabla =
                new DefaultTableModel(
                        new Object[]{
                                "ID",
                                "Usuario",
                                "Tipo",
                                "Fecha de generación",
                                "Formato",
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


        tablaReportes =
                new JTable(
                        modeloTabla
                );

        tablaReportes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );


        JScrollPane scrollTabla =
                new JScrollPane(
                        tablaReportes
                );


        panelCentral.add(
                scrollTabla,
                BorderLayout.CENTER
        );


        // =====================================================
        // ACTUALIZAR
        // =====================================================

        JPanel panelBotones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );


        btnActualizar =
                new JButton(
                        "Actualizar"
                );


        panelBotones.add(
                btnActualizar
        );


        panelCentral.add(
                panelBotones,
                BorderLayout.SOUTH
        );


        add(
                panelCentral,
                BorderLayout.CENTER
        );
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public JComboBox<String> getComboUsuario() {

        return comboUsuario;
    }


    public JTextField getTxtTipo() {

        return txtTipo;
    }


    public JComboBox<String> getComboFormato() {

        return comboFormato;
    }


    public JButton getBtnGenerar() {

        return btnGenerar;
    }


    public JButton getBtnActualizar() {

        return btnActualizar;
    }


    public JTable getTablaReportes() {

        return tablaReportes;
    }


    public DefaultTableModel getModeloTabla() {

        return modeloTabla;
    }


    // =========================================================
    // LIMPIAR FORMULARIO
    // =========================================================

    public void limpiarFormulario() {

        txtTipo.setText("");

        if (comboUsuario.getItemCount() > 0) {

            comboUsuario.setSelectedIndex(0);
        }

        if (comboFormato.getItemCount() > 0) {

            comboFormato.setSelectedIndex(0);
        }
    }
}