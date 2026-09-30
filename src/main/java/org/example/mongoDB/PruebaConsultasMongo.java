package org.example.mongoDB;

import org.bson.Document;
import org.example.mongoDB.controller.ControllerMongoDB;

import java.util.List;

public class PruebaConsultasMongo {

    public static void main(String[] args) {

        ControllerMongoDB controller = new ControllerMongoDB();

        System.out.println("=================================");
        System.out.println("TODOS LOS ENVIOS");
        System.out.println("=================================");

        List<Document> envios = controller.listarEnvios();

        for (Document envio : envios) {
            System.out.println(envio.toJson());
        }


        System.out.println();
        System.out.println("=================================");
        System.out.println("ENVIOS EN TRANSITO");
        System.out.println("=================================");

        List<Document> enTransito =
                controller.buscarEnviosPorEstado("EN_TRANSITO");

        for (Document envio : enTransito) {
            System.out.println(envio.toJson());
        }


        System.out.println();
        System.out.println("=================================");
        System.out.println("ENVIOS DEMORADOS");
        System.out.println("=================================");

        List<Document> demorados =
                controller.buscarEnviosDemorados();

        for (Document envio : demorados) {
            System.out.println(envio.toJson());
        }


        System.out.println();
        System.out.println("=================================");
        System.out.println("ENVIOS RELACIONADOS CON ARGENTINA");
        System.out.println("=================================");

        List<Document> argentina =
                controller.buscarEnviosPorPais("Argentina");

        for (Document envio : argentina) {
            System.out.println(envio.toJson());
        }


        System.out.println();
        System.out.println("=================================");
        System.out.println("INCIDENTES ABIERTOS");
        System.out.println("=================================");

        List<Document> incidentes =
                controller.buscarIncidentesAbiertos();

        for (Document incidente : incidentes) {
            System.out.println(incidente.toJson());
        }
    }
}