
package org.example;

import org.example.DatosPorDefecto.DatosPruebaTotal;
import org.example.DatosPorDefecto.EliminarDatos;
import org.example.interfaz.app.App;
import org.example.logging.AppLog;
import org.example.conecciones.CassandraSingleton;
import org.example.conecciones.MongoSingleton;
import org.example.conecciones.Neo4jSingleton;
import org.example.conecciones.RedisSingleton;

public class Main {

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            AppLog.info(Main.class, "Cerrando conexiones de bases de datos.");
            RedisSingleton.close();
            CassandraSingleton.close();
            Neo4jSingleton.close();
            MongoSingleton.close();
        }, "cerrar-conexiones"));

        AppLog.info(Main.class, "Iniciando aplicación.");
        try {

            // Este reset ya formaba parte del arranque; se registra porque
            // elimina los datos actuales de todos los motores.
            AppLog.warning(Main.class,
                    "El inicio ejecutará el reseteo de datos de prueba.");
            EliminarDatos.eliminarDatosDePrueba();

            DatosPruebaTotal datos =
                    new DatosPruebaTotal();

            datos.cargarDatos();

            App.iniciar();
        } catch (RuntimeException e) {
            AppLog.error(Main.class, "No se pudo completar el inicio.", e);
            throw e;
        }
    }
}
