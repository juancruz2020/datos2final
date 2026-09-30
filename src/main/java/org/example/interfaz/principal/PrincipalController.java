package org.example.interfaz.principal;

import org.example.interfaz.principal.menu.MenuItem;

public class PrincipalController {

    private final PrincipalPanel view;

    public PrincipalController(
            PrincipalPanel view
    ) {

        this.view = view;

        configurarMenu();
    }

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

    private void navegar(String vista) {

        view.mostrarVista(vista);
    }
}