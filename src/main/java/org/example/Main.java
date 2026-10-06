package org.example;

import org.example.interfaz.app.App;

public class Main {

    public static void main(String[] args) {

        // Para cargar datos de prueba manualmente,
        // descomentar estas dos líneas:
        //
        //GeneradorDatosPrueba generador = new GeneradorDatosPrueba();
        // generador.generarDatos();

        App.iniciar();
    }
}