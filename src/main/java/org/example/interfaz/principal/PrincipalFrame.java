package org.example.interfaz.principal;

import org.example.interfaz.tema.Colores;
import org.example.usuarios.controller.SesionController;

import javax.swing.*;
import java.awt.*;

public class PrincipalFrame extends JFrame {

    private final PrincipalPanel panel;

    private final String usuarioId;

    private final String nombreUsuario;

    private final SesionController sesionController;

    private Timer timerSesion;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PrincipalFrame(
            String usuarioId,
            String nombreUsuario
    ) {

        this.usuarioId =
                usuarioId;

        this.nombreUsuario =
                nombreUsuario;

        this.sesionController =
                new SesionController();

        this.panel =
                new PrincipalPanel(
                        usuarioId,
                        nombreUsuario
                );

        configurarVentana();

        setContentPane(
                panel
        );

        new PrincipalController(
                panel
        );

        configurarBotonCerrarSesion();

        iniciarControlSesion();
    }


    // =========================================================
    // CONFIGURACIÓN DE LA VENTANA
    // =========================================================

    private void configurarVentana() {

        setTitle(
                "Logística Inteligente"
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setSize(
                1400,
                850
        );

        setMinimumSize(
                new Dimension(
                        1100,
                        700
                )
        );

        setLocationRelativeTo(
                null
        );

        setBackground(
                Colores.FONDO
        );
    }


    // =========================================================
    // BOTÓN CERRAR SESIÓN
    // =========================================================

    private void configurarBotonCerrarSesion() {

        panel.getHeader()
                .getBtnCerrarSesion()
                .addActionListener(
                        e -> cerrarSesion()
                );
    }


    private void cerrarSesion() {

        int respuesta =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Querés cerrar tu sesión?",
                        "Cerrar sesión",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            sesionController.cerrarSesion(
                    usuarioId
            );

            detenerTimerSesion();

            dispose();

            volverAlLogin();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo cerrar la sesión:\n"
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =========================================================
    // CONTROL DE SESIÓN
    // =========================================================

    private void iniciarControlSesion() {

        timerSesion =
                new Timer(
                        1000,
                        e -> actualizarSesion()
                );

        timerSesion.start();
    }


    private void actualizarSesion() {

        try {

            long segundosRestantes =
                    sesionController
                            .obtenerTiempoRestante(
                                    usuarioId
                            );

            if (segundosRestantes == -1) {

                cerrarPorSesionExpirada();

                return;
            }

            actualizarContador(
                    segundosRestantes
            );

        } catch (Exception e) {

            System.err.println(
                    "Error al verificar la sesión: "
                            + e.getMessage()
            );
        }
    }


    // =========================================================
    // CONTADOR VISUAL
    // =========================================================

    private void actualizarContador(
            long segundos
    ) {

        long minutos =
                segundos / 60;

        long segundosRestantes =
                segundos % 60;

        String tiempo =
                String.format(
                        "Sesión: %02d:%02d",
                        minutos,
                        segundosRestantes
                );

        panel.getHeader()
                .getLblTiempoSesion()
                .setText(
                        tiempo
                );
    }


    // =========================================================
    // SESIÓN EXPIRADA
    // =========================================================

    private void cerrarPorSesionExpirada() {

        detenerTimerSesion();

        JOptionPane.showMessageDialog(
                this,
                "Tu sesión expiró. Volvé a iniciar sesión.",
                "Sesión expirada",
                JOptionPane.WARNING_MESSAGE
        );

        dispose();

        volverAlLogin();
    }


    // =========================================================
    // DETENER TIMER
    // =========================================================

    private void detenerTimerSesion() {

        if (timerSesion != null) {

            timerSesion.stop();

            timerSesion = null;
        }
    }


    // =========================================================
    // VOLVER AL LOGIN
    // =========================================================

    private void volverAlLogin() {

        // Lo conectaremos con el LoginFrame.
    }


    // =========================================================
    // CERRAR FRAME
    // =========================================================

    @Override
    public void dispose() {

        detenerTimerSesion();

        super.dispose();
    }
}