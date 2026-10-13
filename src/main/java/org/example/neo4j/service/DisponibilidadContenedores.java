package org.example.neo4j.service;

import org.bson.Document;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/** Coordina reservas MongoDB con la escritura del envío en Neo4j. */
public class DisponibilidadContenedores {
    public interface Repositorio {
        Document reservar(String id, String envio, boolean propio);
        void restaurar(Document anterior, String envio);
        void liberar(String id, String envio);
        boolean ocupadoPorOtro(String id, String envio);
    }

    private final Repositorio repositorio;

    public DisponibilidadContenedores(Repositorio repositorio) {
        this.repositorio = repositorio;
    }

    public static boolean ocupa(String estado) {
        return "EN_TRANSITO".equals(estado) || "DEMORADO".equals(estado);
    }

    public void guardar(String envio, List<String> anteriores, List<String> seleccionados,
                        String estado, Runnable escritura) {
        List<Document> reservas = new ArrayList<>();
        try {
            for (String id : new LinkedHashSet<>(seleccionados)) {
                boolean propio = anteriores.contains(id);
                if (repositorio.ocupadoPorOtro(id, envio)) {
                    throw new IllegalArgumentException("El contenedor " + id + " ya está en uso en otro envío.");
                }
                Document anterior = repositorio.reservar(id, envio, propio);
                if (anterior == null) {
                    throw new IllegalArgumentException("El contenedor " + id + " no está disponible.");
                }
                reservas.add(anterior);
            }
            escritura.run();
        } catch (RuntimeException error) {
            for (Document anterior : reservas) {
                try {
                    repositorio.restaurar(anterior, envio);
                } catch (RuntimeException restauracion) {
                    error.addSuppressed(restauracion);
                }
            }
            throw error;
        }
        LinkedHashSet<String> liberar = new LinkedHashSet<>(anteriores);
        if (ocupa(estado)) {
            liberar.removeAll(seleccionados);
        } else {
            liberar.addAll(seleccionados);
        }
        liberar(envio, new ArrayList<>(liberar));
    }

    public void liberar(String envio, List<String> ids) {
        for (String id : new LinkedHashSet<>(ids)) {
            if (!repositorio.ocupadoPorOtro(id, envio)) {
                repositorio.liberar(id, envio);
            }
        }
    }
}
