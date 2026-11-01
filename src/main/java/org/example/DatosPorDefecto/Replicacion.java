
package org.example.DatosPorDefecto;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.conecciones.MongoSingleton;
import org.example.logging.AppLog;

import java.util.List;

public class Replicacion {

    private static final String REPLICA_SET = "rs0";
    private static final String HOST = "localhost:27017";

    private static final int MAX_INTENTOS = 30;
    private static final long ESPERA_MS = 2000;

    private Replicacion() {
    }

    public static void inicializar() {

        MongoClient client = MongoSingleton.getInstance();
        MongoDatabase admin = client.getDatabase("admin");

        AppLog.info(Replicacion.class,
                "Iniciando comprobación del Replica Set.");

        esperarMongoDB(admin);

        if (!estaInicializado(admin)) {
            iniciarReplicaSet(admin);
        }

        esperarPrimary(admin);

        AppLog.info(Replicacion.class,
                "Replica Set rs0 inicializado correctamente. PRIMARY disponible.");
    }

    private static void esperarMongoDB(MongoDatabase admin) {

        for (int i = 1; i <= MAX_INTENTOS; i++) {
            try {
                admin.runCommand(new Document("ping", 1));

                AppLog.info(Replicacion.class,
                        "MongoDB está disponible.");

                return;

            } catch (MongoException e) {

                AppLog.warning(Replicacion.class,
                        "Esperando MongoDB. Intento "
                                + i + "/" + MAX_INTENTOS);

                esperar();
            }
        }

        throw new IllegalStateException(
                "MongoDB no respondió después de "
                        + MAX_INTENTOS + " intentos."
        );
    }

    private static boolean estaInicializado(MongoDatabase admin) {

        try {
            admin.runCommand(
                    new Document("replSetGetStatus", 1)
            );

            AppLog.info(Replicacion.class,
                    "El Replica Set ya está inicializado.");

            return true;

        } catch (MongoException e) {

            // Código 94: NotYetInitialized
            if (e.getCode() == 94) {
                return false;
            }

            throw e;
        }
    }

    private static void iniciarReplicaSet(MongoDatabase admin) {

        Document miembro = new Document("_id", 0)
                .append("host", HOST);

        Document configuracion = new Document("_id", REPLICA_SET)
                .append("members", List.of(miembro));

        try {

            admin.runCommand(
                    new Document("replSetInitiate", configuracion)
            );

            AppLog.info(Replicacion.class,
                    "Solicitud de inicialización del Replica Set enviada.");

        } catch (MongoException e) {

            // Código 23: AlreadyInitialized
            if (e.getCode() == 23) {

                AppLog.info(Replicacion.class,
                        "El Replica Set ya estaba inicializado.");

                return;
            }

            throw e;
        }
    }

    private static void esperarPrimary(MongoDatabase admin) {

        AppLog.info(Replicacion.class,
                "Esperando que MongoDB alcance el estado PRIMARY.");

        for (int i = 1; i <= MAX_INTENTOS; i++) {

            try {

                Document respuesta = admin.runCommand(
                        new Document("hello", 1)
                );

                Boolean primary = respuesta.getBoolean(
                        "isWritablePrimary"
                );

                if (Boolean.TRUE.equals(primary)) {

                    AppLog.info(Replicacion.class,
                            "MongoDB está funcionando como PRIMARY.");

                    return;
                }

            } catch (MongoException e) {

                AppLog.warning(Replicacion.class,
                        "Todavía no se pudo consultar el estado PRIMARY.");
            }

            esperar();
        }

        throw new IllegalStateException(
                "MongoDB no consiguió convertirse en PRIMARY después de "
                        + MAX_INTENTOS + " intentos."
        );
    }

    private static void esperar() {

        try {
            Thread.sleep(ESPERA_MS);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Se interrumpió la inicialización del Replica Set.",
                    e
            );
        }
    }
}
