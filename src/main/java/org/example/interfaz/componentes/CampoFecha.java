package org.example.interfaz.componentes;

import com.toedter.calendar.JDateChooser;
import org.example.interfaz.tema.Colores;
import org.example.interfaz.tema.Fuentes;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class CampoFecha extends JPanel {

    private final JDateChooser dateChooser;


    public CampoFecha() {

        setLayout(
                new BorderLayout()
        );

        setOpaque(false);


        // =====================================================
        // TAMAÑO DEL COMPONENTE COMPLETO
        // =====================================================

        setPreferredSize(
                new Dimension(
                        300,
                        38
                )
        );

        setMinimumSize(
                new Dimension(
                        150,
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
        // DATE CHOOSER
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
                        300,
                        38
                )
        );


        // =====================================================
        // BORDE EXTERIOR
        // =====================================================

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
        // CAMPO DE TEXTO INTERNO
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
        // AGREGAR
        // =====================================================

        add(
                dateChooser,
                BorderLayout.CENTER
        );
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


}