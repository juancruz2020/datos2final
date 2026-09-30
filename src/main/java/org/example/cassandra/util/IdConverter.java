package org.example.cassandra.util;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class IdConverter {

    private IdConverter() {
        // Evita instanciar esta clase
    }

    public static UUID mongoIdToUuid(String mongoId) {

        if (mongoId == null || mongoId.isBlank()) {
            throw new IllegalArgumentException(
                    "El ID de Mongo no puede ser null o vacío"
            );
        }

        return UUID.nameUUIDFromBytes(
                mongoId.getBytes(StandardCharsets.UTF_8)
        );
    }
}