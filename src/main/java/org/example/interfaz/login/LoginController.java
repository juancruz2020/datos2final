package org.example.interfaz.login;

import org.bson.Document;
import org.example.interfaz.principal.PrincipalFrame;
import org.example.usuarios.controller.AutenticacionController;
import org.example.usuarios.controller.SesionController;

import javax.swing.*;

public class LoginController {

    private final LoginPanel view;

    private final SesionController sesionController;
    private final AutenticacionController autenticacionController;


    // =========================
    // CONSTRUCTOR
    // =========================

    public LoginController(LoginPanel view) {

        this.view = view;

        this.sesionController =
                new SesionController();

        this.autenticacionController =
                new AutenticacionController();

        configurarEventos();
    }


    // =========================
    // CONFIGURAR EVENTOS
    // =========================

    private void configurarEventos() {

        view.getBtnIngresar()
                .addActionListener(
                        e -> iniciarSesion()
                );
    }


    // =========================
    // INICIAR SESIÓN
    // =========================

    private void iniciarSesion() {

        String email =
                view.getTxtUsuario()
                        .getText()
                        .trim();

        String password =
                new String(
                        view.getTxtPassword()
                                .getPassword()
                );

        if (email.isEmpty()) {

            mostrarError(
                    "Ingresá tu usuario."
            );

            view.getTxtUsuario()
                    .requestFocus();

            return;
        }

        if (password.isEmpty()) {

            mostrarError(
                    "Ingresá tu contraseña."
            );

            view.getTxtPassword()
                    .requestFocus();

            return;
        }

        try {

            Document usuario =
                    autenticacionController.autenticar(
                            email,
                            password
                    );

            if (usuario == null) {

                mostrarError(
                        "Usuario o contraseña incorrectos."
                );

                view.getTxtPassword()
                        .setText("");

                view.getTxtPassword()
                        .requestFocus();

                return;
            }


            // =============================================
            // ID INTERNO DEL USUARIO
            // =============================================

            String usuarioId =
                    usuario
                            .getObjectId("_id")
                            .toHexString();


            // =============================================
            // NOMBRE PARA MOSTRAR
            // =============================================

            String nombre =
                    usuario.getString("nombre");

            String apellido =
                    usuario.getString("apellido");

            String nombreUsuario =
                    (nombre + " " + apellido).trim();


            // =============================================
            // CREAR SESIÓN EN REDIS
            // =============================================

            sesionController
                    .iniciarSesion(usuarioId);


            boolean activa =
                    sesionController
                            .verificarSesion(usuarioId);


            if (activa) {

                abrirPrincipal(
                        usuarioId,
                        nombreUsuario
                );

            } else {

                mostrarError(
                        "No se pudo iniciar la sesión."
                );
            }

        } catch (SecurityException e) {

            mostrarError(
                    e.getMessage()
            );

        } catch (IllegalArgumentException e) {

            mostrarError(
                    e.getMessage()
            );

        } catch (Exception e) {

            mostrarError(
                    "Error al iniciar sesión:\n"
                            + e.getMessage()
            );
        }
    }


    // =========================
    // ABRIR PRINCIPAL
    // =========================

    private void abrirPrincipal(
            String usuarioId,
            String nombreUsuario
    ) {

        SwingUtilities.invokeLater(() -> {

            PrincipalFrame frame =
                    new PrincipalFrame(
                            usuarioId,
                            nombreUsuario
                    );

            frame.setVisible(true);

            SwingUtilities
                    .getWindowAncestor(view)
                    .dispose();
        });
    }


    // =========================
    // MOSTRAR ERROR
    // =========================

    private void mostrarError(
            String mensaje
    ) {

        JOptionPane.showMessageDialog(
                view,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}