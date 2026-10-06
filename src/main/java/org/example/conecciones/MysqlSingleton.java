package org.example.conecciones;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlSingleton {

    private static Connection instancia;

    private MysqlSingleton() {
    }

    public static Connection getInstance() {

        if (instancia == null) {
            try {

                Dotenv dotenv = Dotenv.load();

                String host = dotenv.get("MYSQL_HOST");
                String port = dotenv.get("MYSQL_PORT");
                String database = dotenv.get("MYSQL_DATABASE");
                String user = dotenv.get("MYSQL_USER");
                String password = dotenv.get("MYSQL_PASSWORD");

                String url =
                        "jdbc:mysql://"
                                + host
                                + ":"
                                + port
                                + "/"
                                + database
                                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

                instancia = DriverManager.getConnection(
                        url,
                        user,
                        password
                );

                System.out.println(
                        "Conexión a MySQL establecida correctamente."
                );

            } catch (SQLException e) {

                throw new RuntimeException(
                        "Error al conectar con MySQL: "
                                + e.getMessage(),
                        e
                );
            }
        }

        return instancia;
    }

    public static void cerrarConexion() {

        if (instancia != null) {

            try {
                instancia.close();

                instancia = null;

                System.out.println(
                        "Conexión a MySQL cerrada."
                );

            } catch (SQLException e) {

                System.err.println(
                        "Error al cerrar conexión MySQL: "
                                + e.getMessage()
                );
            }
        }
    }
}