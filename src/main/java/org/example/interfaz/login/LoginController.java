package org.example.interfaz.login;

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

        // =============================================
        // VALIDACIONES
        // =============================================

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

        // =============================================
        // SESIÓN
        // =============================================

        try {

            /*
             * Por ahora SesionController recibe
             * únicamente el ID del usuario.
             *
             * La validación de usuario y contraseña
             * la conectaremos posteriormente.
             */

            sesionController.iniciarSesion(usuario);

            boolean activa =
                    sesionController
                            .verificarSesion(usuario);

            if (activa) {

                JOptionPane.showMessageDialog(
                        view,
                        "Inicio de sesión exitoso.",
                        "Bienvenido",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Próximamente:
                // abrir PrincipalFrame

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

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                view,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}