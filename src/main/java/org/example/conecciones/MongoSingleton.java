package org.example.conecciones;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.github.cdimascio.dotenv.Dotenv;

public class MongoSingleton {

    private static MongoClient client;

    private MongoSingleton() {
        // Evita crear objetos de esta clase
    }

    public static MongoClient getInstance() {

        if (client == null) {

            Dotenv dotenv = Dotenv.load();

            String uri = dotenv.get("MONGO_URI");

            client = MongoClients.create(uri);
        }

        return client;
    }

    public static void close() {

        if (client != null) {
            client.close();
            client = null;
        }
    }
}