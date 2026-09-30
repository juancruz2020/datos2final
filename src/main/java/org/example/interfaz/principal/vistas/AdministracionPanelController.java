package org.example.interfaz.principal.vistas;

import org.example.usuarios.controller.AdministracionController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Map;

public class AdministracionPanelController {

    private final AdministracionPanel view;

    private final AdministracionController controller;

    private Timer timer;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AdministracionPanelController(
            AdministracionPanel view
    ) {

        this.view = view;

        this.controller =
                new AdministracionController();

        configurarEventos();

        cargarSesiones();

        iniciarActualizacionAutomatica();
    }

    // =========================================================
    // EVENTOS
    // =========================================================

    private void configurarEventos() {

        view.getBtnActualizarSesiones()
                .addActionListener(
                        e -> cargarSesiones()
                );

        view.getBtnCerrarSesion()
                .addActionListener(
                        e -> cerrarSesionSeleccionada()
                );

        view.getBtnRegistrar()
                .addActionListener(
                        e -> registrarUsuario()
                );

        view.getBtnEliminar()
                .addActionListener(
                        e -> eliminarUsuario()
                );
    }

    // =========================================================
    // CARGAR SESIONES
    // =========================================================

    private void cargarSesiones() {

        try {

            Map<String, Long> sesiones =
                    controller.obtenerSesionesActivas();

            DefaultTableModel modelo =
                    (DefaultTableModel)
                            view.getTablaSesiones()
                                    .getModel();

            modelo.setRowCount(0);

            for (
                    Map.Entry<String, Long> entrada :
                    sesiones.entrySet()
            ) {

                modelo.addRow(
                        new Object[]{
                                entrada.getKey(),
                                formatearTiempo(
                                        entrada.getValue()
                                )
                        }
                );
            }

            view.getLblEstado()
                    .setText(
                            "Sesiones activas: "
                                    + sesiones.size()
                    );

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar las sesiones.",
                    e
            );
        }
    }

    // =========================================================
    // CERRAR SESIÓN SELECCIONADA
    // =========================================================

    private void cerrarSesionSeleccionada() {

        int fila =
                view.getTablaSesiones()
                        .getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná una sesión.",
                    "Administración",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String usuario =
                String.valueOf(
                        view.getTablaSesiones()
                                .getValueAt(
                                        fila,
                                        0
                                )
                );

        int respuesta =
                JOptionPane.showConfirmDialog(
                        view,
                        "¿Querés cerrar la sesión de \""
                                + usuario
                                + "\"?",
                        "Cerrar sesión",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (
                respuesta !=
                        JOptionPane.YES_OPTION
        ) {

            return;
        }

        try {

            controller.cerrarSesion(
                    usuario
            );

            cargarSesiones();

            JOptionPane.showMessageDialog(
                    view,
                    "La sesión de "
                            + usuario
                            + " fue cerrada.",
                    "Sesión cerrada",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {

            mostrarError(
                    "No se pudo cerrar la sesión.",
                    e
            );
        }
    }

    // =========================================================
    // ACTUALIZACIÓN AUTOMÁTICA
    // =========================================================

    private void iniciarActualizacionAutomatica() {

        timer =
                new Timer(
                        1000,
                        e -> cargarSesiones()
                );

        timer.start();
    }

    // =========================================================
    // FORMATEAR TTL
    // =========================================================

    private String formatearTiempo(
            long segundos
    ) {

        if (segundos < 0) {

            return "Expirada";
        }

        long minutos =
                segundos / 60;

        long segundosRestantes =
                segundos % 60;

        return String.format(
                "%02d:%02d",
                minutos,
                segundosRestantes
        );
    }

    // =========================================================
    // REGISTRAR USUARIO
    // =========================================================

    private void registrarUsuario() {

        JOptionPane.showMessageDialog(
                view,
                "La registración de usuarios todavía "
                        + "no está implementada.\n"
                        + "El formulario ya está preparado "
                        + "para conectar la persistencia.",
                "Próximamente",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =========================================================
    // ELIMINAR USUARIO
    // =========================================================

    private void eliminarUsuario() {

        String usuario =
                view.getTxtUsuario()
                        .getText()
                        .trim();

        if (usuario.isEmpty()) {

            JOptionPane.showMessageDialog(
                    view,
                    "Ingresá el usuario que querés eliminar.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        JOptionPane.showMessageDialog(
                view,
                "La eliminación de usuarios todavía "
                        + "no está implementada.\n"
                        + "Más adelante se conectará con "
                        + "la base de usuarios.",
                "Próximamente",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =========================================================
    // ERROR
    // =========================================================

    private void mostrarError(
            String mensaje,
            Exception e
    ) {

        JOptionPane.showMessageDialog(
                view,
                mensaje
                        + "\n\n"
                        + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}