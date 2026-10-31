package org.example.interfaz.principal.vistas;

import com.datastax.oss.driver.api.core.cql.Row;
import org.example.cassandra.alertas.controller.AlertasCassandraController;
import org.example.interfaz.tema.Colores;
import org.example.logging.AppLog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class AlertasPanelController {

    private final AlertasPanel view;
    private final AlertasCassandraController alertas = new AlertasCassandraController();

    public AlertasPanelController(AlertasPanel view) {
        this.view = view;
        view.getActualizar().addActionListener(e -> cargarAlertas());
        cargarAlertas();
    }

    private void cargarAlertas() {
        view.getActualizar().setEnabled(false);
        view.getEstado().setText("Consultando alertas...");
        SwingWorker<List<Row>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Row> doInBackground() {
                return alertas.obtenerTodas();
            }

            @Override
            protected void done() {
                view.getActualizar().setEnabled(true);
                try {
                    List<Row> filas = get();
                    DefaultTableModel modelo = new DefaultTableModel(
                            new Object[]{"ID", "Nombre", "Descripción"}, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }
                    };
                    for (Row fila : filas) {
                        modelo.addRow(new Object[]{
                                fila.getString("id"),
                                fila.getString("nombre"),
                                fila.getString("descripcion")
                        });
                    }
                    view.getTabla().setModel(modelo);
                    view.configurarColumnas();
                    view.getEstado().setForeground(Colores.TEXTO_SECUNDARIO);
                    view.getEstado().setText("Se encontraron " + filas.size() + " alertas.");
                } catch (Exception ex) {
                    AppLog.error(AlertasPanelController.class,
                            "No se pudieron cargar las alertas de Cassandra.", ex);
                    view.getEstado().setForeground(new java.awt.Color(185, 28, 28));
                    view.getEstado().setText("No se pudieron cargar las alertas.");
                    JOptionPane.showMessageDialog(view,
                            "No se pudieron cargar las alertas.\n\n" + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
