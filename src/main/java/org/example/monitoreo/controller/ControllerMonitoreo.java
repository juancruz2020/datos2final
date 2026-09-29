package org.example.monitoreo.controller;

import org.example.monitoreo.service.MonitoreoService;

public class ControllerMonitoreo {

    private final MonitoreoService serviceMonitoreo;

    public ControllerMonitoreo() {
        this.serviceMonitoreo = new MonitoreoService();
    }

    public void crearTablas() {

        serviceMonitoreo.crearTablas();

        System.out.println("Tablas de monitoreo creadas correctamente.");
    }

    public void cargarDatosDePrueba() {

        serviceMonitoreo.cargarDatosDePrueba();

        System.out.println("Datos de prueba de monitoreo cargados correctamente.");
    }
}