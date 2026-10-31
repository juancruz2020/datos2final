package org.example.interfaz.principal.vistas;

import org.example.interfaz.componentes.BotonPrincipal;
import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AlertasPanel extends JPanel {

    private final JButton actualizar = new BotonPrincipal("Actualizar alertas");
    private final JTable tabla = new JTable() {
        @Override
        public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer,
                                         int row, int column) {
            Component componente = super.prepareRenderer(renderer, row, column);
            if (componente instanceof JTextArea) {
                int ancho = getColumnModel().getColumn(column).getWidth();
                componente.setSize(Math.max(1, ancho - 12), Short.MAX_VALUE);
                int alto = componente.getPreferredSize().height + 10;
                if (getRowHeight(row) != alto) {
                    setRowHeight(row, alto);
                }
            }
            return componente;
        }
    };
    private final JLabel estado = new JLabel("Listo para consultar las alertas.");

    public AlertasPanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Colores.FONDO);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel titulo = new JLabel("Alertas");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(Colores.TEXTO);

        JLabel subtitulo = new JLabel("Incidentes y sensores que requieren atención");
        subtitulo.setFont(Fuentes.SUBTITULO);
        subtitulo.setForeground(Colores.TEXTO_SECUNDARIO);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        JPanel cabecera = new JPanel(new BorderLayout(12, 0));
        cabecera.setOpaque(false);
        cabecera.add(textos, BorderLayout.CENTER);
        cabecera.add(actualizar, BorderLayout.EAST);
        add(cabecera, BorderLayout.NORTH);

        tabla.setFont(Fuentes.NORMAL);
        tabla.setRowHeight(30);
        tabla.setFillsViewportHeight(true);
        tabla.setAutoCreateRowSorter(true);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tabla.setDefaultRenderer(Object.class, (table, value, selected, focus, row, column) -> {
            JTextArea texto = new JTextArea(value == null ? "" : value.toString());
            texto.setFont(Fuentes.NORMAL);
            texto.setLineWrap(true);
            texto.setWrapStyleWord(true);
            texto.setOpaque(true);
            texto.setBorder(BorderFactory.createEmptyBorder(5, 6, 5, 6));
            texto.setBackground(selected ? table.getSelectionBackground() : table.getBackground());
            texto.setForeground(selected ? table.getSelectionForeground() : table.getForeground());
            return texto;
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(Colores.BORDE));
        add(scroll, BorderLayout.CENTER);

        estado.setFont(Fuentes.SUBTITULO);
        estado.setForeground(Colores.TEXTO_SECUNDARIO);
        add(estado, BorderLayout.SOUTH);

        tabla.setModel(new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Descripción"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    public JButton getActualizar() { return actualizar; }
    public JTable getTabla() { return tabla; }
    public JLabel getEstado() { return estado; }

    public void configurarColumnas() {
        if (tabla.getColumnModel().getColumnCount() == 3) {
            tabla.getColumnModel().getColumn(0).setPreferredWidth(270);
            tabla.getColumnModel().getColumn(1).setPreferredWidth(190);
            tabla.getColumnModel().getColumn(2).setPreferredWidth(560);
        }
    }
}
