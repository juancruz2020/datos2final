package org.example;

import org.example.interfaz.app.App;
import org.example.mongoDB.GeneradorDatosPrueba;

public class Main {

    public static void main(String[] args) {

        // Generar datos de prueba en MongoDB
        GeneradorDatosPrueba generador = new GeneradorDatosPrueba();
        generador.generarDatos();
        App.iniciar();
    }
}