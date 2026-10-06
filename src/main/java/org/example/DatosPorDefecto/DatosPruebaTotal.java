package org.example.DatosPorDefecto;

import org.example.mongoDB.controller.*;

import java.util.List;

public class DatosPruebaTotal {

    ControllerMongoDB mongo = new ControllerMongoDB();

    List<String> idsMongo = mongo.obtenerIdsSensores();


}
