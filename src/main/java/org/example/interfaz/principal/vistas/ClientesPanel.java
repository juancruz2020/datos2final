package org.example.interfaz.principal.vistas;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClientesPanel extends JPanel {

    // =========================================================
    // TABLA
    // =========================================================

    private final JTable tablaClientes;
    private JTable tablaRankingEnvios;

    private final JButton btnActualizar;

    private final JButton btnMostrarFormulario;

    private final JButton btnEditar;

    private final JButton btnEliminar;


    // =========================================================
    // FORMULARIO
    // =========================================================

    private final JTextField txtRazonSocial;

    private final JTextField txtCuit;

    private final JTextField txtEmail;

    private final JTextField txtTelefono;

    private final JTextField txtCalle;

    private final JTextField txtNumero;

    private final JTextField txtCiudad;

    private final JTextField txtCodigoPostal;

    private final JTextField txtPais;

    private final JButton btnAgregar;

    private final JButton btnCancelar;

    private JPanel panelFormulario;


    // =========================================================
    // ESTADO
    // =========================================================

    private final JLabel lblEstado;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ClientesPanel() {

        tablaClientes =
                new JTable();


        btnActualizar =
                new JButton("Actualizar");


        btnMostrarFormulario =
                new JButton("+ Agregar cliente");


        btnEditar =
                new JButton("Editar");


        btnEliminar =
                new JButton("Eliminar");


        txtRazonSocial =
                new JTextField();


        txtCuit =
                new JTextField();


        txtEmail =
                new JTextField();


        txtTelefono =
                new JTextField();


        txtCalle =
                new JTextField();


        txtNumero =
                new JTextField();


        txtCiudad =
                new JTextField();


        txtCodigoPostal =
                new JTextField();


        txtPais =
                new JTextField();


        btnAgregar =
                new JButton("Guardar cliente");


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
                        "Clientes"
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


        contenido.setOpaque(false);


        // -----------------------------------------------------
        // LISTADO
        // -----------------------------------------------------

        contenido.add(
                crearPanelListado(),
                BorderLayout.CENTER
        );


        // -----------------------------------------------------
        // PARTE INFERIOR
        // -----------------------------------------------------

        JPanel inferior =
                new JPanel();


        inferior.setLayout(
                new BoxLayout(
                        inferior,
                        BoxLayout.Y_AXIS
                )
        );


        inferior.setOpaque(false);


        JPanel panelBotones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );


        panelBotones.setOpaque(false);


        // -----------------------------------------------------
        // BOTÓN EDITAR
        // -----------------------------------------------------

        btnEditar.setFont(
                Fuentes.BOTON
        );


        btnEditar.setFocusPainted(
                false
        );


        panelBotones.add(
                btnEditar
        );


        // -----------------------------------------------------
        // BOTÓN ELIMINAR
        // -----------------------------------------------------

        btnEliminar.setFont(
                Fuentes.BOTON
        );


        btnEliminar.setFocusPainted(
                false
        );


        panelBotones.add(
                btnEliminar
        );


        // -----------------------------------------------------
        // BOTÓN AGREGAR CLIENTE
        // -----------------------------------------------------

        btnMostrarFormulario.setFont(
                Fuentes.BOTON
        );


        btnMostrarFormulario.setFocusPainted(
                false
        );


        panelBotones.add(
                btnMostrarFormulario
        );


        inferior.add(
                panelBotones
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


        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Clientes", contenido);
        pestanas.addTab("Ranking de envíos", crearPanelRanking());
        add(pestanas, BorderLayout.CENTER);


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

    private JPanel crearPanelRanking() {
        JPanel panel = crearPanelBase();
        panel.setLayout(new BorderLayout(10, 10));
        JLabel titulo = new JLabel("Clientes con más envíos");
        titulo.setFont(Fuentes.LABEL);
        panel.add(titulo, BorderLayout.NORTH);
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"Posición", "Cliente", "Cantidad de envíos"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaRankingEnvios = new JTable(modelo);
        tablaRankingEnvios.setRowHeight(28);
        tablaRankingEnvios.setFont(Fuentes.NORMAL);
        tablaRankingEnvios.getTableHeader().setFont(Fuentes.LABEL);
        panel.add(new JScrollPane(tablaRankingEnvios), BorderLayout.CENTER);
        return panel;
    }


    // =========================================================
    // LISTADO DE CLIENTES
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


        cabecera.setOpaque(false);


        JLabel titulo =
                new JLabel(
                        "Clientes registrados"
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
                                "Razón social",
                                "CUIT",
                                "Email",
                                "Teléfono",
                                "Ciudad",
                                "País",
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


        tablaClientes.setModel(
                modelo
        );


        tablaClientes.setRowHeight(
                30
        );


        tablaClientes.setFont(
                Fuentes.NORMAL
        );


        tablaClientes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );


        tablaClientes
                .getTableHeader()
                .setReorderingAllowed(
                        false
                );


        JScrollPane scroll =
                new JScrollPane(
                        tablaClientes
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


        JLabel titulo =
                new JLabel(
                        "Datos del cliente"
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


        formulario.setOpaque(false);


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
        // COLUMNA IZQUIERDA
        // -----------------------------------------------------

        agregarCampo(
                formulario,
                gbc,
                0,
                0,
                "Razón social:",
                txtRazonSocial
        );


        agregarCampo(
                formulario,
                gbc,
                0,
                1,
                "CUIT:",
                txtCuit
        );


        agregarCampo(
                formulario,
                gbc,
                0,
                2,
                "Email:",
                txtEmail
        );


        agregarCampo(
                formulario,
                gbc,
                0,
                3,
                "Teléfono:",
                txtTelefono
        );


        agregarCampo(
                formulario,
                gbc,
                0,
                4,
                "País:",
                txtPais
        );


        // -----------------------------------------------------
        // COLUMNA DERECHA
        // -----------------------------------------------------

        agregarCampo(
                formulario,
                gbc,
                2,
                0,
                "Calle:",
                txtCalle
        );


        agregarCampo(
                formulario,
                gbc,
                2,
                1,
                "Número:",
                txtNumero
        );


        agregarCampo(
                formulario,
                gbc,
                2,
                2,
                "Ciudad:",
                txtCiudad
        );


        agregarCampo(
                formulario,
                gbc,
                2,
                3,
                "Código postal:",
                txtCodigoPostal
        );


        panel.add(
                formulario,
                BorderLayout.CENTER
        );


        // =====================================================
        // BOTONES DEL FORMULARIO
        // =====================================================

        JPanel botones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );


        botones.setOpaque(false);


        btnCancelar.setFont(
                Fuentes.BOTON
        );


        btnCancelar.setFocusPainted(
                false
        );


        btnAgregar.setFont(
                Fuentes.BOTON
        );


        btnAgregar.setFocusPainted(
                false
        );


        botones.add(
                btnCancelar
        );


        botones.add(
                btnAgregar
        );


        panel.add(
                botones,
                BorderLayout.SOUTH
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
    // AGREGAR CAMPO
    // =========================================================

    private void agregarCampo(
            JPanel panel,
            GridBagConstraints gbc,
            int columna,
            int fila,
            String texto,
            JComponent campo
    ) {

        gbc.gridx =
                columna;


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
                columna + 1;


        gbc.weightx =
                1;


        campo.setPreferredSize(
                new Dimension(
                        220,
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
    // LIMPIAR FORMULARIO
    // =========================================================

    public void limpiarFormulario() {

        txtRazonSocial.setText("");

        txtCuit.setText("");

        txtEmail.setText("");

        txtTelefono.setText("");

        txtCalle.setText("");

        txtNumero.setText("");

        txtCiudad.setText("");

        txtCodigoPostal.setText("");

        txtPais.setText("");
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public JTable getTablaClientes() {

        return tablaClientes;
    }

    public JTable getTablaRankingEnvios() { return tablaRankingEnvios; }


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


    public JButton getBtnCancelar() {

        return btnCancelar;
    }


    public JTextField getTxtRazonSocial() {

        return txtRazonSocial;
    }


    public JTextField getTxtCuit() {

        return txtCuit;
    }


    public JTextField getTxtEmail() {

        return txtEmail;
    }


    public JTextField getTxtTelefono() {

        return txtTelefono;
    }


    public JTextField getTxtCalle() {

        return txtCalle;
    }


    public JTextField getTxtNumero() {

        return txtNumero;
    }


    public JTextField getTxtCiudad() {

        return txtCiudad;
    }


    public JTextField getTxtCodigoPostal() {

        return txtCodigoPostal;
    }


    public JTextField getTxtPais() {

        return txtPais;
    }


    public JButton getBtnAgregar() {

        return btnAgregar;
    }


    public JLabel getLblEstado() {

        return lblEstado;
    }
}
