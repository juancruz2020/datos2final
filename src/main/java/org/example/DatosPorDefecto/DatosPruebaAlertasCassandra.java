package org.example.DatosPorDefecto;

import com.datastax.oss.driver.api.core.CqlSession;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.conecciones.CassandraSingleton;
import org.example.logging.AppLog;

import java.util.List;

public class DatosPruebaAlertasCassandra {

    public void insertarDatos(List<Document> incidentes, List<Document> sensores) {
        CqlSession session = CassandraSingleton.getInstance();

        // Elimina IDs sintéticos generados por versiones anteriores de estos datos de prueba.
        var eliminarAnterior = session.prepare(
                "DELETE FROM logistica.alertas WHERE id = ?");
        for (String idAnterior : List.of(
                "ALT-SENSOR-NO-DISPONIBLE",
                "ALT-ENVIO-DEMORADO",
                "ALT-RUTA-BLOQUEADA",
                "ALT-INCIDENTE-MECANICO",
                "ALT-CONTROL-ADUANERO",
                "ALT-INCIDENTE-CLIMATICO",
                "00000000-0000-4000-8000-000000000001",
                "00000000-0000-4000-8000-000000000002",
                "00000000-0000-4000-8000-000000000003",
                "00000000-0000-4000-8000-000000000004",
                "00000000-0000-4000-8000-000000000005",
                "00000000-0000-4000-8000-000000000006")) {
            session.execute(eliminarAnterior.bind(idAnterior));
        }

        var insertar = session.prepare("""
                INSERT INTO logistica.alertas (id, nombre, descripcion)
                VALUES (?, ?, ?)
                """);

        int cantidad = 0;
        for (Document incidente : incidentes) {
            if (!"ABIERTO".equalsIgnoreCase(incidente.getString("estado"))) {
                continue;
            }
            String id = idMongo(incidente.get("_id"));
            if (id == null) continue;

            String tipo = valor(incidente, "tipo", "Incidente");
            String envio = valor(incidente, "envio_id", "sin envío asociado");
            String severidad = valor(incidente, "severidad", "sin severidad");
            String descripcion = valor(incidente, "descripcion", "Sin descripción.");
            session.execute(insertar.bind(
                    id,
                    "Incidente " + tipo,
                    "Envío " + envio + ". Severidad " + severidad + ". " + descripcion
            ));
            cantidad++;
        }

        for (Document sensor : sensores) {
            if (!"INACTIVO".equalsIgnoreCase(sensor.getString("estado"))) {
                continue;
            }
            String id = idMongo(sensor.get("_id"));
            if (id == null) continue;

            String codigo = valor(sensor, "codigo", id);
            String tipo = valor(sensor, "tipo", "sensor");
            String contenedor = idMongo(sensor.get("contenedor_id"));
            session.execute(insertar.bind(
                    id,
                    "Sensor inactivo",
                    "El sensor " + codigo + " (" + tipo + ")" +
                            (contenedor == null ? "" : " del contenedor " + contenedor) +
                            " está marcado como INACTIVO."
            ));
            cantidad++;
        }

        AppLog.info(DatosPruebaAlertasCassandra.class,
                "Se cargaron " + cantidad + " alertas vinculadas a incidentes abiertos y sensores inactivos de MongoDB.");
    }

    private String idMongo(Object id) {
        if (id instanceof ObjectId objectId) {
            return objectId.toHexString();
        }
        if (id instanceof String texto && ObjectId.isValid(texto)) {
            return texto;
        }
        return null;
    }

    private String valor(Document documento, String campo, String predeterminado) {
        String valor = documento.getString(campo);
        return valor == null || valor.isBlank() ? predeterminado : valor;
    }
}
