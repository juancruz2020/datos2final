package org.example.interfaz.principal.vistas;

import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TrazabilidadPanel extends JPanel {
    private final JTable tablaRiesgosEventos;
    private final JTable tablaAnomaliasTemperatura;
    private final JTable tablaBateriasBajas;
    private final JTable tablaVibracionesAltas;
    private final JButton btnActualizar;
    private final JLabel lblEstado;

    public TrazabilidadPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(Colores.FONDO);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 20, 30));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        JLabel titulo = new JLabel("Trazabilidad");
        titulo.setFont(Fuentes.TITULO);
        titulo.setForeground(Colores.TEXTO);
        btnActualizar = new JButton("Actualizar");
        cabecera.add(titulo, BorderLayout.WEST);
        cabecera.add(btnActualizar, BorderLayout.EAST);
        add(cabecera, BorderLayout.NORTH);

        JTabbedPane pestanas = new JTabbedPane();
        tablaRiesgosEventos = crearTabla(new String[]{"Envío", "Ubicación", "Repeticiones", "Motivo"});
        tablaAnomaliasTemperatura = crearTabla(new String[]{"Contenedor", "Sensor", "Temperatura", "Fecha"});
        tablaBateriasBajas = crearTabla(new String[]{"Contenedor", "Sensor", "Batería", "Fecha"});
        tablaVibracionesAltas = crearTabla(new String[]{"Contenedor", "Sensor", "Vibración", "Fecha"});
        pestanas.addTab("Riesgo por eventos", panelTabla(
                "Envíos con ubicación de origen o destino repetida en eventos", tablaRiesgosEventos));
        pestanas.addTab("Temperaturas de riesgo", panelTabla(
                "Temperaturas de riesgo: menor a 15 °C o mayor a 40 °C", tablaAnomaliasTemperatura));
        pestanas.addTab("Batería baja", panelTabla(
                "Batería baja: 40% o menos", tablaBateriasBajas));
        pestanas.addTab("Vibración alta", panelTabla(
                "Vibración alta: superior a 1.2", tablaVibracionesAltas));
        add(pestanas, BorderLayout.CENTER);

        lblEstado = new JLabel("Actualizá para consultar los análisis de trazabilidad.");
        lblEstado.setFont(Fuentes.NORMAL);
        lblEstado.setForeground(Colores.TEXTO_SECUNDARIO);
        add(lblEstado, BorderLayout.SOUTH);
    }

    private JTable crearTabla(String[] columnas) {
        JTable tabla = new JTable(new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        });
        tabla.setRowHeight(28);
        tabla.setFont(Fuentes.NORMAL);
        tabla.getTableHeader().setFont(Fuentes.LABEL);
        tabla.getTableHeader().setReorderingAllowed(false);
        return tabla;
    }

    private JPanel panelTabla(String titulo, JTable tabla) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Colores.SUPERFICIE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colores.BORDE),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(Fuentes.LABEL);
        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    public JTable getTablaRiesgosEventos() { return tablaRiesgosEventos; }
    public JTable getTablaAnomaliasTemperatura() { return tablaAnomaliasTemperatura; }
    public JTable getTablaBateriasBajas() { return tablaBateriasBajas; }
    public JTable getTablaVibracionesAltas() { return tablaVibracionesAltas; }
    public JButton getBtnActualizar() { return btnActualizar; }
    public JLabel getLblEstado() { return lblEstado; }
}
