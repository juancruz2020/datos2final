package org.example.interfaz.principal;

import org.example.interfaz.principal.menu.MenuItem;
import org.example.mongoDB.controller.PermisosController;

import javax.swing.*;

public class PrincipalController {

    private final PrincipalPanel view;

    private final PermisosController permisosController;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PrincipalController(
            PrincipalPanel view
    ) {

        this.view = view;

        this.permisosController =
                new PermisosController();

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
                    permisosController
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
                        permisosController
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