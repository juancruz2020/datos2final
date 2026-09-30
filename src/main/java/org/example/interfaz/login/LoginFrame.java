package org.example.interfaz.login;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Dimensiones;

import javax.swing.*;

public class LoginFrame extends JFrame {

    private final LoginPanel panel;

    public LoginFrame() {

        panel = new LoginPanel();

        configurarVentana();

        setContentPane(panel);

        new LoginController(panel);
    }

    private void configurarVentana() {

        setTitle("Sistema Logístico");

        setSize(
                Dimensiones.LOGIN_ANCHO,
                Dimensiones.LOGIN_ALTO
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        setResizable(false);

        getContentPane().setBackground(
                Colores.FONDO
        );
    }
}