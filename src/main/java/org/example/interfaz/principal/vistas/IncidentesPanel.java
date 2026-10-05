package org.example.interfaz.principal.vistas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class IncidentesPanel extends JPanel {

    private final JComboBox<String> comboEnvio;
    private final JTextField txtTipo;
    private final JTextField txtSeveridad;
    private final JComboBox<String> comboEstado;
    private final JTextArea txtDescripcion;

    private final JButton btnRegistrar;
    private final JButton btnEditar;
    private final JButton btnEliminar;
    private final JButton btnActualizar;

    private final JTable tablaIncidentes;
    private final DefaultTableModel modeloTabla;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public IncidentesPanel() {

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
                        "Incidentes"
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
                        "Registrar / modificar incidente"
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
        // ENVÍO
        // -----------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        formulario.add(
                new JLabel("Envío:"),
                gbc
        );


        comboEnvio =
                new JComboBox<>();

        gbc.gridx = 1;
        gbc.weightx = 1;

        formulario.add(
                comboEnvio,
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
        // SEVERIDAD
        // -----------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;

        formulario.add(
                new JLabel("Severidad:"),
                gbc
        );


        txtSeveridad =
                new JTextField();

        gbc.gridx = 1;
        gbc.weightx = 1;

        formulario.add(
                txtSeveridad,
                gbc
        );


        // -----------------------------------------------------
        // ESTADO
        // -----------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;

        formulario.add(
                new JLabel("Estado:"),
                gbc
        );


        comboEstado =
                new JComboBox<>(
                        new String[]{
                                "ABIERTO",
                                "CERRADO"
                        }
                );

        comboEstado.setSelectedItem(
                "ABIERTO"
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        formulario.add(
                comboEstado,
                gbc
        );


        // -----------------------------------------------------
        // DESCRIPCIÓN
        // -----------------------------------------------------

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;

        gbc.anchor =
                GridBagConstraints.NORTH;

        formulario.add(
                new JLabel("Descripción:"),
                gbc
        );


        txtDescripcion =
                new JTextArea(
                        3,
                        20
                );

        txtDescripcion.setLineWrap(
                true
        );

        txtDescripcion.setWrapStyleWord(
                true
        );


        JScrollPane scrollDescripcion =
                new JScrollPane(
                        txtDescripcion
                );


        gbc.gridx = 1;
        gbc.weightx = 1;

        formulario.add(
                scrollDescripcion,
                gbc
        );


        // -----------------------------------------------------
        // REGISTRAR / GUARDAR
        // -----------------------------------------------------

        btnRegistrar =
                new JButton(
                        "Registrar incidente"
                );


        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 0;

        formulario.add(
                btnRegistrar,
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
                                "Envío",
                                "Fecha",
                                "Tipo",
                                "Severidad",
                                "Estado",
                                "Descripción"
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


        tablaIncidentes =
                new JTable(
                        modeloTabla
                );


        tablaIncidentes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );


        JScrollPane scrollTabla =
                new JScrollPane(
                        tablaIncidentes
                );


        panelCentral.add(
                scrollTabla,
                BorderLayout.CENTER
        );


        // =====================================================
        // BOTONES
        // =====================================================

        JPanel panelBotones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );


        btnEditar =
                new JButton(
                        "Editar"
                );


        btnEliminar =
                new JButton(
                        "Eliminar"
                );


        btnActualizar =
                new JButton(
                        "Actualizar"
                );


        panelBotones.add(
                btnEditar
        );

        panelBotones.add(
                btnEliminar
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

    public JComboBox<String> getComboEnvio() {

        return comboEnvio;
    }


    public JTextField getTxtTipo() {

        return txtTipo;
    }


    public JTextField getTxtSeveridad() {

        return txtSeveridad;
    }


    public JComboBox<String> getComboEstado() {

        return comboEstado;
    }


    public JTextArea getTxtDescripcion() {

        return txtDescripcion;
    }


    public JButton getBtnRegistrar() {

        return btnRegistrar;
    }


    public JButton getBtnEditar() {

        return btnEditar;
    }


    public JButton getBtnEliminar() {

        return btnEliminar;
    }


    public JButton getBtnActualizar() {

        return btnActualizar;
    }


    public JTable getTablaIncidentes() {

        return tablaIncidentes;
    }


    public DefaultTableModel getModeloTabla() {

        return modeloTabla;
    }


    // =========================================================
    // LIMPIAR FORMULARIO
    // =========================================================

    public void limpiarFormulario() {

        txtTipo.setText("");

        txtSeveridad.setText("");

        comboEstado.setSelectedItem(
                "ABIERTO"
        );

        txtDescripcion.setText("");


        if (comboEnvio.getItemCount() > 0) {

            comboEnvio.setSelectedIndex(
                    0
            );
        }
    }


    // =========================================================
    // MODO REGISTRAR
    // =========================================================

    public void modoRegistrar() {

        btnRegistrar.setText(
                "Registrar incidente"
        );

        comboEstado.setSelectedItem(
                "ABIERTO"
        );

        comboEstado.setEnabled(
                false
        );
    }


    // =========================================================
    // MODO EDITAR
    // =========================================================

    public void modoEditar() {

        btnRegistrar.setText(
                "Guardar cambios"
        );

        comboEstado.setEnabled(
                true
        );
    }
}