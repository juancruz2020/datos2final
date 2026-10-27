package org.example.conecciones;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.github.cdimascio.dotenv.Dotenv;
import org.example.logging.AppLog;

public class MongoSingleton {

    private static MongoClient client;

    private MongoSingleton() {
        // Evita crear objetos de esta clase
    }

    public static MongoClient getInstance() {

        if (client == null) {

            Dotenv dotenv = Dotenv.load();

            String uri = dotenv.get("MONGO_URI");

            try {
                client = MongoClients.create(uri);
                AppLog.info(MongoSingleton.class,
                        "Cliente MongoDB creado; la conexión se establece bajo demanda.");
            } catch (RuntimeException e) {
                AppLog.error(MongoSingleton.class,
                        "No se pudo inicializar el cliente MongoDB.", e);
                throw e;
            }
        }

        return client;
    }

    public static void close() {

        if (client != null) {
            client.close();
            client = null;
            AppLog.info(MongoSingleton.class, "Cliente MongoDB cerrado.");
        }
    }
}
