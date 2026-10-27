package org.example.interfaz.principal;

import org.example.mongoDB.controller.ControllerMongoDB;

import org.example.interfaz.principal.menu.MenuItem;
import org.example.logging.AppLog;

import javax.swing.*;

public class PrincipalController {

    private final ControllerMongoDB mongo = new ControllerMongoDB();

    private final PrincipalPanel view;



    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PrincipalController(
            PrincipalPanel view
    ) {

        this.view = view;


        configurarMenu();

        aplicarPermisosMenu();
    }


    // =========================================================
    // CONFIGURAR MENÚ
    // =========================================================

    private void configurarMenu() {

        for (
                MenuItem item :
                view.getMenuLateral().getItems()
        ) {

            item.addMouseListener(
                    new java.awt.event.MouseAdapter() {

                        @Override
                        public void mouseClicked(
                                java.awt.event.MouseEvent e
                        ) {

                            navegar(
                                    item.getVista()
                            );
                        }
                    }
            );
        }
    }


    // =========================================================
    // NAVEGAR
    // =========================================================

    private void navegar(
            String vista
    ) {

        try {

            boolean permitido =
                    mongo
                            .puedeAccederVista(
                                    view.getUsuarioId(),
                                    vista
                            );

            if (!permitido) {

                JOptionPane.showMessageDialog(
                        view,
                        "No tenés permisos para acceder a esta sección.",
                        "Acceso denegado",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            view.mostrarVista(
                    vista
            );

        } catch (Exception e) {

            AppLog.error(PrincipalController.class,
                    "No se pudieron verificar los permisos de acceso.", e);

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudieron verificar los permisos:\n"
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =========================================================
    // APLICAR PERMISOS AL MENÚ
    // =========================================================

    private void aplicarPermisosMenu() {

        try {

            for (
                    MenuItem item :
                    view.getMenuLateral().getItems()
            ) {

                boolean permitido =
                        mongo
                                .puedeAccederVista(
                                        view.getUsuarioId(),
                                        item.getVista()
                                );

                item.setVisible(
                        permitido
                );
            }

            view.getMenuLateral()
                    .revalidate();

            view.getMenuLateral()
                    .repaint();

        } catch (Exception e) {

            AppLog.error(PrincipalController.class,
                    "No se pudieron cargar los permisos del usuario.", e);

            JOptionPane.showMessageDialog(
                    view,
                    "No se pudieron cargar los permisos del usuario:\n"
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
