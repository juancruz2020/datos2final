package org.example.interfaz.principal.menu;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MenuLateralPanel extends JPanel {

    private static final int ANCHO_ABIERTO = 230;
    private static final int ANCHO_CERRADO = 70;

    private boolean abierto = true;

    private final List<MenuItem> items;

    public MenuLateralPanel() {

        items = new ArrayList<>();

        construir();
    }

    private void construir() {

        setLayout(new BorderLayout());

        setBackground(
                new Color(15, 42, 68)
        );

        setPreferredSize(
                new Dimension(
                        ANCHO_ABIERTO,
                        0
                )
        );

        // =========================================
        // CABECERA
        // =========================================

        JPanel cabecera = new JPanel(
                new BorderLayout()
        );

        cabecera.setOpaque(false);

        JButton btnMenu = new JButton("☰");

        btnMenu.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        20
                )
        );

        btnMenu.setForeground(Color.WHITE);

        btnMenu.setBackground(
                new Color(15, 42, 68)
        );

        btnMenu.setBorderPainted(false);

        btnMenu.setFocusPainted(false);

        btnMenu.addActionListener(
                e -> alternar()
        );

        cabecera.add(
                btnMenu,
                BorderLayout.WEST
        );

        JLabel titulo =
                new JLabel("  LOGÍSTICA");

        titulo.setForeground(Color.WHITE);

        titulo.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        15
                )
        );

        cabecera.add(
                titulo,
                BorderLayout.CENTER
        );

        cabecera.setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        10,
                        15,
                        10
                )
        );

        add(
                cabecera,
                BorderLayout.NORTH
        );

        // =========================================
        // OPCIONES
        // =========================================

        JPanel opciones = new JPanel();

        opciones.setLayout(
                new BoxLayout(
                        opciones,
                        BoxLayout.Y_AXIS
                )
        );

        opciones.setOpaque(false);

        agregarItem(
                opciones,
                "▣",
                "Dashboard",
                "DASHBOARD"
        );

        agregarItem(
                opciones,
                "♙",
                "Clientes",
                "CLIENTES"
        );

        agregarItem(
                opciones,
                "➤",
                "Envíos",
                "ENVIOS"
        );

        // =========================================
        // EVENTOS LOGÍSTICOS
        // =========================================

        agregarItem(
                opciones,
                "◆",
                "Eventos Logísticos",
                "EVENTOS_LOGISTICOS"
        );

        agregarItem(
                opciones,
                "▤",
                "Contenedores",
                "CONTENEDORES"
        );

        agregarItem(
                opciones,
                "◉",
                "Sensores",
                "SENSORES"
        );

        agregarItem(
                opciones,
                "◌",
                "Monitoreo",
                "MONITOREO"
        );

        agregarItem(
                opciones,
                "↔",
                "Trazabilidad",
                "TRAZABILIDAD"
        );

        agregarItem(
                opciones,
                "✉",
                "Comunicaciones",
                "COMUNICACIONES"
        );

        agregarItem(
                opciones,
                "$",
                "Facturación",
                "FACTURACION"
        );

        agregarItem(
                opciones,
                "▥",
                "Reportes",
                "REPORTES"
        );

        agregarItem(
                opciones,
                "⚠",
                "Riesgos",
                "RIESGOS"
        );

        agregarItem(
                opciones,
                "⚙",
                "Administración",
                "ADMINISTRACION"
        );

        add(
                opciones,
                BorderLayout.CENTER
        );
    }

    // =========================================
    // AGREGAR ITEM
    // =========================================

    private void agregarItem(
            JPanel contenedor,
            String icono,
            String texto,
            String vista
    ) {

        MenuItem item =
                new MenuItem(
                        icono,
                        texto,
                        vista
                );

        items.add(item);

        contenedor.add(item);
    }

    // =========================================
    // ABRIR / CERRAR MENÚ
    // =========================================

    private void alternar() {

        abierto = !abierto;

        int ancho =
                abierto
                        ? ANCHO_ABIERTO
                        : ANCHO_CERRADO;

        setPreferredSize(
                new Dimension(
                        ancho,
                        getHeight()
                )
        );

        for (MenuItem item : items) {

            item.setExpandido(abierto);
        }

        revalidate();
        repaint();
    }

    // =========================================
    // GET ITEMS
    // =========================================

    public List<MenuItem> getItems() {

        return items;
    }
}