package org.example.DatosPorDefecto;

import com.datastax.oss.driver.api.core.CqlSession;
import org.example.conecciones.CassandraSingleton;
import org.example.cassandra.monitoreo.dao.InsertarCassandraMonitoreo;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;

public class DatosDePruebaCassandra {

    private final Random random = new Random();

    // =====================================================
    // PAÍSES
    // =====================================================

    private final List<String> paises = List.of(
            "Argentina",
            "Brasil",
            "Chile",
            "Uruguay",
            "Paraguay"
    );

    // =====================================================
    // REGIONES
    // =====================================================

    private final List<String> regionesArgentina = List.of(
            "Buenos Aires",
            "Córdoba",
            "Mendoza",
            "Santa Fe",
            "Tucumán"
    );

    private final List<String> regionesBrasil = List.of(
            "São Paulo",
            "Rio de Janeiro",
            "Paraná",
            "Bahia",
            "Minas Gerais"
    );

    private final List<String> regionesChile = List.of(
            "Santiago",
            "Valparaíso",
            "Biobío",
            "Coquimbo",
            "Antofagasta"
    );

    private final List<String> regionesUruguay = List.of(
            "Montevideo",
            "Canelones",
            "Maldonado",
            "Colonia",
            "Salto"
    );

    private final List<String> regionesParaguay = List.of(
            "Asunción",
            "Central",
            "Alto Paraná",
            "Itapúa",
            "Caaguazú"
    );


    // =====================================================
    // INSERTAR 60 LECTURAS
    // =====================================================

    public void insertarDatos(
            List<String> sensorIds,
            List<String> contenedorIds) {

        if (sensorIds == null || sensorIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "La lista de sensores no puede estar vacía."
            );
        }

        if (contenedorIds == null || contenedorIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "La lista de contenedores no puede estar vacía."
            );
        }

        // =================================================
        // INICIALIZAR CASSANDRA
        // =================================================

        CqlSession session =
                CassandraSingleton.getInstance();

        // =================================================
        // GENERAR 60 LECTURAS
        // =================================================

        for (int i = 0; i < 150; i++) {

            // Sensor de la lista recibida
            String sensorId = sensorIds.get(
                    random.nextInt(sensorIds.size())
            );

            // Contenedor de la lista recibida
            String contenedorId = contenedorIds.get(
                    random.nextInt(contenedorIds.size())
            );

            // Fecha
            LocalDate fechaDia =
                    generarFechaAleatoria();

            // Fecha y hora
            Instant fechaHora =
                    generarFechaHoraAleatoria(fechaDia);

            // País
            String pais =
                    paises.get(
                            random.nextInt(paises.size())
                    );

            // Región compatible con el país
            String region =
                    obtenerRegionAleatoria(pais);

            // Datos del sensor
            BigDecimal temperatura =
                    generarDecimal(5, 40);

            BigDecimal humedad =
                    generarDecimal(20, 95);

            BigDecimal vibracion =
                    generarDecimal(0, 2);

            BigDecimal bateria =
                    generarDecimal(20, 100);

            // GPS
            BigDecimal latitud =
                    generarLatitud(pais);

            BigDecimal longitud =
                    generarLongitud(pais);

            // =================================================
            // INSERTAR EN CASSANDRA
            // =================================================

            InsertarCassandraMonitoreo.insertarLecturaSensor(
                    session,
                    sensorId,
                    fechaDia,
                    fechaHora,
                    contenedorId,
                    temperatura,
                    humedad,
                    vibracion,
                    latitud,
                    longitud,
                    bateria,
                    pais,
                    region
            );
        }

        System.out.println(
                "150 lecturas aleatorias cargadas correctamente."
        );
    }


    // =====================================================
    // FECHA ALEATORIA
    // =====================================================

    private LocalDate generarFechaAleatoria() {

        LocalDate inicio =
                LocalDate.of(2026, 9, 1);

        LocalDate fin =
                LocalDate.of(2026, 10, 6);

        long inicioDia =
                inicio.toEpochDay();

        long finDia =
                fin.toEpochDay();

        long dia =
                inicioDia +
                        random.nextInt(
                                (int) (finDia - inicioDia + 1)
                        );

        return LocalDate.ofEpochDay(dia);
    }


    // =====================================================
    // FECHA Y HORA ALEATORIA
    // =====================================================

    private Instant generarFechaHoraAleatoria(
            LocalDate fecha) {

        int hora =
                random.nextInt(24);

        int minuto =
                random.nextInt(60);

        int segundo =
                random.nextInt(60);

        return fecha
                .atTime(hora, minuto, segundo)
                .toInstant(ZoneOffset.UTC);
    }


    // =====================================================
    // DECIMAL ALEATORIO
    // =====================================================

    private BigDecimal generarDecimal(
            double minimo,
            double maximo) {

        double valor =
                minimo +
                        (maximo - minimo) *
                                random.nextDouble();

        return BigDecimal
                .valueOf(valor)
                .setScale(
                        2,
                        java.math.RoundingMode.HALF_UP
                );
    }


    // =====================================================
    // REGIÓN SEGÚN PAÍS
    // =====================================================

    private String obtenerRegionAleatoria(
            String pais) {

        List<String> regiones;

        switch (pais) {

            case "Argentina":
                regiones = regionesArgentina;
                break;

            case "Brasil":
                regiones = regionesBrasil;
                break;

            case "Chile":
                regiones = regionesChile;
                break;

            case "Uruguay":
                regiones = regionesUruguay;
                break;

            case "Paraguay":
                regiones = regionesParaguay;
                break;

            default:
                throw new IllegalArgumentException(
                        "País no soportado: " + pais
                );
        }

        return regiones.get(
                random.nextInt(regiones.size())
        );
    }


    // =====================================================
    // LATITUD
    // =====================================================

    private BigDecimal generarLatitud(
            String pais) {

        return switch (pais) {

            case "Argentina" ->
                    generarDecimal(-55, -22);

            case "Brasil" ->
                    generarDecimal(-33, 5);

            case "Chile" ->
                    generarDecimal(-56, -17);

            case "Uruguay" ->
                    generarDecimal(-35, -30);

            case "Paraguay" ->
                    generarDecimal(-28, -19);

            default ->
                    BigDecimal.ZERO;
        };
    }


    // =====================================================
    // LONGITUD
    // =====================================================

    private BigDecimal generarLongitud(
            String pais) {

        return switch (pais) {

            case "Argentina" ->
                    generarDecimal(-73, -53);

            case "Brasil" ->
                    generarDecimal(-74, -34);

            case "Chile" ->
                    generarDecimal(-76, -66);

            case "Uruguay" ->
                    generarDecimal(-59, -53);

            case "Paraguay" ->
                    generarDecimal(-63, -54);

            default ->
                    BigDecimal.ZERO;
        };
    }
}