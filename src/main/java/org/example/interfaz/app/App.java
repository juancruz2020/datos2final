package org.example.interfaz.app;

import org.example.interfaz.login.LoginFrame;
import org.example.logging.AppLog;

import javax.swing.*;

public class App {

    public static void iniciar() {

        Thread.setDefaultUncaughtExceptionHandler((hilo, error) ->
                AppLog.error(App.class,
                        "Excepción no controlada en el hilo "
                                + hilo.getName() + ".", error));

        AppLog.info(App.class, "Mostrando pantalla de inicio de sesión.");

        SwingUtilities.invokeLater(() -> {

            LoginFrame frame =
                    new LoginFrame();

            frame.setVisible(true);
        });
    }
}
