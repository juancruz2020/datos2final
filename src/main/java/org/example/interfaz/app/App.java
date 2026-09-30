package org.example.interfaz.app;

import org.example.interfaz.login.LoginFrame;

import javax.swing.*;

public class App {

    public static void iniciar() {

        SwingUtilities.invokeLater(() -> {

            LoginFrame frame =
                    new LoginFrame();

            frame.setVisible(true);
        });
    }
}