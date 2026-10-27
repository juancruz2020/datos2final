package org.example.consola;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.ColumnDefinition;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.Row;
import org.bson.Document;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Pruebas ejecutables con main, sin framework ni bases de datos. */
public class TablaConsolaTest {

    public static void main(String[] args) {
        columnasDinamicasYDatosCompletos();
        textosLargosYControles();
        resultadosVaciosYEscalares();
        filasCassandraConEsquemaDiferente();
        conservaResultadoYSoportaErroresDePresentacion();
        coleccionesAnidadasYBinarios();
        System.out.println("TablaConsola: 6 grupos de pruebas correctos, sin conexiones a bases.");
    }

    private static void columnasDinamicasYDatosCompletos() {
        Document primero = new Document("id", 1).append("nombre", "Ana");
        Document segundo = new Document("id", 2).append("columna_nueva", "dato nuevo")
                .append("saldo", new BigDecimal("123.45")).append("opcional", null);
        List<Document> originales = List.of(primero, segundo);
        String antes = originales.toString();
        String tabla = TablaConsola.formatear("Consulta de prueba", originales);
        contiene(tabla, "nombre", "columna_nueva", "dato nuevo", "saldo", "123.45", "null", "(ausente)", "Filas: 2");
        igual(antes, originales.toString());
        bordesAlineados(tabla);

        // Un campo agregado en vivo aparece en la siguiente impresión, aunque no esté en la primera fila.
        segundo.put("otro_campo", "visible");
        String actualizada = TablaConsola.formatear("Consulta modificada", originales);
        contiene(actualizada, "otro_campo", "visible");
        primero.remove("nombre");
        segundo.remove("columna_nueva");
        String eliminadas = TablaConsola.formatear("Consulta modificada", originales);
        comprobar(!eliminadas.contains("columna_nueva") && !eliminadas.contains("nombre"), "Columnas eliminadas");
        bordesAlineados(eliminadas);
    }

    private static void textosLargosYControles() {
        String largo = "INICIO-" + "Ω".repeat(135) + "-FINAL";
        Document fila = new Document("un_encabezado_que_supera_el_ancho_maximo_de_la_columna", largo)
                .append("contenido", "linea uno\nlinea dos\tvalor\r\u001b[31m | texto");
        String tabla = TablaConsola.formatear("Texto", List.of(fila));
        contiene(tabla, "INICIO-", "linea uno", "linea dos", "\\t", "\\r", "\\u001b", "\\u007c");
        StringBuilder reconstruido = new StringBuilder();
        int bordes = 0;
        for (String linea : tabla.split("\n")) {
            if (linea.startsWith("+")) bordes++;
            if (bordes == 2 && linea.startsWith("|")) {
                reconstruido.append(linea.split("\\|", -1)[1].strip());
            }
        }
        igual(largo, reconstruido.toString());
        igual(135L, tabla.chars().filter(c -> c == 'Ω').count());
        comprobar(!tabla.contains("\u001b") && !tabla.contains("\t") && !tabla.contains("\r"), "Controles escapados");
        bordesAlineados(tabla);
    }

    private static void resultadosVaciosYEscalares() {
        contiene(TablaConsola.formatear("Vacia", List.of()), "0 filas");
        contiene(TablaConsola.formatear("Nula", null), "Sin resultados", "null");
        contiene(TablaConsola.formatear("Saldo", new BigDecimal("100.00")), "valor", "100.00", "Filas: 1");
        contiene(TablaConsola.formatear("Existe", false), "false");
        contiene(TablaConsola.formatear("IDs", Arrays.asList("uno", null, "tres")), "uno", "null", "tres", "Filas: 3");
        contiene(TablaConsola.formatear("Sesiones", Map.of("sesion:1", 150L)), "sesion:1", "150");
    }

    private static void filasCassandraConEsquemaDiferente() {
        Row anterior = row(List.of("sensor_id", "temperatura"), Arrays.asList("sensor-1", null));
        Row nueva = row(List.of("sensor_id", "humedad", "campo_agregado"), List.of("sensor-2", 65, "nuevo"));
        String tabla = TablaConsola.formatear("Cassandra", List.of(anterior, nueva));
        contiene(tabla, "sensor_id", "temperatura", "humedad", "campo_agregado", "sensor-1", "sensor-2", "nuevo", "65", "null", "(ausente)");
        bordesAlineados(tabla);
        Row aliasRepetido = row(List.of("campo", "campo"), List.of("primero", "segundo"));
        contiene(TablaConsola.formatear("Alias", aliasRepetido), "campo [2]", "primero", "segundo");
    }

    private static void conservaResultadoYSoportaErroresDePresentacion() {
        PrintStream original = System.out;
        PrintStream errores = System.err;
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        ByteArrayOutputStream error = new ByteArrayOutputStream();
        try (PrintStream capturada = new PrintStream(salida, true, StandardCharsets.UTF_8);
             PrintStream errorCapturado = new PrintStream(error, true, StandardCharsets.UTF_8)) {
            System.setOut(capturada);
            System.setErr(errorCapturado);
            List<Document> datos = new ArrayList<>(List.of(new Document("id", 1)));
            comprobar(TablaConsola.mostrar("Identidad", datos) == datos, "Devuelve la lista original");
            comprobar(TablaConsola.mostrar("Null", null) == null, "Conserva null");
            Object defectuoso = new Object() {
                @Override public String toString() { throw new IllegalStateException("Falla simulada"); }
            };
            comprobar(TablaConsola.mostrar("Presentacion defectuosa", defectuoso) == defectuoso,
                    "Un error visual no debe romper la consulta");
        } finally {
            System.setOut(original);
            System.setErr(errores);
        }
        contiene(salida.toString(StandardCharsets.UTF_8), "Identidad", "Filas: 1");
        contiene(error.toString(StandardCharsets.UTF_8), "No se pudo mostrar", "IllegalStateException");
    }

    private static void coleccionesAnidadasYBinarios() {
        Map<String, Object> direccion = new LinkedHashMap<>();
        direccion.put("ciudad", "Cordoba");
        direccion.put("calle", "San Martin");
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{0, 1, 2});
        buffer.position(1);
        Document fila = new Document("direccion", direccion).append("participantes", List.of("Ana", "Juan"))
                .append("array", new int[]{3, 4}).append("binario", buffer);
        String tabla = TablaConsola.formatear("Anidados", fila);
        contiene(tabla, "ciudad=Cordoba", "calle=San Martin", "[Ana, Juan]", "[3, 4]", "0102");
        igual(1, buffer.position());
        Map<String, Object> circular = new LinkedHashMap<>();
        circular.put("referencia", circular);
        contiene(TablaConsola.formatear("Circular", circular), "referencia circular");
        bordesAlineados(tabla);
    }

    private static Row row(List<String> nombres, List<Object> valores) {
        List<ColumnDefinition> columnas = new ArrayList<>();
        for (String nombre : nombres) {
            columnas.add((ColumnDefinition) Proxy.newProxyInstance(ColumnDefinition.class.getClassLoader(),
                    new Class<?>[]{ColumnDefinition.class}, (proxy, method, args) -> {
                        if (method.getName().equals("getName")) return CqlIdentifier.fromInternal(nombre);
                        throw new UnsupportedOperationException(method.getName());
                    }));
        }
        ColumnDefinitions definiciones = (ColumnDefinitions) Proxy.newProxyInstance(
                ColumnDefinitions.class.getClassLoader(), new Class<?>[]{ColumnDefinitions.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("iterator")) return columnas.iterator();
                    throw new UnsupportedOperationException(method.getName());
                });
        return (Row) Proxy.newProxyInstance(Row.class.getClassLoader(), new Class<?>[]{Row.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getColumnDefinitions" -> definiciones;
                    case "getObject" -> valores.get((Integer) args[0]);
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }

    private static void bordesAlineados(String tabla) {
        int ancho = -1;
        for (String linea : tabla.split("\n")) {
            if (!linea.startsWith("+") && !linea.startsWith("|")) continue;
            int actual = linea.codePointCount(0, linea.length());
            if (ancho == -1) ancho = actual;
            igual(ancho, actual);
        }
    }

    private static void contiene(String texto, String... partes) {
        for (String parte : partes) comprobar(texto.contains(parte), "Falta: " + parte + "\n" + texto);
    }

    private static void igual(Object esperado, Object actual) {
        comprobar(Objects.equals(esperado, actual), "Esperado: " + esperado + "; actual: " + actual);
    }

    private static void comprobar(boolean cumple, String mensaje) {
        if (!cumple) throw new AssertionError(mensaje);
    }
}
