package org.example.interfaz.login;

import org.example.interfaz.componentes.BotonPrincipal;
import org.example.interfaz.componentes.CampoPassword;
import org.example.interfaz.componentes.CampoTexto;
import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Dimensiones;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginPanel extends JPanel {

    private final CampoTexto txtUsuario;
    private final CampoPassword txtPassword;
    private final BotonPrincipal btnIngresar;


    // =============================================
    // CONSTRUCTOR
    // =============================================

    public LoginPanel() {

        txtUsuario = new CampoTexto();

        txtPassword = new CampoPassword();

        btnIngresar =
                new BotonPrincipal("Iniciar sesión");


        // Alinear componentes
        txtUsuario.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        txtPassword.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        btnIngresar.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );


        construir();
    }


    // =============================================
    // CONSTRUIR PANEL
    // =============================================

    private void construir() {

        setLayout(
                new GridBagLayout()
        );

        setBackground(
                Colores.FONDO
        );

        add(crearTarjeta());
    }


    // =============================================
    // CREAR TARJETA LOGIN
    // =============================================

    private JPanel crearTarjeta() {

        JPanel tarjeta =
                new JPanel();

        tarjeta.setLayout(
                new BoxLayout(
                        tarjeta,
                        BoxLayout.Y_AXIS
                )
        );

        tarjeta.setBackground(
                Colores.SUPERFICIE
        );

        tarjeta.setBorder(
                new EmptyBorder(
                        35,
                        40,
                        35,
                        40
                )
        );

        tarjeta.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );


        // =============================================
        // TÍTULO
        // =============================================

        JLabel titulo =
                new JLabel(
                        "Iniciar sesión"
                );

        titulo.setFont(
                Fuentes.TITULO
        );

        titulo.setForeground(
                Colores.TEXTO
        );

        titulo.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        tarjeta.add(titulo);


        tarjeta.add(
                Box.createVerticalStrut(
                        Dimensiones.ESPACIO_PEQUENO
                )
        );


        // =============================================
        // SUBTÍTULO
        // =============================================

        JLabel subtitulo =
                new JLabel(
                        "Sistema de Gestión Logística"
                );

        subtitulo.setFont(
                Fuentes.SUBTITULO
        );

        subtitulo.setForeground(
                Colores.TEXTO_SECUNDARIO
        );

        subtitulo.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        tarjeta.add(subtitulo);


        tarjeta.add(
                Box.createVerticalStrut(
                        Dimensiones.ESPACIO_GRANDE
                )
        );


        // =============================================
        // USUARIO
        // =============================================

        tarjeta.add(
                crearLabel("Usuario")
        );


        tarjeta.add(
                Box.createVerticalStrut(
                        Dimensiones.ESPACIO_PEQUENO
                )
        );


        tarjeta.add(
                txtUsuario
        );


        tarjeta.add(
                Box.createVerticalStrut(
                        Dimensiones.ESPACIO_MEDIO
                )
        );


        // =============================================
        // CONTRASEÑA
        // =============================================

        tarjeta.add(
                crearLabel("Contraseña")
        );


        tarjeta.add(
                Box.createVerticalStrut(
                        Dimensiones.ESPACIO_PEQUENO
                )
        );


        tarjeta.add(
                txtPassword
        );


        tarjeta.add(
                Box.createVerticalStrut(
                        Dimensiones.ESPACIO_GRANDE
                )
        );


        // =============================================
        // BOTÓN
        // =============================================

        tarjeta.add(
                btnIngresar
        );


        return tarjeta;
    }


    // =============================================
    // CREAR LABEL
    // =============================================

    private JLabel crearLabel(
            String texto
    ) {

        JLabel label =
                new JLabel(
                        texto,
                        SwingConstants.CENTER
                );

        label.setFont(
                Fuentes.LABEL
        );

        label.setForeground(
                Colores.TEXTO
        );


        Dimension tamaño =
                new Dimension(
                        300,
                        label
                                .getPreferredSize()
                                .height
                );


        label.setPreferredSize(
                tamaño
        );

        label.setMinimumSize(
                tamaño
        );

        label.setMaximumSize(
                tamaño
        );


        label.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );


        return label;
    }


    // =============================================
    // GETTERS
    // =============================================

    public CampoTexto getTxtUsuario() {

        return txtUsuario;
    }


    public CampoPassword getTxtPassword() {

        return txtPassword;
    }


    public BotonPrincipal getBtnIngresar() {

        return btnIngresar;
    }
}