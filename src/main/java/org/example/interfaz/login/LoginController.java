package org.example.interfaz.login;

import org.example.interfaz.principal.PrincipalFrame;
import org.example.usuarios.controller.SesionController;

import javax.swing.*;

public class LoginController {

    private final LoginPanel view;

    private final SesionController sesionController;

    public LoginController(LoginPanel view) {

        this.view = view;

        this.sesionController =
                new SesionController();

        configurarEventos();
    }

    private void configurarEventos() {

        view.getBtnIngresar()
                .addActionListener(
                        e -> iniciarSesion()
                );
    }

    private void iniciarSesion() {

        String usuario =
                view.getTxtUsuario()
                        .getText()
                        .trim();

        String password =
                new String(
                        view.getTxtPassword()
                                .getPassword()
                );

        if (usuario.isEmpty()) {

            mostrarError(
                    "Ingresá tu usuario."
            );

            view.getTxtUsuario().requestFocus();

            return;
        }

        if (password.isEmpty()) {

            mostrarError(
                    "Ingresá tu contraseña."
            );

            view.getTxtPassword().requestFocus();

            return;
        }

        try {

            sesionController.iniciarSesion(usuario);

            boolean activa =
                    sesionController
                            .verificarSesion(usuario);

            if (activa) {

                abrirPrincipal(usuario);

            } else {

                mostrarError(
                        "No se pudo iniciar la sesión."
                );
            }

        } catch (Exception e) {

            mostrarError(
                    "Error al iniciar sesión:\n"
                            + e.getMessage()
            );
        }
    }

    private void abrirPrincipal(String usuario) {

        SwingUtilities.invokeLater(() -> {

            PrincipalFrame frame =
                    new PrincipalFrame(usuario);

            frame.setVisible(true);

            SwingUtilities
                    .getWindowAncestor(view)
                    .dispose();
        });
    }

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                view,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}