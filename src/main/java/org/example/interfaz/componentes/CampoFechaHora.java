package org.example.interfaz.componentes;

import com.toedter.calendar.JDateChooser;
import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.*;
import java.util.Date;

public class CampoFechaHora extends JPanel {

    private final JDateChooser dateChooser;

    private final JSpinner hora;

    private final JSpinner minuto;

    private final JSpinner segundo;


    public CampoFechaHora() {

        setLayout(
                new BorderLayout(
                        8,
                        0
                )
        );

        setOpaque(false);


        // =====================================================
        // TAMAÑO
        // =====================================================

        setPreferredSize(
                new Dimension(
                        450,
                        38
                )
        );

        setMinimumSize(
                new Dimension(
                        250,
                        38
                )
        );

        setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        38
                )
        );


        // =====================================================
        // CALENDARIO
        // =====================================================

        dateChooser =
                new JDateChooser();

        dateChooser.setDateFormatString(
                "yyyy-MM-dd"
        );

        dateChooser.setFont(
                Fuentes.NORMAL
        );

        dateChooser.setOpaque(true);

        dateChooser.setBackground(
                Color.WHITE
        );

        dateChooser.setPreferredSize(
                new Dimension(
                        280,
                        38
                )
        );


        dateChooser.setBorder(
                new LineBorder(
                        new Color(
                                150,
                                160,
                                170
                        ),
                        1,
                        true
                )
        );


        // =====================================================
        // TEXTO DEL CALENDARIO
        // =====================================================

        JComponent editor =
                dateChooser
                        .getDateEditor()
                        .getUiComponent();


        if (editor instanceof JTextField) {

            JTextField campo =
                    (JTextField) editor;

            campo.setOpaque(true);

            campo.setBackground(
                    Color.WHITE
            );

            campo.setForeground(
                    Colores.TEXTO
            );

            campo.setFont(
                    Fuentes.NORMAL
            );

            campo.setBorder(
                    null
            );
        }


        // =====================================================
        // SPINNERS
        // =====================================================

        hora =
                crearSpinner(
                        0,
                        23
                );

        minuto =
                crearSpinner(
                        0,
                        59
                );

        segundo =
                crearSpinner(
                        0,
                        59
                );


        // =====================================================
        // PANEL DE HORA
        // =====================================================

        JPanel panelHora =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                3,
                                2
                        )
                );

        panelHora.setOpaque(false);


        panelHora.add(
                hora
        );

        panelHora.add(
                new JLabel(":")
        );

        panelHora.add(
                minuto
        );

        panelHora.add(
                new JLabel(":")
        );

        panelHora.add(
                segundo
        );


        // =====================================================
        // AGREGAR
        // =====================================================

        add(
                dateChooser,
                BorderLayout.CENTER
        );

        add(
                panelHora,
                BorderLayout.EAST
        );
    }


    // =========================================================
    // CREAR SPINNER
    // =========================================================

    private JSpinner crearSpinner(
            int minimo,
            int maximo
    ) {

        JSpinner spinner =
                new JSpinner(
                        new SpinnerNumberModel(
                                0,
                                minimo,
                                maximo,
                                1
                        )
                );


        spinner.setPreferredSize(
                new Dimension(
                        55,
                        34
                )
        );


        spinner.setOpaque(true);

        spinner.setBackground(
                Color.WHITE
        );


        spinner.setBorder(
                new LineBorder(
                        new Color(
                                150,
                                160,
                                170
                        ),
                        1,
                        true
                )
        );


        JComponent editor =
                spinner.getEditor();


        if (
                editor instanceof
                        JSpinner.DefaultEditor
        ) {

            JTextField campo =
                    (
                            (
                                    JSpinner.DefaultEditor
                                    )
                                    editor
                    ).getTextField();


            campo.setOpaque(true);

            campo.setBackground(
                    Color.WHITE
            );

            campo.setForeground(
                    Colores.TEXTO
            );

            campo.setFont(
                    Fuentes.NORMAL
            );

            campo.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            campo.setBorder(
                    null
            );
        }


        return spinner;
    }


    // =========================================================
    // GET LOCAL DATE
    // =========================================================

    public LocalDate getLocalDate() {

        Date fecha =
                dateChooser.getDate();

        if (fecha == null) {

            return null;
        }

        return fecha
                .toInstant()
                .atZone(
                        ZoneId.systemDefault()
                )
                .toLocalDate();
    }


    // =========================================================
    // GET LOCAL DATETIME
    // =========================================================

    public LocalDateTime getLocalDateTime() {

        LocalDate fecha =
                getLocalDate();

        if (fecha == null) {

            return null;
        }


        int h =
                (Integer)
                        hora.getValue();

        int m =
                (Integer)
                        minuto.getValue();

        int s =
                (Integer)
                        segundo.getValue();


        return LocalDateTime.of(
                fecha,
                LocalTime.of(
                        h,
                        m,
                        s
                )
        );
    }


    // =========================================================
    // GET INSTANT
    // =========================================================

    public Instant getInstant() {

        LocalDateTime fechaHora =
                getLocalDateTime();

        if (fechaHora == null) {

            return null;
        }

        return fechaHora
                .atZone(
                        ZoneId.systemDefault()
                )
                .toInstant();
    }


    // =========================================================
    // SET LOCAL DATETIME
    // =========================================================

    public void setLocalDateTime(
            LocalDateTime fechaHora
    ) {

        if (fechaHora == null) {

            dateChooser.setDate(
                    null
            );

            hora.setValue(0);

            minuto.setValue(0);

            segundo.setValue(0);

            return;
        }


        dateChooser.setDate(
                Date.from(
                        fechaHora
                                .toLocalDate()
                                .atStartOfDay(
                                        ZoneId.systemDefault()
                                )
                                .toInstant()
                )
        );


        hora.setValue(
                fechaHora.getHour()
        );

        minuto.setValue(
                fechaHora.getMinute()
        );

        segundo.setValue(
                fechaHora.getSecond()
        );
    }


    // =========================================================
    // GET TEXT
    // =========================================================


    public String getText() {

        LocalDateTime fechaHora =
                getLocalDateTime();

        if (fechaHora == null) {

            return "";
        }

        return fechaHora.toString();
    }


    // =========================================================
    // GET DATE CHOOSER
    // =========================================================

    public JDateChooser getDateChooser() {

        return dateChooser;
    }
}