package org.example.consola;

import com.datastax.oss.driver.api.core.cql.Row;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HexFormat;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Vista de consultas en consola. Las columnas surgen de TODOS los registros, no del primero.
 * Acepta Document/Map (MongoDB y Neo4j), Row de Cassandra, listas y resultados escalares.
 * Los valores largos se continúan en otras líneas: no se recortan campos ni filas.
 * Para ver un campo nuevo, la consulta/DAO debe incluirlo en su resultado.
 */
public final class TablaConsola {

    private static final int ANCHO_MAXIMO_COLUMNA = 48;

    private TablaConsola() {
    }

    /** Imprime la consulta y devuelve exactamente el mismo resultado, sin copiarlo ni modificarlo. */
    public static <T> T mostrar(String titulo, T resultado) {
        try {
            String tabla = formatear(titulo, resultado);
            synchronized (TablaConsola.class) {
                System.out.print(tabla);
            }
        } catch (RuntimeException error) {
            // Una falla de presentación no debe convertir una consulta exitosa en un error de negocio.
            System.err.println("No se pudo mostrar la consulta " + titulo
                    + " en consola: " + error.getClass().getSimpleName());
        }
        return resultado;
    }

    /** Solo construye texto; permite comprobar el formato sin conectarse a ninguna base. */
    public static String formatear(String titulo, Object resultado) {
        StringBuilder salida = new StringBuilder("\n=== ")
                .append(textoSeguro(String.valueOf(titulo)).replace("\n", "\\n"))
                .append(" ===\n");
        if (resultado == null) {
            return salida.append("Sin resultados (null).\n").toString();
        }

        List<Map<String, Object>> filas = new ArrayList<>();
        if (resultado instanceof Iterable<?> elementos) {
            for (Object elemento : elementos) {
                filas.add(fila(elemento));
            }
        } else {
            filas.add(fila(resultado));
        }
        if (filas.isEmpty()) {
            return salida.append("Sin resultados (0 filas).\n").toString();
        }

        Set<String> columnas = new LinkedHashSet<>();
        for (Map<String, Object> fila : filas) {
            columnas.addAll(fila.keySet());
        }
        List<String> encabezados = new ArrayList<>(columnas);
        List<List<String>> celdas = new ArrayList<>();
        for (Map<String, Object> fila : filas) {
            List<String> valores = new ArrayList<>();
            for (String columna : encabezados) {
                valores.add(fila.containsKey(columna)
                        ? textoSeguro(valorCompleto(fila.get(columna), new IdentityHashMap<>()))
                        : "(ausente)");
            }
            celdas.add(valores);
        }

        int[] anchos = new int[encabezados.size()];
        List<String> encabezadosSeguros = encabezados.stream().map(TablaConsola::textoSeguro).toList();
        for (int i = 0; i < anchos.length; i++) {
            int ancho = ancho(encabezadosSeguros.get(i));
            for (List<String> fila : celdas) {
                ancho = Math.max(ancho, ancho(fila.get(i)));
            }
            anchos[i] = Math.max(1, Math.min(ANCHO_MAXIMO_COLUMNA, ancho));
        }

        String borde = borde(anchos);
        salida.append(borde);
        agregarFila(salida, encabezadosSeguros, anchos);
        salida.append(borde);
        for (List<String> fila : celdas) {
            agregarFila(salida, fila, anchos);
            salida.append(borde);
        }
        return salida.append("Filas: ").append(filas.size()).append('\n').toString();
    }

    private static Map<String, Object> fila(Object elemento) {
        Map<String, Object> fila = new LinkedHashMap<>();
        if (elemento instanceof Map<?, ?> mapa && !mapa.isEmpty()) {
            for (Map.Entry<?, ?> campo : mapa.entrySet()) {
                agregarCampo(fila, String.valueOf(campo.getKey()), campo.getValue());
            }
        } else if (elemento instanceof Row row) {
            int indice = 0;
            for (var columna : row.getColumnDefinitions()) {
                agregarCampo(fila, columna.getName().asInternal(), row.getObject(indice++));
            }
        } else {
            fila.put("valor", elemento);
        }
        if (fila.isEmpty()) {
            fila.put("valor", "{}");
        }
        return fila;
    }

    private static void agregarCampo(Map<String, Object> fila, String nombre, Object valor) {
        String unico = nombre;
        int repeticion = 2;
        while (fila.containsKey(unico)) {
            unico = nombre + " [" + repeticion++ + "]";
        }
        fila.put(unico, valor);
    }

    private static String valorCompleto(Object valor, IdentityHashMap<Object, Boolean> visitados) {
        if (valor == null) return "null";
        if (valor instanceof BigDecimal numero) return numero.toPlainString();
        if (valor instanceof Date fecha) return fecha.toInstant().toString();
        if (valor instanceof byte[] bytes) return HexFormat.of().formatHex(bytes);
        if (valor instanceof ByteBuffer buffer) {
            ByteBuffer copia = buffer.duplicate();
            byte[] bytes = new byte[copia.remaining()];
            copia.get(bytes);
            return HexFormat.of().formatHex(bytes);
        }
        if (visitados.put(valor, Boolean.TRUE) != null) return "(referencia circular)";
        try {
            if (valor instanceof Map<?, ?> mapa) {
                StringJoiner campos = new StringJoiner(", ", "{", "}");
                for (Map.Entry<?, ?> campo : mapa.entrySet()) {
                    campos.add(campo.getKey() + "=" + valorCompleto(campo.getValue(), visitados));
                }
                return campos.toString();
            }
            if (valor instanceof Iterable<?> lista) {
                StringJoiner elementos = new StringJoiner(", ", "[", "]");
                for (Object elemento : lista) elementos.add(valorCompleto(elemento, visitados));
                return elementos.toString();
            }
            if (valor.getClass().isArray()) {
                StringJoiner elementos = new StringJoiner(", ", "[", "]");
                for (int i = 0; i < Array.getLength(valor); i++) {
                    elementos.add(valorCompleto(Array.get(valor, i), visitados));
                }
                return elementos.toString();
            }
            return valor.toString();
        } finally {
            visitados.remove(valor);
        }
    }

    // Evita que tabs, retornos o secuencias de control desarmen la tabla en la terminal.
    // Los saltos de línea se conservan como filas visuales dentro de la misma celda.
    private static String textoSeguro(String texto) {
        StringBuilder seguro = new StringBuilder();
        texto.codePoints().forEach(cp -> {
            if (cp == '\n') seguro.append('\n');
            else if (cp == '\t') seguro.append("\\t");
            else if (cp == '\r') seguro.append("\\r");
            else if (cp == '|') seguro.append("\\u007c");
            else if (Character.isISOControl(cp)) seguro.append(String.format("\\u%04x", cp));
            else seguro.appendCodePoint(cp);
        });
        return seguro.toString();
    }

    private static int ancho(String texto) {
        return texto.lines().mapToInt(linea -> linea.codePointCount(0, linea.length())).max().orElse(0);
    }

    private static String borde(int[] anchos) {
        StringBuilder borde = new StringBuilder("+");
        for (int ancho : anchos) borde.append("-".repeat(ancho + 2)).append('+');
        return borde.append('\n').toString();
    }

    private static void agregarFila(StringBuilder salida, List<String> valores, int[] anchos) {
        List<List<String>> lineas = new ArrayList<>();
        int altura = 1;
        for (int i = 0; i < valores.size(); i++) {
            List<String> partes = envolver(valores.get(i), anchos[i]);
            lineas.add(partes);
            altura = Math.max(altura, partes.size());
        }
        for (int linea = 0; linea < altura; linea++) {
            salida.append('|');
            for (int columna = 0; columna < valores.size(); columna++) {
                List<String> partes = lineas.get(columna);
                String parte = linea < partes.size() ? partes.get(linea) : "";
                int largo = parte.codePointCount(0, parte.length());
                salida.append(' ').append(parte).append(" ".repeat(anchos[columna] - largo + 1)).append('|');
            }
            salida.append('\n');
        }
    }

    private static List<String> envolver(String texto, int ancho) {
        List<String> lineas = new ArrayList<>();
        for (String linea : texto.split("\n", -1)) {
            if (linea.isEmpty()) {
                lineas.add("");
                continue;
            }
            int inicio = 0;
            while (inicio < linea.length()) {
                int cantidad = Math.min(ancho, linea.codePointCount(inicio, linea.length()));
                int fin = linea.offsetByCodePoints(inicio, cantidad);
                lineas.add(linea.substring(inicio, fin));
                inicio = fin;
            }
        }
        return lineas.isEmpty() ? Collections.singletonList("") : lineas;
    }
}
