package org.example.DatosPorDefecto;

import com.datastax.oss.driver.api.core.CqlSession;
import org.example.conecciones.CassandraSingleton;
import org.example.logging.AppLog;

public class CrearTablaAlertas {

    public void crearTabla() {
        CqlSession session = CassandraSingleton.getInstance();
        session.execute("""
                CREATE TABLE IF NOT EXISTS logistica.alertas (
                    id text PRIMARY KEY,
                    nombre text,
                    descripcion text
                )
                """);

        AppLog.info(CrearTablaAlertas.class,
                "Tabla de alertas Cassandra creada o verificada.");
    }
}
