
package org.example;

import org.example.DatosPorDefecto.DatosPruebaTotal;
import org.example.DatosPorDefecto.EliminarDatos;
import org.example.interfaz.app.App;

public class Main {

    public static void main(String[] args) {

        // Descomentar únicamente cuando se necesite
        // eliminar los datos de prueba.
        EliminarDatos.eliminarDatosDePrueba();

        DatosPruebaTotal datos =
                new DatosPruebaTotal();

        datos.cargarDatos();

        App.iniciar();
    }
}
