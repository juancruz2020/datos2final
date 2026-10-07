package org.example.interfaz.principal.vistas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ComunicacionesPanel extends JPanel {

    private final DefaultListModel<String> modeloConversaciones;
    private final JList<String> listaConversaciones;

    private final DefaultListModel<String> modeloMensajes;
    private final JList<String> listaMensajes;

    private final JTextArea txtMensaje;

    private final JButton btnNuevaPrivada;
    private final JButton btnNuevoGrupo;
    private final JButton btnActualizar;

    private final JButton btnEnviar;
    private final JButton btnEditar;
    private final JButton btnEliminar;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ComunicacionesPanel() {

        modeloConversaciones =
                new DefaultListModel<>();

        listaConversaciones =
                new JList<>(
                        modeloConversaciones
                );


        modeloMensajes =
                new DefaultListModel<>();

        listaMensajes =
                new JList<>(
                        modeloMensajes
                );


        txtMensaje =
                new JTextArea();


        btnNuevaPrivada =
                new JButton(
                        "Nueva conversación"
                );

        btnNuevoGrupo =
                new JButton(
                        "Nuevo grupo"
                );

        btnActualizar =
                new JButton(
                        "Actualizar"
                );

        btnEnviar =
                new JButton(
                        "Enviar"
                );

        btnEditar =
                new JButton(
                        "Editar mensaje"
                );

        btnEliminar =
                new JButton(
                        "Eliminar mensaje"
                );


        construir();
    }


    // =========================================================
    // CONSTRUIR
    // =========================================================

    private void construir() {

        setLayout(
                new BorderLayout(
                        15,
                        15
                )
        );

        setBorder(
                new EmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );


        // =====================================================
        // TITULO
        // =====================================================

        JLabel titulo =
                new JLabel(
                        "Comunicaciones"
                );

        titulo.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        24
                )
        );

        add(
                titulo,
                BorderLayout.NORTH
        );


        // =====================================================
        // PANEL IZQUIERDO - CONVERSACIONES
        // =====================================================

        JPanel panelConversaciones =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        panelConversaciones.setPreferredSize(
                new Dimension(
                        320,
                        0
                )
        );


        JLabel lblConversaciones =
                new JLabel(
                        "Conversaciones"
                );

        lblConversaciones.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        16
                )
        );


        panelConversaciones.add(
                lblConversaciones,
                BorderLayout.NORTH
        );


        listaConversaciones.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );


        panelConversaciones.add(
                new JScrollPane(
                        listaConversaciones
                ),
                BorderLayout.CENTER
        );


        JPanel botonesConversaciones =
                new JPanel(
                        new GridLayout(
                                3,
                                1,
                                5,
                                5
                        )
                );


        botonesConversaciones.add(
                btnNuevaPrivada
        );

        botonesConversaciones.add(
                btnNuevoGrupo
        );

        botonesConversaciones.add(
                btnActualizar
        );


        panelConversaciones.add(
                botonesConversaciones,
                BorderLayout.SOUTH
        );


        add(
                panelConversaciones,
                BorderLayout.WEST
        );


        // =====================================================
        // PANEL CENTRAL - MENSAJES
        // =====================================================

        JPanel panelChat =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );


        JLabel lblMensajes =
                new JLabel(
                        "Historial de mensajes"
                );

        lblMensajes.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        16
                )
        );


        panelChat.add(
                lblMensajes,
                BorderLayout.NORTH
        );


        listaMensajes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );


        panelChat.add(
                new JScrollPane(
                        listaMensajes
                ),
                BorderLayout.CENTER
        );


        // =====================================================
        // PANEL PARA ESCRIBIR
        // =====================================================

        JPanel panelInferior =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );


        txtMensaje.setRows(
                3
        );

        txtMensaje.setLineWrap(
                true
        );

        txtMensaje.setWrapStyleWord(
                true
        );


        panelInferior.add(
                new JScrollPane(
                        txtMensaje
                ),
                BorderLayout.CENTER
        );


        JPanel panelBotonesMensaje =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                5,
                                5
                        )
                );


        panelBotonesMensaje.add(
                btnEnviar
        );

        panelBotonesMensaje.add(
                btnEditar
        );

        panelBotonesMensaje.add(
                btnEliminar
        );


        panelInferior.add(
                panelBotonesMensaje,
                BorderLayout.SOUTH
        );


        panelChat.add(
                panelInferior,
                BorderLayout.SOUTH
        );


        add(
                panelChat,
                BorderLayout.CENTER
        );
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public DefaultListModel<String> getModeloConversaciones() {
        return modeloConversaciones;
    }


    public JList<String> getListaConversaciones() {
        return listaConversaciones;
    }


    public DefaultListModel<String> getModeloMensajes() {
        return modeloMensajes;
    }


    public JList<String> getListaMensajes() {
        return listaMensajes;
    }


    public JTextArea getTxtMensaje() {
        return txtMensaje;
    }


    public JButton getBtnNuevaPrivada() {
        return btnNuevaPrivada;
    }


    public JButton getBtnNuevoGrupo() {
        return btnNuevoGrupo;
    }


    public JButton getBtnActualizar() {
        return btnActualizar;
    }


    public JButton getBtnEnviar() {
        return btnEnviar;
    }


    public JButton getBtnEditar() {
        return btnEditar;
    }


    public JButton getBtnEliminar() {
        return btnEliminar;
    }
}