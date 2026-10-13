package org.example.neo4j.service;

import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Pruebas ejecutables sin servicios externos: java -ea ...DisponibilidadContenedoresTest. */
public class DisponibilidadContenedoresTest {
    private static class Memoria implements DisponibilidadContenedores.Repositorio {
        final Map<String, Document> datos = new HashMap<>();
        boolean otro;
        void agregar(String id, String estado, String propietario) {
            Document d = new Document("_id", new ObjectId(id)).append("estado", estado);
            if (propietario != null) d.append("envio_asignado", propietario);
            datos.put(id, d);
        }
        public Document reservar(String id, String envio, boolean propio) {
            Document d = datos.get(id);
            if (d == null || ("EN_USO".equals(d.getString("estado"))
                    && !(propio && (d.get("envio_asignado") == null
                    || envio.equals(d.getString("envio_asignado")))))) return null;
            Document anterior = new Document(d);
            d.put("estado", "EN_USO"); d.put("envio_asignado", envio);
            return anterior;
        }
        public void restaurar(Document anterior, String envio) {
            datos.put(anterior.getObjectId("_id").toHexString(), anterior);
        }
        public void liberar(String id, String envio) {
            Document d = datos.get(id);
            if (d.get("envio_asignado") == null || envio.equals(d.get("envio_asignado"))) {
                d.put("estado", "DISPONIBLE"); d.remove("envio_asignado");
            }
        }
        public boolean ocupadoPorOtro(String id, String envio) { return otro; }
        String estado(String id) { return datos.get(id).getString("estado"); }
    }
    public static void main(String[] args) {
        String a = "000000000000000000000001", b = "000000000000000000000002";
        Memoria r = new Memoria();
        var servicio = new DisponibilidadContenedores(r);
        for (String estado : List.of("PENDIENTE", "ENTREGADO", "CANCELADO", "EN_TRANSITO", "DEMORADO")) {
            r.agregar(a, "DISPONIBLE", null);
            servicio.guardar("E1", List.of(), List.of(a), estado, () -> {});
            assert r.estado(a).equals(DisponibilidadContenedores.ocupa(estado) ? "EN_USO" : "DISPONIBLE");
        }
        // Conservar el propio contenedor al editar.
        servicio.guardar("E1", List.of(a), List.of(a), "EN_TRANSITO", () -> {});
        // Rechazar otro envío, sin ejecutar la escritura.
        try {
            servicio.guardar("E2", List.of(), List.of(a), "PENDIENTE", () -> { throw new AssertionError(); });
            throw new AssertionError("Debió rechazar el contenedor ocupado");
        } catch (IllegalArgumentException esperado) { }
        // Revertir todas las reservas si uno de los seleccionados está ocupado.
        r.agregar(b, "DISPONIBLE", null);
        try {
            servicio.guardar("E2", List.of(), List.of(b, a), "EN_TRANSITO", () -> { throw new AssertionError(); });
            throw new AssertionError();
        } catch (IllegalArgumentException esperado) { }
        assert r.estado(b).equals("DISPONIBLE");
        // Revertir si falla la escritura Neo4j.
        try {
            servicio.guardar("E2", List.of(), List.of(b), "EN_TRANSITO", () -> { throw new IllegalStateException(); });
            throw new AssertionError();
        } catch (IllegalStateException esperado) { }
        assert r.estado(b).equals("DISPONIBLE");
        // Cambio de contenedor: libera el anterior y ocupa el nuevo.
        servicio.guardar("E1", List.of(a), List.of(b), "DEMORADO", () -> {});
        assert r.estado(a).equals("DISPONIBLE") && r.estado(b).equals("EN_USO");
        // No liberar uno compartido por datos antiguos si otro envío sigue activo.
        r.otro = true;
        servicio.liberar("E1", List.of(b));
        assert r.estado(b).equals("EN_USO");
        r.otro = false;
        servicio.liberar("E1", List.of(b));
        assert r.estado(b).equals("DISPONIBLE");
        System.out.println("Pruebas de disponibilidad: OK");
    }
}
