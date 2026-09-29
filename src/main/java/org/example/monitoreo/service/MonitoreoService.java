package org.example.monitoreo.service;

import com.datastax.oss.driver.api.core.CqlSession;
import org.example.conecciones.CassandraSingleton;
import org.example.monitoreo.dao.CrearTablas;
import org.example.monitoreo.dao.DatosDePrueba;

public class MonitoreoService {

    private final CrearTablas crearTablas;
    private final DatosDePrueba datosDePrueba;

    public MonitoreoService() {
        this.crearTablas = new CrearTablas();
        this.datosDePrueba = new DatosDePrueba();
    }

    public void crearTablas() {

        CqlSession session = CassandraSingleton.getInstance();

        crearTablas.crearTablas(session);
    }
    public void cargarDatosDePrueba() {

        CqlSession session = CassandraSingleton.getInstance();

        datosDePrueba.insertarDatos(session);
    }
}