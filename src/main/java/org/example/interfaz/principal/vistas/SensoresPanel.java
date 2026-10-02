package org.example.interfaz.principal.vistas;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SensoresPanel extends JPanel {

    // =========================================================
    // TABLA
    // =========================================================

    private final JTable tablaSensores;

    private final JButton btnActualizar;

    private final JButton btnMostrarFormulario;


    // =========================================================
    // FORMULARIO
    // =========================================================

    private final JComboBox<String> cmbContenedor;

    private final JTextField txtTipo;

    private final JTextField txtFabricante;

    private final JTextField txtFechaInstalacion;

    private final JButton btnGuardar;

    private final JButton btnCancelar;

    private JPanel panelFormulario;


    // =========================================================
    // ESTADO
    // =========================================================

    private final JLabel lblEstado;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SensoresPanel() {

        tablaSensores =
                new JTable();

        btnActualizar =
                new JButton("Actualizar");

        btnMostrarFormulario =
                new JButton("+ Agregar sensor");


        cmbContenedor =
                new JComboBox<>();

        txtTipo =
                new JTextField();

        txtFabricante =
                new JTextField();

        txtFechaInstalacion =
                new JTextField();


        btnGuardar =
                new JButton("Guardar sensor");

        btnCancelar =
                new JButton("Cancelar");


        lblEstado =
                new JLabel(" ");


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
                        "Sensores"
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
        // BOTÓN AGREGAR
        // -----------------------------------------------------

        JPanel panelBotonAgregar =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        panelBotonAgregar.setOpaque(
                false
        );


        btnMostrarFormulario.setFont(
                Fuentes.BOTON
        );

        btnMostrarFormulario.setFocusPainted(
                false
        );


        panelBotonAgregar.add(
                btnMostrarFormulario
        );


        inferior.add(
                panelBotonAgregar
        );


        // -----------------------------------------------------
        // FORMULARIO
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
        // ESTADO
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
    // PANEL LISTADO
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


        // =====================================================
        // CABECERA
        // =====================================================

        JPanel cabecera =
                new JPanel(
                        new BorderLayout()
                );

        cabecera.setOpaque(
                false
        );


        JLabel titulo =
                new JLabel(
                        "Sensores registrados"
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


        // =====================================================
        // TABLA
        // =====================================================

        DefaultTableModel modelo =
                new DefaultTableModel(
                        new Object[][]{},
                        new String[]{
                                "Contenedor",
                                "Tipo",
                                "Fabricante",
                                "Fecha instalación",
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


        tablaSensores.setModel(
                modelo
        );

        tablaSensores.setRowHeight(
                30
        );

        tablaSensores.setFont(
                Fuentes.NORMAL
        );

        tablaSensores.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaSensores
                .getTableHeader()
                .setReorderingAllowed(false);


        JScrollPane scroll =
                new JScrollPane(
                        tablaSensores
                );


        panel.add(
                scroll,
                BorderLayout.CENTER
        );


        return panel;
    }


    // =========================================================
    // PANEL FORMULARIO
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


        JLabel titulo =
                new JLabel(
                        "Nuevo sensor"
                );

        titulo.setFont(
                Fuentes.LABEL
        );

        titulo.setForeground(
                Colores.TEXTO
        );


        panel.add(
                titulo,
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
                "Contenedor:",
                cmbContenedor
        );


        agregarCampo(
                formulario,
                gbc,
                1,
                "Tipo:",
                txtTipo
        );


        agregarCampo(
                formulario,
                gbc,
                2,
                "Fabricante:",
                txtFabricante
        );


        agregarCampo(
                formulario,
                gbc,
                3,
                "Fecha instalación:",
                txtFechaInstalacion
        );


        JLabel ayudaFecha =
                new JLabel(
                        "Formato: dd/MM/yyyy"
                );

        ayudaFecha.setFont(
                Fuentes.NORMAL
        );

        ayudaFecha.setForeground(
                Colores.TEXTO_SECUNDARIO
        );


        gbc.gridx =
                1;

        gbc.gridy =
                4;

        gbc.weightx =
                1;


        formulario.add(
                ayudaFecha,
                gbc
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

        if (
                cmbContenedor.getItemCount() > 0
        ) {

            cmbContenedor.setSelectedIndex(
                    0
            );
        }


        txtTipo.setText("");

        txtFabricante.setText("");

        txtFechaInstalacion.setText("");
    }


    // =========================================================
    // GETTERS TABLA
    // =========================================================

    public JTable getTablaSensores() {

        return tablaSensores;
    }


    public JButton getBtnActualizar() {

        return btnActualizar;
    }


    public JButton getBtnMostrarFormulario() {

        return btnMostrarFormulario;
    }


    // =========================================================
    // GETTERS FORMULARIO
    // =========================================================

    public JComboBox<String> getCmbContenedor() {

        return cmbContenedor;
    }


    public JTextField getTxtTipo() {

        return txtTipo;
    }


    public JTextField getTxtFabricante() {

        return txtFabricante;
    }


    public JTextField getTxtFechaInstalacion() {

        return txtFechaInstalacion;
    }


    public JButton getBtnGuardar() {

        return btnGuardar;
    }


    public JButton getBtnCancelar() {

        return btnCancelar;
    }


    // =========================================================
    // ESTADO
    // =========================================================

    public JLabel getLblEstado() {

        return lblEstado;
    }
}