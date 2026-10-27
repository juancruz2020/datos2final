package org.example.mongoDB.controller;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

/** Pruebas ejecutables con main; no consultan bases ni abren la interfaz gráfica. */
public class ControllerMongoDBTest {

    public static void main(String[] args) throws Exception {
        List<String> internos;
        try (var archivos = Files.list(Path.of("src/main/java/org/example/mongoDB/controller"))) {
            internos = archivos.map(p -> p.getFileName().toString())
                    .filter(nombre -> nombre.endsWith("Controller.java"))
                    .map(nombre -> nombre.substring(0, nombre.length() - 5))
                    .sorted().toList();
        }
        comprobar(!internos.isEmpty(), "Debe haber controllers internos para verificar");
        construccionSinDependencias();
        accesoExternoBloqueado(internos);
        System.out.println("ControllerMongoDB: construcción sin dependencias y acceso externo bloqueado para "
                + internos.size() + " controllers. Sin conexiones a bases.");
    }

    private static void construccionSinDependencias() throws Exception {
        ControllerMongoDB central = new ControllerMongoDB();
        comprobar(Modifier.isPublic(ControllerMongoDB.class.getModifiers()), "La fachada debe ser pública");
        for (Field campo : ControllerMongoDB.class.getDeclaredFields()) {
            comprobar(Modifier.isPrivate(campo.getModifiers()), "No exponer las dependencias: " + campo.getName());
            campo.setAccessible(true);
            Object proveedor = campo.get(central);
            Field instancia = proveedor.getClass().getDeclaredField("instancia");
            instancia.setAccessible(true);
            comprobar(instancia.get(proveedor) == null,
                    "El constructor no debe crear dependencias ni el ciclo Mongo/Neo4j: " + campo.getName());
        }
    }

    private static void accesoExternoBloqueado(List<String> internos) throws Exception {
        StringBuilder codigo = new StringBuilder("package consumidor;\n");
        for (String interno : internos) {
            Class<?> tipo = Class.forName("org.example.mongoDB.controller." + interno);
            comprobar(!Modifier.isPublic(tipo.getModifiers()), "Controller interno expuesto: " + interno);
            codigo.append("import org.example.mongoDB.controller.").append(interno).append(";\n");
        }
        codigo.append("class AccesoDirecto {}\n");
        JavaFileObject fuente = new SimpleJavaFileObject(URI.create("string:///consumidor/AccesoDirecto.java"),
                JavaFileObject.Kind.SOURCE) {
            @Override public CharSequence getCharContent(boolean ignorarErrores) {
                return codigo;
            }
        };
        JavaCompiler compilador = ToolProvider.getSystemJavaCompiler();
        comprobar(compilador != null, "Ejecutar la prueba con el JDK 21");
        DiagnosticCollector<JavaFileObject> diagnosticos = new DiagnosticCollector<>();
        Path salida = Files.createTempDirectory("prueba-central-mongo-");
        try (var archivos = compilador.getStandardFileManager(diagnosticos, null, null)) {
            boolean permitido = compilador.getTask(null, archivos, diagnosticos,
                    List.of("-proc:none", "--release", "21", "-classpath", System.getProperty("java.class.path"),
                            "-d", salida.toString()), null, List.of(fuente)).call();
            comprobar(!permitido, "Java debe impedir imports directos de los controllers internos");
            long errores = diagnosticos.getDiagnostics().stream()
                    .filter(d -> d.getKind() == Diagnostic.Kind.ERROR).count();
            comprobar(errores >= internos.size(), "Comprobar el bloqueo de todos los controllers internos");
        } finally {
            try (var archivos = Files.walk(salida)) {
                for (Path archivo : archivos.sorted(Comparator.reverseOrder()).toList()) Files.delete(archivo);
            }
        }
    }

    private static void comprobar(boolean cumple, String mensaje) {
        if (!cumple) throw new AssertionError(mensaje);
    }
}
