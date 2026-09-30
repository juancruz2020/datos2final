package org.example.interfaz.principal.vistas;

import com.datastax.oss.driver.api.core.cql.Row;
import org.example.interfaz.componentes.CampoFecha;
import org.example.interfaz.componentes.CampoFechaHora;
import org.example.monitoreo.controller.ControllerMonitoreo;
import org.example.monitoreo.controller.ControllerMonitoreoInsert;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class MonitoreoPanelController {

    private final MonitoreoPanel view;

    private final ControllerMonitoreo monitoreo;

    private final ControllerMonitoreoInsert insert;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public MonitoreoPanelController(
            MonitoreoPanel view
    ) {

        this.view = view;

        this.monitoreo =
                new ControllerMonitoreo();

        this.insert =
                new ControllerMonitoreoInsert();

        configurarEventos();
    }


    // =========================================================
    // EVENTOS
    // =========================================================

    private void configurarEventos() {

        view.getEjecutarConsulta()
                .addActionListener(
                        e -> ejecutarConsulta()
                );


        view.getInsertarLectura()
                .addActionListener(
                        e -> insertarLectura()
                );




        view.getCrearTablas()
                .addActionListener(
                        e -> ejecutarAccion(
                                "Crear tablas",
                                monitoreo::crearTablas
                        )
                );


        view.getCargarPrueba()
                .addActionListener(
                        e -> ejecutarAccion(
                                "Cargar datos de prueba",
                                monitoreo::cargarDatosDePrueba
                        )
                );
    }


    // =========================================================
    // CONSULTAS
    // =========================================================

    private void ejecutarConsulta() {

        try {

            String opcion =
                    (String)
                            view.getComboConsulta()
                                    .getSelectedItem();


            List<Row> resultados;


            switch (opcion) {

                // -------------------------------------------------
                // HISTORIAL
                // -------------------------------------------------

                case "Historial de sensor" ->

                        resultados =
                                monitoreo.obtenerHistorialSensor(

                                        uuid(
                                                view.getSensorId()
                                        ),

                                        fecha(
                                                view.getFecha()
                                        )
                                );


                // -------------------------------------------------
                // ENTRE HORARIOS
                // -------------------------------------------------

                case "Lecturas entre horarios" ->

                        resultados =
                                monitoreo.obtenerLecturasEntreFechas(

                                        uuid(
                                                view.getSensorId()
                                        ),

                                        fecha(
                                                view.getFecha()
                                        ),

                                        fechaHora(
                                                view.getDesde()
                                        ),

                                        fechaHora(
                                                view.getHasta()
                                        )
                                );


                // -------------------------------------------------
                // TEMPERATURAS
                // -------------------------------------------------

                case "Temperaturas" ->

                        resultados =
                                monitoreo.obtenerTemperaturas(

                                        uuid(
                                                view.getSensorId()
                                        ),

                                        fecha(
                                                view.getFecha()
                                        )
                                );


                // -------------------------------------------------
                // BATERÍA
                // -------------------------------------------------

                case "Batería" ->

                        resultados =
                                monitoreo.obtenerBateria(

                                        uuid(
                                                view.getSensorId()
                                        ),

                                        fecha(
                                                view.getFecha()
                                        )
                                );


                // -------------------------------------------------
                // GPS
                // -------------------------------------------------

                case "Posiciones GPS" ->

                        resultados =
                                monitoreo.obtenerGPS(

                                        uuid(
                                                view.getSensorId()
                                        ),

                                        fecha(
                                                view.getFecha()
                                        )
                                );


                // -------------------------------------------------
                // REGIÓN
                // -------------------------------------------------

                case "Métricas por región" ->

                        resultados =
                                monitoreo.obtenerMetricasRegion(

                                        texto(
                                                view.getRegion()
                                        ),

                                        fecha(
                                                view.getRegionDesde()
                                        ),

                                        fecha(
                                                view.getRegionHasta()
                                        )
                                );


                // -------------------------------------------------
                // PAÍS
                // -------------------------------------------------

                case "Métricas por país" ->

                        resultados =
                                monitoreo.obtenerMetricasPais(

                                        texto(
                                                view.getPais()
                                        ),

                                        fecha(
                                                view.getPaisDesde()
                                        ),

                                        fecha(
                                                view.getPaisHasta()
                                        )
                                );


                // -------------------------------------------------
                // TABLA COMPLETA DE LECTURAS
                // -------------------------------------------------

                case "Todas las lecturas" ->

                        resultados =
                                monitoreo.obtenerTodasLasLecturas();


                // -------------------------------------------------
                // TABLA COMPLETA DE MÉTRICAS POR REGIÓN
                // -------------------------------------------------

                case "Todas las métricas por región" ->

                        resultados =
                                monitoreo.obtenerTodasLasMetricasRegion();


                // -------------------------------------------------
                // TABLA COMPLETA DE MÉTRICAS POR PAÍS
                // -------------------------------------------------

                case "Todas las métricas por país" ->

                        resultados =
                                monitoreo.obtenerTodasLasMetricasPais();


                default ->

                        throw new IllegalArgumentException(
                                "Tipo de consulta no reconocido."
                        );
            }


            mostrarResultados(
                    resultados
            );


            view.getEstado().setText(
                    "Consulta realizada correctamente. " +
                            "Registros: " +
                            resultados.size()
            );


        } catch (Exception ex) {

            mostrarError(
                    "No se pudo realizar la consulta.",
                    ex
            );
        }
    }


    // =========================================================
    // INSERTAR LECTURA
    // =========================================================

    private void insertarLectura() {

        try {

            insert.insertarLecturaSensor(

                    uuid(
                            view.getiSensorId()
                    ),

                    fecha(
                            view.getiFechaDia()
                    ),

                    instant(
                            view.getiFechaHora()
                    ),

                    uuid(
                            view.getiContenedorId()
                    ),

                    decimal(
                            view.getiTemperatura()
                    ),

                    decimal(
                            view.getiHumedad()
                    ),

                    decimal(
                            view.getiVibracion()
                    ),

                    decimal(
                            view.getiLatitud()
                    ),

                    decimal(
                            view.getiLongitud()
                    ),

                    decimal(
                            view.getiBateria()
                    ),

                    texto(
                            view.getiPais()
                    ),

                    texto(
                            view.getiRegion()
                    )
            );


            exito(
                    "Lectura insertada correctamente."
            );


        } catch (Exception ex) {

            mostrarError(
                    "No se pudo insertar la lectura.",
                    ex
            );
        }
    }



    // =========================================================
    // MOSTRAR RESULTADOS
    // =========================================================

    private void mostrarResultados(
            List<Row> filas
    ) {

        if (
                filas == null ||
                        filas.isEmpty()
        ) {

            view.getTabla()
                    .setModel(
                            new DefaultTableModel()
                    );

            return;
        }


        Row primera =
                filas.get(0);


        int columnas =
                primera
                        .getColumnDefinitions()
                        .size();


        String[] nombres =
                new String[columnas];


        for (
                int i = 0;
                i < columnas;
                i++
        ) {

            nombres[i] =
                    primera
                            .getColumnDefinitions()
                            .get(i)
                            .getName()
                            .asInternal();
        }


        DefaultTableModel modelo =
                new DefaultTableModel(
                        nombres,
                        0
                );


        for (
                Row fila :
                filas
        ) {

            Object[] datos =
                    new Object[columnas];


            for (
                    int i = 0;
                    i < columnas;
                    i++
            ) {

                datos[i] =
                        fila.getObject(i);
            }


            modelo.addRow(
                    datos
            );
        }


        view.getTabla()
                .setModel(
                        modelo
                );
    }


    // =========================================================
    // ACCIONES ADMINISTRATIVAS
    // =========================================================

    private void ejecutarAccion(
            String nombre,
            Runnable accion
    ) {

        try {

            accion.run();

            exito(
                    nombre +
                            " realizado correctamente."
            );


        } catch (Exception ex) {

            mostrarError(
                    "No se pudo ejecutar: " +
                            nombre,
                    ex
            );
        }
    }


    // =========================================================
    // CONVERSIONES
    // =========================================================

    private UUID uuid(
            JTextField campo
    ) {

        return UUID.fromString(
                texto(campo)
        );
    }


    // =========================================================
    // CAMPO FECHA
    // =========================================================

    private LocalDate fecha(
            CampoFecha campo
    ) {

        LocalDate valor =
                campo.getLocalDate();

        if (valor == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar una fecha."
            );
        }

        return valor;
    }


    // =========================================================
    // CAMPO FECHA Y HORA
    // =========================================================

    private LocalDateTime fechaHora(
            CampoFechaHora campo
    ) {

        LocalDateTime valor =
                campo.getLocalDateTime();

        if (valor == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar una fecha y hora."
            );
        }

        return valor;
    }


    // =========================================================
    // CAMPO FECHA Y HORA -> INSTANT
    // =========================================================

    private Instant instant(
            CampoFechaHora campo
    ) {

        Instant valor =
                campo.getInstant();

        if (valor == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar una fecha y hora."
            );
        }

        return valor;
    }


    // =========================================================
    // DECIMAL
    // =========================================================

    private BigDecimal decimal(
            JTextField campo
    ) {

        return new BigDecimal(
                texto(campo)
        );
    }


    // =========================================================
    // LONG
    // =========================================================

    private long numeroLong(
            JTextField campo
    ) {

        return Long.parseLong(
                texto(campo)
        );
    }


    // =========================================================
    // TEXTO
    // =========================================================

    private String texto(
            JTextField campo
    ) {

        String valor =
                campo.getText().trim();


        if (
                valor.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Hay campos obligatorios sin completar."
            );
        }


        return valor;
    }


    // =========================================================
    // MENSAJE DE ÉXITO
    // =========================================================

    private void exito(
            String mensaje
    ) {

        view.getEstado()
                .setText(
                        mensaje
                );


        JOptionPane.showMessageDialog(
                view,
                mensaje,
                "Monitoreo",
                JOptionPane.INFORMATION_MESSAGE
        );
    }


    // =========================================================
    // MENSAJE DE ERROR
    // =========================================================

    private void mostrarError(
            String mensaje,
            Exception ex
    ) {

        view.getEstado()
                .setText(
                        mensaje
                );


        JOptionPane.showMessageDialog(
                view,

                mensaje +
                        "\n\n" +
                        ex.getMessage(),

                "Error",

                JOptionPane.ERROR_MESSAGE
        );
    }
}