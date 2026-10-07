package org.example.interfaz.principal.vistas;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VehiculosPanel extends JPanel {

    // =========================================================
    // TABLA
    // =========================================================

    private final JTable tablaVehiculos;

    private final JButton btnActualizar;

    private final JButton btnMostrarFormulario;

    private final JButton btnEditar;

    private final JButton btnEliminar;


    // =========================================================
    // FORMULARIO
    // =========================================================

    private final JTextField txtTipo;

    private final JTextField txtIdentificacion;

    private final JTextField txtEmpresaOperadora;

    private final JComboBox<String> comboEstado;

    private final JButton btnGuardar;

    private final JButton btnCancelar;

    private JPanel panelFormulario;

    private JLabel lblTituloFormulario;


    // =========================================================
    // ESTADO
    // =========================================================

    private final JLabel lblEstado;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public VehiculosPanel() {

        tablaVehiculos =
                new JTable();

        btnActualizar =
                new JButton(
                        "Actualizar"
                );

        btnMostrarFormulario =
                new JButton(
                        "+ Agregar vehículo"
                );

        btnEditar =
                new JButton(
                        "Editar"
                );

        btnEliminar =
                new JButton(
                        "Eliminar"
                );

        txtTipo =
                new JTextField();

        txtIdentificacion =
                new JTextField();

        txtEmpresaOperadora =
                new JTextField();

        comboEstado =
                new JComboBox<>(
                        new String[]{
                                "ACTIVO",
                                "INACTIVO"
                        }
                );

        btnGuardar =
                new JButton(
                        "Guardar vehículo"
                );

        btnCancelar =
                new JButton(
                        "Cancelar"
                );

        lblEstado =
                new JLabel(
                        " "
                );

        construir();
    }


    // =========================================================
    // CONSTRUIR
    // =========================================================

    private void construir() {

        setLayout(
                new BorderLayout(
                        20,
                        20
                )
        );

        setBackground(
                Colores.FONDO
        );

        setBorder(
                new EmptyBorder(
                        25,
                        30,
                        25,
                        30
                )
        );


        // =====================================================
        // TÍTULO
        // =====================================================

        JLabel titulo =
                new JLabel(
                        "Vehículos"
                );

        titulo.setFont(
                Fuentes.TITULO
        );

        titulo.setForeground(
                Colores.TEXTO
        );

        add(
                titulo,
                BorderLayout.NORTH
        );


        // =====================================================
        // CONTENIDO
        // =====================================================

        JPanel contenido =
                new JPanel(
                        new BorderLayout(
                                20,
                                20
                        )
                );

        contenido.setOpaque(
                false
        );

        contenido.add(
                crearPanelListado(),
                BorderLayout.CENTER
        );


        // =====================================================
        // PARTE INFERIOR
        // =====================================================

        JPanel inferior =
                new JPanel();

        inferior.setLayout(
                new BoxLayout(
                        inferior,
                        BoxLayout.Y_AXIS
                )
        );

        inferior.setOpaque(
                false
        );


        // -----------------------------------------------------
        // BOTONES PRINCIPALES
        // -----------------------------------------------------

        JPanel panelBotonesPrincipales =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        panelBotonesPrincipales.setOpaque(
                false
        );

        btnEditar.setFont(
                Fuentes.BOTON
        );

        btnEditar.setFocusPainted(
                false
        );

        btnEliminar.setFont(
                Fuentes.BOTON
        );

        btnEliminar.setFocusPainted(
                false
        );

        btnMostrarFormulario.setFont(
                Fuentes.BOTON
        );

        btnMostrarFormulario.setFocusPainted(
                false
        );

        panelBotonesPrincipales.add(
                btnEditar
        );

        panelBotonesPrincipales.add(
                btnEliminar
        );

        panelBotonesPrincipales.add(
                btnMostrarFormulario
        );

        inferior.add(
                panelBotonesPrincipales
        );


        // -----------------------------------------------------
        // FORMULARIO OCULTO
        // -----------------------------------------------------

        panelFormulario =
                crearPanelFormulario();

        panelFormulario.setVisible(
                false
        );

        inferior.add(
                panelFormulario
        );

        contenido.add(
                inferior,
                BorderLayout.SOUTH
        );

        add(
                contenido,
                BorderLayout.CENTER
        );


        // =====================================================
        // ESTADO GENERAL
        // =====================================================

        lblEstado.setFont(
                Fuentes.NORMAL
        );

        lblEstado.setForeground(
                Colores.TEXTO_SECUNDARIO
        );

        add(
                lblEstado,
                BorderLayout.SOUTH
        );
    }


    // =========================================================
    // LISTADO
    // =========================================================

    private JPanel crearPanelListado() {

        JPanel panel =
                crearPanelBase();

        panel.setLayout(
                new BorderLayout(
                        10,
                        10
                )
        );


        // -----------------------------------------------------
        // CABECERA
        // -----------------------------------------------------

        JPanel cabecera =
                new JPanel(
                        new BorderLayout()
                );

        cabecera.setOpaque(
                false
        );

        JLabel titulo =
                new JLabel(
                        "Vehículos registrados"
                );

        titulo.setFont(
                Fuentes.LABEL
        );

        titulo.setForeground(
                Colores.TEXTO
        );

        btnActualizar.setFont(
                Fuentes.BOTON
        );

        btnActualizar.setFocusPainted(
                false
        );

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


        // -----------------------------------------------------
        // TABLA
        // -----------------------------------------------------

        DefaultTableModel modelo =
                new DefaultTableModel(
                        new Object[][]{},
                        new String[]{
                                "Tipo",
                                "Identificación",
                                "Empresa operadora",
                                "Estado"
                        }
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {

                        return false;
                    }
                };

        tablaVehiculos.setModel(
                modelo
        );

        tablaVehiculos.setRowHeight(
                30
        );

        tablaVehiculos.setFont(
                Fuentes.NORMAL
        );

        tablaVehiculos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaVehiculos
                .getTableHeader()
                .setReorderingAllowed(
                        false
                );

        JScrollPane scroll =
                new JScrollPane(
                        tablaVehiculos
                );

        panel.add(
                scroll,
                BorderLayout.CENTER
        );

        return panel;
    }


    // =========================================================
    // FORMULARIO
    // =========================================================

    private JPanel crearPanelFormulario() {

        JPanel panel =
                crearPanelBase();

        panel.setLayout(
                new BorderLayout(
                        10,
                        10
                )
        );

        lblTituloFormulario =
                new JLabel(
                        "Nuevo vehículo"
                );

        lblTituloFormulario.setFont(
                Fuentes.LABEL
        );

        lblTituloFormulario.setForeground(
                Colores.TEXTO
        );

        panel.add(
                lblTituloFormulario,
                BorderLayout.NORTH
        );


        // =====================================================
        // CAMPOS
        // =====================================================

        JPanel formulario =
                new JPanel(
                        new GridBagLayout()
                );

        formulario.setOpaque(
                false
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

        agregarCampo(
                formulario,
                gbc,
                0,
                "Tipo:",
                txtTipo
        );

        agregarCampo(
                formulario,
                gbc,
                1,
                "Identificación:",
                txtIdentificacion
        );

        agregarCampo(
                formulario,
                gbc,
                2,
                "Empresa operadora:",
                txtEmpresaOperadora
        );

        agregarCampo(
                formulario,
                gbc,
                3,
                "Estado:",
                comboEstado
        );

        panel.add(
                formulario,
                BorderLayout.CENTER
        );


        // =====================================================
        // BOTONES
        // =====================================================

        JPanel botones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        botones.setOpaque(
                false
        );

        btnCancelar.setFont(
                Fuentes.BOTON
        );

        btnCancelar.setFocusPainted(
                false
        );

        btnGuardar.setFont(
                Fuentes.BOTON
        );

        btnGuardar.setFocusPainted(
                false
        );

        botones.add(
                btnCancelar
        );

        botones.add(
                btnGuardar
        );

        panel.add(
                botones,
                BorderLayout.SOUTH
        );

        return panel;
    }


    // =========================================================
    // AGREGAR CAMPO
    // =========================================================

    private void agregarCampo(
            JPanel panel,
            GridBagConstraints gbc,
            int fila,
            String texto,
            JComponent campo
    ) {

        gbc.gridx =
                0;

        gbc.gridy =
                fila;

        gbc.weightx =
                0;

        JLabel label =
                new JLabel(
                        texto
                );

        label.setFont(
                Fuentes.NORMAL
        );

        label.setForeground(
                Colores.TEXTO
        );

        panel.add(
                label,
                gbc
        );

        gbc.gridx =
                1;

        gbc.weightx =
                1;

        campo.setPreferredSize(
                new Dimension(
                        350,
                        30
                )
        );

        panel.add(
                campo,
                gbc
        );
    }


    // =========================================================
    // PANEL BASE
    // =========================================================

    private JPanel crearPanelBase() {

        JPanel panel =
                new JPanel();

        panel.setBackground(
                Colores.SUPERFICIE
        );

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Colores.BORDE
                        ),
                        new EmptyBorder(
                                15,
                                15,
                                15,
                                15
                        )
                )
        );

        return panel;
    }


    // =========================================================
    // MOSTRAR FORMULARIO
    // =========================================================

    public void mostrarFormulario() {

        panelFormulario.setVisible(
                true
        );

        btnMostrarFormulario.setVisible(
                false
        );

        revalidate();

        repaint();
    }


    // =========================================================
    // OCULTAR FORMULARIO
    // =========================================================

    public void ocultarFormulario() {

        panelFormulario.setVisible(
                false
        );

        btnMostrarFormulario.setVisible(
                true
        );

        revalidate();

        repaint();
    }


    // =========================================================
    // LIMPIAR FORMULARIO
    // =========================================================

    public void limpiarFormulario() {

        txtTipo.setText(
                ""
        );

        txtIdentificacion.setText(
                ""
        );

        txtEmpresaOperadora.setText(
                ""
        );

        comboEstado.setSelectedItem(
                "ACTIVO"
        );
    }


    // =========================================================
    // MODO AGREGAR
    // =========================================================

    public void modoAgregar() {

        lblTituloFormulario.setText(
                "Nuevo vehículo"
        );

        btnGuardar.setText(
                "Guardar vehículo"
        );

        comboEstado.setSelectedItem(
                "ACTIVO"
        );

        comboEstado.setEnabled(
                false
        );
    }


    // =========================================================
    // MODO EDITAR
    // =========================================================

    public void modoEditar() {

        lblTituloFormulario.setText(
                "Editar vehículo"
        );

        btnGuardar.setText(
                "Guardar cambios"
        );

        comboEstado.setEnabled(
                true
        );
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public JTable getTablaVehiculos() {
        return tablaVehiculos;
    }

    public JButton getBtnActualizar() {
        return btnActualizar;
    }

    public JButton getBtnMostrarFormulario() {
        return btnMostrarFormulario;
    }

    public JButton getBtnEditar() {
        return btnEditar;
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public JTextField getTxtTipo() {
        return txtTipo;
    }

    public JTextField getTxtIdentificacion() {
        return txtIdentificacion;
    }

    public JTextField getTxtEmpresaOperadora() {
        return txtEmpresaOperadora;
    }

    public JComboBox<String> getComboEstado() {
        return comboEstado;
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public JButton getBtnCancelar() {
        return btnCancelar;
    }

    public JLabel getLblEstado() {
        return lblEstado;
    }
}