package org.example.interfaz.principal.vistas;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdministracionPanel extends JPanel {

    // =========================================================
    // SESIONES
    // =========================================================

    private final JTable tablaSesiones;

    private final JButton btnActualizarSesiones;

    private final JButton btnCerrarSesion;


    // =========================================================
    // USUARIO
    // =========================================================

    private final JTextField txtNombre;

    private final JTextField txtApellido;

    private final JTextField txtEmail;

    private final JPasswordField txtContrasena;

    private final JComboBox<String> cmbRol;

    private final JButton btnRegistrar;

    private final JButton btnEliminar;


    // =========================================================
    // DATOS DEL CLIENTE
    // =========================================================

    private JPanel panelDatosCliente;

    private final JTextField txtRazonSocialCliente;

    private final JTextField txtCuitCliente;

    private final JTextField txtEmailCliente;

    private final JTextField txtTelefonoCliente;

    private final JTextField txtCalleCliente;

    private final JTextField txtNumeroCliente;

    private final JTextField txtCiudadCliente;

    private final JTextField txtCodigoPostalCliente;

    private final JTextField txtPaisCliente;


    // =========================================================
    // ESTADO
    // =========================================================

    private final JLabel lblEstado;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AdministracionPanel() {

        tablaSesiones =
                new JTable();

        btnActualizarSesiones =
                new JButton("Actualizar");

        btnCerrarSesion =
                new JButton("Cerrar sesión");


        txtNombre =
                new JTextField();

        txtApellido =
                new JTextField();

        txtEmail =
                new JTextField();

        txtContrasena =
                new JPasswordField();

        cmbRol =
                new JComboBox<>();


        txtRazonSocialCliente =
                new JTextField();

        txtCuitCliente =
                new JTextField();

        txtEmailCliente =
                new JTextField();

        txtTelefonoCliente =
                new JTextField();

        txtCalleCliente =
                new JTextField();

        txtNumeroCliente =
                new JTextField();

        txtCiudadCliente =
                new JTextField();

        txtCodigoPostalCliente =
                new JTextField();

        txtPaisCliente =
                new JTextField();


        btnRegistrar =
                new JButton(
                        "Registrar usuario"
                );

        btnEliminar =
                new JButton(
                        "Eliminar usuario"
                );


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
                        "Administración"
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
                new JPanel();

        contenido.setLayout(
                new BoxLayout(
                        contenido,
                        BoxLayout.Y_AXIS
                )
        );

        contenido.setOpaque(
                false
        );


        JPanel sesiones =
                crearPanelSesiones();

        sesiones.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        JPanel usuarios =
                crearPanelGestionUsuarios();

        usuarios.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        contenido.add(
                sesiones
        );

        contenido.add(
                Box.createVerticalStrut(
                        20
                )
        );

        contenido.add(
                usuarios
        );


        JScrollPane scrollContenido =
                new JScrollPane(
                        contenido
                );

        scrollContenido.setBorder(
                null
        );

        scrollContenido.setOpaque(
                false
        );

        scrollContenido
                .getViewport()
                .setOpaque(false);

        scrollContenido.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollContenido.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );


        add(
                scrollContenido,
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
    // PANEL SESIONES
    // =========================================================

    private JPanel crearPanelSesiones() {

        JPanel panel =
                crearPanelBase();

        panel.setLayout(
                new BorderLayout(
                        10,
                        10
                )
        );

        panel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        310
                )
        );


        JLabel titulo =
                new JLabel(
                        "Sesiones activas"
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
        // TABLA
        // =====================================================

        DefaultTableModel modelo =
                new DefaultTableModel(
                        new Object[][]{},
                        new String[]{
                                "Usuario",
                                "Tiempo restante"
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


        tablaSesiones.setModel(
                modelo
        );

        tablaSesiones.setFont(
                Fuentes.NORMAL
        );

        tablaSesiones.setRowHeight(
                28
        );

        tablaSesiones.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaSesiones
                .getTableHeader()
                .setReorderingAllowed(false);


        JScrollPane scroll =
                new JScrollPane(
                        tablaSesiones
                );

        scroll.setPreferredSize(
                new Dimension(
                        700,
                        190
                )
        );


        panel.add(
                scroll,
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


        btnActualizarSesiones.setFont(
                Fuentes.BOTON
        );

        btnCerrarSesion.setFont(
                Fuentes.BOTON
        );


        btnActualizarSesiones.setFocusPainted(
                false
        );

        btnCerrarSesion.setFocusPainted(
                false
        );


        botones.add(
                btnActualizarSesiones
        );

        botones.add(
                btnCerrarSesion
        );


        panel.add(
                botones,
                BorderLayout.SOUTH
        );


        return panel;
    }


    // =========================================================
    // GESTIÓN DE USUARIOS
    // =========================================================

    private JPanel crearPanelGestionUsuarios() {

        JPanel panel =
                crearPanelBase();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE
                )
        );


        // =====================================================
        // TÍTULO
        // =====================================================

        JLabel titulo =
                new JLabel(
                        "Gestión de usuarios"
                );

        titulo.setFont(
                Fuentes.LABEL
        );

        titulo.setForeground(
                Colores.TEXTO
        );

        titulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        panel.add(
                titulo
        );

        panel.add(
                Box.createVerticalStrut(
                        12
                )
        );


        // =====================================================
        // DATOS DEL USUARIO
        // =====================================================

        JPanel formularioUsuario =
                new JPanel(
                        new GridBagLayout()
                );

        formularioUsuario.setOpaque(
                false
        );

        formularioUsuario.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        GridBagConstraints gbc =
                crearConstraints();


        agregarCampo(
                formularioUsuario,
                gbc,
                0,
                "Nombre:",
                txtNombre
        );

        agregarCampo(
                formularioUsuario,
                gbc,
                1,
                "Apellido:",
                txtApellido
        );

        agregarCampo(
                formularioUsuario,
                gbc,
                2,
                "Email:",
                txtEmail
        );

        agregarCampo(
                formularioUsuario,
                gbc,
                3,
                "Contraseña:",
                txtContrasena
        );

        agregarCampo(
                formularioUsuario,
                gbc,
                4,
                "Rol:",
                cmbRol
        );


        panel.add(
                formularioUsuario
        );


        // =====================================================
        // DATOS DEL CLIENTE
        // =====================================================

        panelDatosCliente =
                crearPanelDatosCliente();

        panelDatosCliente.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panelDatosCliente.setVisible(
                false
        );


        panel.add(
                Box.createVerticalStrut(
                        12
                )
        );

        panel.add(
                panelDatosCliente
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

        botones.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        btnRegistrar.setFont(
                Fuentes.BOTON
        );

        btnEliminar.setFont(
                Fuentes.BOTON
        );


        btnRegistrar.setFocusPainted(
                false
        );

        btnEliminar.setFocusPainted(
                false
        );


        botones.add(
                btnRegistrar
        );

        botones.add(
                btnEliminar
        );


        panel.add(
                Box.createVerticalStrut(
                        10
                )
        );

        panel.add(
                botones
        );


        return panel;
    }


    // =========================================================
    // PANEL DATOS DEL CLIENTE
    // =========================================================

    private JPanel crearPanelDatosCliente() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        panel.setBackground(
                Colores.FONDO
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


        // =====================================================
        // TÍTULO
        // =====================================================

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
        // FORMULARIO
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


        // -----------------------------------------------------
        // IZQUIERDA
        // -----------------------------------------------------

        agregarCampoCliente(
                formulario,
                gbc,
                0,
                0,
                "Razón social:",
                txtRazonSocialCliente
        );

        agregarCampoCliente(
                formulario,
                gbc,
                0,
                1,
                "CUIT:",
                txtCuitCliente
        );

        agregarCampoCliente(
                formulario,
                gbc,
                0,
                2,
                "Email:",
                txtEmailCliente
        );

        agregarCampoCliente(
                formulario,
                gbc,
                0,
                3,
                "Teléfono:",
                txtTelefonoCliente
        );

        agregarCampoCliente(
                formulario,
                gbc,
                0,
                4,
                "País:",
                txtPaisCliente
        );


        // -----------------------------------------------------
        // DERECHA
        // -----------------------------------------------------

        agregarCampoCliente(
                formulario,
                gbc,
                2,
                0,
                "Calle:",
                txtCalleCliente
        );

        agregarCampoCliente(
                formulario,
                gbc,
                2,
                1,
                "Número:",
                txtNumeroCliente
        );

        agregarCampoCliente(
                formulario,
                gbc,
                2,
                2,
                "Ciudad:",
                txtCiudadCliente
        );

        agregarCampoCliente(
                formulario,
                gbc,
                2,
                3,
                "Código postal:",
                txtCodigoPostalCliente
        );


        panel.add(
                formulario,
                BorderLayout.CENTER
        );


        return panel;
    }


    // =========================================================
    // CONSTRAINTS USUARIO
    // =========================================================

    private GridBagConstraints crearConstraints() {

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

        gbc.anchor =
                GridBagConstraints.WEST;

        return gbc;
    }


    // =========================================================
    // AGREGAR CAMPO USUARIO
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
                        500,
                        30
                )
        );


        panel.add(
                campo,
                gbc
        );
    }


    // =========================================================
    // AGREGAR CAMPO CLIENTE
    // =========================================================

    private void agregarCampoCliente(
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
    // MOSTRAR / OCULTAR DATOS DEL CLIENTE
    // =========================================================

    public void mostrarDatosCliente() {

        panelDatosCliente.setVisible(
                true
        );

        revalidate();

        repaint();
    }


    public void ocultarDatosCliente() {

        panelDatosCliente.setVisible(
                false
        );

        revalidate();

        repaint();
    }


    // =========================================================
    // LIMPIAR DATOS DEL CLIENTE
    // =========================================================

    public void limpiarDatosCliente() {

        txtRazonSocialCliente.setText("");

        txtCuitCliente.setText("");

        txtEmailCliente.setText("");

        txtTelefonoCliente.setText("");

        txtCalleCliente.setText("");

        txtNumeroCliente.setText("");

        txtCiudadCliente.setText("");

        txtCodigoPostalCliente.setText("");

        txtPaisCliente.setText("");
    }


    // =========================================================
    // GETTERS SESIONES
    // =========================================================

    public JTable getTablaSesiones() {

        return tablaSesiones;
    }


    public JButton getBtnActualizarSesiones() {

        return btnActualizarSesiones;
    }


    public JButton getBtnCerrarSesion() {

        return btnCerrarSesion;
    }


    // =========================================================
    // GETTERS USUARIO
    // =========================================================

    public JTextField getTxtNombre() {

        return txtNombre;
    }


    public JTextField getTxtApellido() {

        return txtApellido;
    }


    public JTextField getTxtEmail() {

        return txtEmail;
    }


    public JPasswordField getTxtContrasena() {

        return txtContrasena;
    }


    public JComboBox<String> getCmbRol() {

        return cmbRol;
    }


    public JButton getBtnRegistrar() {

        return btnRegistrar;
    }


    public JButton getBtnEliminar() {

        return btnEliminar;
    }


    // =========================================================
    // GETTERS CLIENTE
    // =========================================================

    public JTextField getTxtRazonSocialCliente() {

        return txtRazonSocialCliente;
    }


    public JTextField getTxtCuitCliente() {

        return txtCuitCliente;
    }


    public JTextField getTxtEmailCliente() {

        return txtEmailCliente;
    }


    public JTextField getTxtTelefonoCliente() {

        return txtTelefonoCliente;
    }


    public JTextField getTxtCalleCliente() {

        return txtCalleCliente;
    }


    public JTextField getTxtNumeroCliente() {

        return txtNumeroCliente;
    }


    public JTextField getTxtCiudadCliente() {

        return txtCiudadCliente;
    }


    public JTextField getTxtCodigoPostalCliente() {

        return txtCodigoPostalCliente;
    }


    public JTextField getTxtPaisCliente() {

        return txtPaisCliente;
    }


    // =========================================================
    // ESTADO
    // =========================================================

    public JLabel getLblEstado() {

        return lblEstado;
    }
}