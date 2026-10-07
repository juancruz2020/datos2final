package org.example.interfaz.principal.vistas;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;
import org.jxmapviewer.JXMapViewer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SeguimientoGPSPanel extends JPanel {

    private final JXMapViewer mapa;

    private final JLabel lblEstado;
    private final JLabel lblCantidad;

    private final JButton btnActualizar;

    public SeguimientoGPSPanel() {

        mapa = new JXMapViewer();

        lblEstado = new JLabel("Esperando posiciones...");
        lblCantidad = new JLabel("Sensores: 0");

        btnActualizar = new JButton("Actualizar");

        construir();
    }

    // =========================================================
    // CONSTRUIR
    // =========================================================

    private void construir() {

        setLayout(new BorderLayout(15, 15));
        setBackground(Colores.FONDO);

        setBorder(
                new EmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        // =====================================================
        // CABECERA
        // =====================================================

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);

        JLabel titulo = new JLabel(
                "Seguimiento GPS"
        );

        titulo.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        24
                )
        );

        titulo.setForeground(
                Colores.TEXTO
        );

        JLabel subtitulo = new JLabel(
                "Ubicación de los sensores y contenedores"
        );

        subtitulo.setFont(
                Fuentes.NORMAL
        );

        subtitulo.setForeground(
                Colores.TEXTO_SECUNDARIO
        );

        JPanel textos = new JPanel();
        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        textos.setOpaque(false);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(5));
        textos.add(subtitulo);

        cabecera.add(
                textos,
                BorderLayout.WEST
        );

        cabecera.add(
                btnActualizar,
                BorderLayout.EAST
        );

        add(
                cabecera,
                BorderLayout.NORTH
        );

        // =====================================================
        // MAPA
        // =====================================================

        mapa.setZoom(5);

        mapa.setAddressLocation(
                new org.jxmapviewer.viewer.GeoPosition(
                        -34.6037,
                        -58.3816
                )
        );

        mapa.setBorder(
                BorderFactory.createLineBorder(
                        new Color(210, 210, 210)
                )
        );

        add(
                mapa,
                BorderLayout.CENTER
        );

        // =====================================================
        // PIE
        // =====================================================

        JPanel pie = new JPanel(
                new BorderLayout()
        );

        pie.setOpaque(false);

        lblEstado.setFont(
                Fuentes.NORMAL
        );

        lblEstado.setForeground(
                Colores.TEXTO_SECUNDARIO
        );

        lblCantidad.setFont(
                Fuentes.NORMAL
        );

        lblCantidad.setForeground(
                Colores.TEXTO_SECUNDARIO
        );

        pie.add(
                lblEstado,
                BorderLayout.WEST
        );

        pie.add(
                lblCantidad,
                BorderLayout.EAST
        );

        add(
                pie,
                BorderLayout.SOUTH
        );
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public JXMapViewer getMapa() {
        return mapa;
    }

    public JLabel getLblEstado() {
        return lblEstado;
    }

    public JLabel getLblCantidad() {
        return lblCantidad;
    }

    public JButton getBtnActualizar() {
        return btnActualizar;
    }
}