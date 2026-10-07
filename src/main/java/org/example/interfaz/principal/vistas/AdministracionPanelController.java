package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.ClienteController;
import org.example.mongoDB.controller.RolController;
import org.example.mongoDB.controller.UsuarioController;
import org.example.usuarios.controller.AdministracionController;
import org.example.usuarios.controller.SesionController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Map;

public class AdministracionPanelController {

    private final AdministracionPanel view;

    private final AdministracionController administracionController;

    private final UsuarioController usuarioController;

    private final RolController rolController;

    private final ClienteController clienteController;

    private final SesionController sesionController;

    private Timer timer;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AdministracionPanelController(
            AdministracionPanel view
    ) {

        this.view = view;

        this.administracionController =
                new AdministracionController();

        this.usuarioController =
                new UsuarioController();

        this.rolController =
                new RolController();

        this.clienteController =
                new ClienteController();

        this.sesionController =
                new SesionController();


        configurarEventos();

        cargarRoles();

        actualizarFormularioCliente();

        cargarSesiones();

        iniciarActualizacionAutomatica();
    }


    // =========================================================
    // EVENTOS
    // =========================================================

    private void configurarEventos() {

        // -----------------------------------------------------
        // SESIONES
        // -----------------------------------------------------

        view.getBtnActualizarSesiones()
                .addActionListener(
                        e -> actualizarSesionesManualmente()
                );


        view.getBtnCerrarSesion()
                .addActionListener(
                        e -> cerrarSesionSeleccionada()
                );


        // -----------------------------------------------------
        // ROL
        // -----------------------------------------------------

        view.getCmbRol()
                .addActionListener(
                        e -> actualizarFormularioCliente()
                );


        // -----------------------------------------------------
        // USUARIOS
        // -----------------------------------------------------

        view.getBtnRegistrar()
                .addActionListener(
                        e -> registrarUsuario()
                );


        view.getBtnEliminar()
                .addActionListener(
                        e -> eliminarUsuario()
                );
    }


    // =========================================================
    // CARGAR ROLES
    // =========================================================

    private void cargarRoles() {

        try {

            List<Document> roles =
                    rolController.listarRoles();


            view.getCmbRol()
                    .removeAllItems();


            for (Document rol : roles) {

                String descripcion =
                        rol.getString(
                                "descripcion"
                        );


                if (descripcion != null) {

                    view.getCmbRol()
                            .addItem(
                                    descripcion
                            );
                }
            }


        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar los roles.",
                    e
            );
        }
    }


    // =========================================================
    // MOSTRAR / OCULTAR FORMULARIO CLIENTE
    // =========================================================

    private void actualizarFormularioCliente() {

        Object seleccionado =
                view.getCmbRol()
                        .getSelectedItem();


        if (seleccionado == null) {

            view.ocultarDatosCliente();

            return;
        }


        String rol =
                seleccionado
                        .toString()
                        .trim();


        if (
                rol.equalsIgnoreCase(
                        "Cliente"
                )
        ) {

            view.mostrarDatosCliente();

        } else {

            view.ocultarDatosCliente();

            view.limpiarDatosCliente();
        }
    }


    // =========================================================
    // REGISTRAR USUARIO
    // =========================================================

    private void registrarUsuario() {

        String nombre =
                view.getTxtNombre()
                        .getText()
                        .trim();

        String apellido =
                view.getTxtApellido()
                        .getText()
                        .trim();

        String email =
                view.getTxtEmail()
                        .getText()
                        .trim();

        String contrasena =
                new String(
                        view.getTxtContrasena()
                                .getPassword()
                ).trim();

        Object rolSeleccionado =
                view.getCmbRol()
                        .getSelectedItem();


        if (rolSeleccionado == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná un rol.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        String descripcionRol =
                rolSeleccionado
                        .toString()
                        .trim();


        try {

            // =================================================
            // BUSCAR ROL
            // =================================================

            Document rol =
                    rolController.buscarPorDescripcion(
                            descripcionRol
                    );


            if (rol == null) {

                throw new IllegalArgumentException(
                        "No se encontró el rol seleccionado."
                );
            }


            ObjectId rolObjectId =
                    rol.getObjectId(
                            "_id"
                    );


            if (rolObjectId == null) {

                throw new IllegalArgumentException(
                        "El rol seleccionado no tiene un ID válido."
                );
            }


            String rolId =
                    rolObjectId.toHexString();


            // =================================================
            // CLIENTE
            // =================================================

            String clienteId =
                    null;


            if (
                    descripcionRol.equalsIgnoreCase(
                            "Cliente"
                    )
            ) {

                clienteId =
                        crearClienteYObtenerId();
            }


            // =================================================
            // CREAR USUARIO
            // =================================================

            usuarioController.crearUsuario(
                    clienteId,
                    rolId,
                    nombre,
                    apellido,
                    email,
                    contrasena
            );


            // =================================================
            // ÉXITO
            // =================================================

            JOptionPane.showMessageDialog(
                    view,
                    descripcionRol.equalsIgnoreCase("Cliente")
                            ? "Cliente y usuario registrados correctamente."
                            : "Usuario registrado correctamente.",
                    "Administración",
                    JOptionPane.INFORMATION_MESSAGE
            );


            limpiarFormularioUsuario();

            view.limpiarDatosCliente();

            actualizarFormularioCliente();


        } catch (Exception e) {

            mostrarError(
                    "No se pudo registrar el usuario.",
                    e
            );
        }
    }


    // =========================================================
    // CREAR CLIENTE Y OBTENER ID
    // =========================================================

    private String crearClienteYObtenerId() {

        String razonSocial =
                view.getTxtRazonSocialCliente()
                        .getText()
                        .trim();

        String cuit =
                view.getTxtCuitCliente()
                        .getText()
                        .trim();

        String email =
                view.getTxtEmailCliente()
                        .getText()
                        .trim();

        String telefono =
                view.getTxtTelefonoCliente()
                        .getText()
                        .trim();

        String calle =
                view.getTxtCalleCliente()
                        .getText()
                        .trim();

        String numero =
                view.getTxtNumeroCliente()
                        .getText()
                        .trim();

        String ciudad =
                view.getTxtCiudadCliente()
                        .getText()
                        .trim();

        String codigoPostal =
                view.getTxtCodigoPostalCliente()
                        .getText()
                        .trim();

        String pais =
                view.getTxtPaisCliente()
                        .getText()
                        .trim();


        // =====================================================
        // CREAR CLIENTE
        // =====================================================

        clienteController.crearCliente(
                razonSocial,
                cuit,
                email,
                telefono,
                calle,
                numero,
                ciudad,
                codigoPostal,
                pais
        );


        // =====================================================
        // BUSCAR CLIENTE RECIÉN CREADO
        // =====================================================

        Document cliente =
                clienteController.buscarPorCuit(
                        cuit
                );


        if (cliente == null) {

            throw new IllegalArgumentException(
                    "No se pudo recuperar el cliente recién creado."
            );
        }


        ObjectId clienteId =
                cliente.getObjectId(
                        "_id"
                );


        if (clienteId == null) {

            throw new IllegalArgumentException(
                    "El cliente creado no tiene un ID válido."
            );
        }


        return clienteId.toHexString();
    }


    // =========================================================
    // ELIMINAR USUARIO
    // =========================================================

    private void eliminarUsuario() {

        String email =
                view.getTxtEmail()
                        .getText()
                        .trim();


        if (email.isEmpty()) {

            JOptionPane.showMessageDialog(
                    view,
                    "Ingresá el email del usuario.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        try {

            Document usuario =
                    usuarioController.buscarPorEmail(
                            email
                    );


            if (usuario == null) {

                throw new IllegalArgumentException(
                        "No se encontró un usuario con ese email."
                );
            }


            ObjectId usuarioId =
                    usuario.getObjectId(
                            "_id"
                    );


            if (usuarioId == null) {

                throw new IllegalArgumentException(
                        "El usuario no tiene un ID válido."
                );
            }


            int respuesta =
                    JOptionPane.showConfirmDialog(
                            view,
                            "¿Querés eliminar este usuario?",
                            "Eliminar usuario",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );


            if (
                    respuesta !=
                            JOptionPane.YES_OPTION
            ) {

                return;
            }


            usuarioController.eliminarUsuario(
                    usuarioId.toHexString()
            );


            JOptionPane.showMessageDialog(
                    view,
                    "Usuario eliminado correctamente.",
                    "Administración",
                    JOptionPane.INFORMATION_MESSAGE
            );


            limpiarFormularioUsuario();


        } catch (Exception e) {

            mostrarError(
                    "No se pudo eliminar el usuario.",
                    e
            );
        }
    }


    // =========================================================
    // CARGAR SESIONES
    // =========================================================

    private void cargarSesiones() {

        try {

            JTable tabla =
                    view.getTablaSesiones();


            String usuarioSeleccionado =
                    obtenerUsuarioSeleccionado();


            Map<String, Long> sesiones =
                    administracionController
                            .obtenerSesionesActivas();


            DefaultTableModel modelo =
                    (DefaultTableModel)
                            tabla.getModel();


            modelo.setRowCount(
                    0
            );


            for (
                    Map.Entry<String, Long> entrada :
                    sesiones.entrySet()
            ) {

                modelo.addRow(
                        new Object[]{
                                entrada.getKey(),
                                formatearTiempo(
                                        entrada.getValue()
                                )
                        }
                );
            }


            restaurarSeleccion(
                    usuarioSeleccionado
            );


            view.getLblEstado()
                    .setText(
                            "Sesiones activas: "
                                    + sesiones.size()
                    );


        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar las sesiones.",
                    e
            );
        }
    }


    // =========================================================
    // ACTUALIZAR SESIONES MANUALMENTE
    // =========================================================

    private void actualizarSesionesManualmente() {

        try {

            int fila =
                    view.getTablaSesiones()
                            .getSelectedRow();


            if (fila == -1) {

                cargarSesiones();

                view.getLblEstado()
                        .setText(
                                "Sesiones actualizadas."
                        );

                return;
            }


            String usuario =
                    String.valueOf(
                            view.getTablaSesiones()
                                    .getValueAt(
                                            fila,
                                            0
                                    )
                    );


            // =================================================
            // REFRESH DE LA SESIÓN
            // =================================================

            sesionController.renovarSesion(
                    usuario
            );


            // =================================================
            // ACTUALIZAR TABLA
            // =================================================

            cargarSesiones();


            view.getLblEstado()
                    .setText(
                            "Sesión de "
                                    + usuario
                                    + " renovada."
                    );


        } catch (Exception e) {

            mostrarError(
                    "No se pudo renovar la sesión.",
                    e
            );
        }
    }


    // =========================================================
    // OBTENER USUARIO SELECCIONADO
    // =========================================================

    private String obtenerUsuarioSeleccionado() {

        JTable tabla =
                view.getTablaSesiones();


        int fila =
                tabla.getSelectedRow();


        if (fila == -1) {

            return null;
        }


        Object valor =
                tabla.getValueAt(
                        fila,
                        0
                );


        if (valor == null) {

            return null;
        }


        return valor.toString();
    }


    // =========================================================
    // RESTAURAR SELECCIÓN
    // =========================================================

    private void restaurarSeleccion(
            String usuarioSeleccionado
    ) {

        if (
                usuarioSeleccionado == null
        ) {

            return;
        }


        JTable tabla =
                view.getTablaSesiones();


        DefaultTableModel modelo =
                (DefaultTableModel)
                        tabla.getModel();


        for (
                int i = 0;
                i < modelo.getRowCount();
                i++
        ) {

            Object valor =
                    modelo.getValueAt(
                            i,
                            0
                    );


            if (
                    valor != null
                            && valor.toString()
                            .equals(
                                    usuarioSeleccionado
                            )
            ) {

                tabla.setRowSelectionInterval(
                        i,
                        i
                );

                return;
            }
        }
    }


    // =========================================================
    // ACTUALIZACIÓN AUTOMÁTICA
    // =========================================================

    private void iniciarActualizacionAutomatica() {

        timer =
                new Timer(
                        1000,
                        e -> actualizarTiempoSesiones()
                );


        timer.start();
    }


    // =========================================================
    // ACTUALIZAR TIEMPO DE SESIONES
    // =========================================================

    private void actualizarTiempoSesiones() {

        try {

            JTable tabla =
                    view.getTablaSesiones();


            Map<String, Long> sesiones =
                    administracionController
                            .obtenerSesionesActivas();


            DefaultTableModel modelo =
                    (DefaultTableModel)
                            tabla.getModel();


            // -------------------------------------------------
            // ACTUALIZAR TTL
            // -------------------------------------------------

            for (
                    int fila = 0;
                    fila < modelo.getRowCount();
                    fila++
            ) {

                Object valorUsuario =
                        modelo.getValueAt(
                                fila,
                                0
                        );


                if (valorUsuario == null) {

                    continue;
                }


                String usuario =
                        valorUsuario.toString();


                Long segundos =
                        sesiones.get(
                                usuario
                        );


                if (segundos != null) {

                    modelo.setValueAt(
                            formatearTiempo(
                                    segundos
                            ),
                            fila,
                            1
                    );
                }
            }


            // -------------------------------------------------
            // ELIMINAR SESIONES EXPIRADAS
            // -------------------------------------------------

            for (
                    int fila = modelo.getRowCount() - 1;
                    fila >= 0;
                    fila--
            ) {

                Object valorUsuario =
                        modelo.getValueAt(
                                fila,
                                0
                        );


                if (valorUsuario == null) {

                    continue;
                }


                String usuario =
                        valorUsuario.toString();


                if (
                        !sesiones.containsKey(
                                usuario
                        )
                ) {

                    modelo.removeRow(
                            fila
                    );
                }
            }


            view.getLblEstado()
                    .setText(
                            "Sesiones activas: "
                                    + sesiones.size()
                    );


        } catch (Exception e) {

            // No mostrar un JOptionPane cada segundo.
        }
    }


    // =========================================================
    // FORMATEAR TIEMPO
    // =========================================================

    private String formatearTiempo(
            long segundos
    ) {

        if (segundos < 0) {

            return "Expirada";
        }


        long minutos =
                segundos / 60;


        long segundosRestantes =
                segundos % 60;


        return String.format(
                "%02d:%02d",
                minutos,
                segundosRestantes
        );
    }


    // =========================================================
    // LIMPIAR FORMULARIO
    // =========================================================

    private void limpiarFormularioUsuario() {

        view.getTxtNombre()
                .setText("");

        view.getTxtApellido()
                .setText("");

        view.getTxtEmail()
                .setText("");

        view.getTxtContrasena()
                .setText("");

        view.getCmbRol()
                .setSelectedIndex(-1);
    }


    // =========================================================
    // ERROR
    // =========================================================

    private void mostrarError(
            String mensaje,
            Exception e
    ) {

        JOptionPane.showMessageDialog(
                view,
                mensaje
                        + "\n\n"
                        + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void cerrarSesionSeleccionada() {

        int fila =
                view.getTablaSesiones()
                        .getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    view,
                    "Seleccioná una sesión.",
                    "Administración",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String usuario =
                String.valueOf(
                        view.getTablaSesiones()
                                .getValueAt(
                                        fila,
                                        0
                                )
                );

        int respuesta =
                JOptionPane.showConfirmDialog(
                        view,
                        "¿Querés cerrar la sesión de \""
                                + usuario
                                + "\"?",
                        "Cerrar sesión",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            administracionController.cerrarSesion(
                    usuario
            );

            cargarSesiones();

            JOptionPane.showMessageDialog(
                    view,
                    "La sesión de "
                            + usuario
                            + " fue cerrada.",
                    "Sesión cerrada",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {

            mostrarError(
                    "No se pudo cerrar la sesión.",
                    e
            );
        }
    }
}