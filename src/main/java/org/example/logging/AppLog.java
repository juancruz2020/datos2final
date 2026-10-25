package org.example.logging;

import java.util.logging.Logger;

/** Registro uniforme de eventos relevantes de la aplicación en consola. */
public final class AppLog {

    private AppLog() {
    }

    public static void info(Class<?> origen, String mensaje) {
        Logger.getLogger(origen.getName()).info(mensaje);
    }

    public static void debug(Class<?> origen, String mensaje) {
        Logger.getLogger(origen.getName()).fine(mensaje);
    }

    public static void warning(Class<?> origen, String mensaje) {
        Logger.getLogger(origen.getName()).warning(mensaje);
    }

    public static void error(
            Class<?> origen,
            String mensaje,
            Throwable error
    ) {
        StringBuilder tipos = new StringBuilder();
        Throwable actual = error;
        int profundidad = 0;
        while (actual != null && profundidad < 4) {
            if (!tipos.isEmpty()) {
                tipos.append(" <- ");
            }
            tipos.append(actual.getClass().getSimpleName());
            actual = actual.getCause();
            profundidad++;
        }
        Logger.getLogger(origen.getName()).severe(
                mensaje + " [" + tipos + "]"
        );
    }
}
