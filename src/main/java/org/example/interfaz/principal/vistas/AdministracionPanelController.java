package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.ClienteController;
import org.example.mongoDB.controller.RolController;
import org.example.mongoDB.controller.UsuarioController;
import org.example.usuarios.controller.AdministracionController;

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
                        e -> cargarSesiones()
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

            throw new IllegalStateException(
                    "El cliente fue creado pero no pudo recuperarse."
            );
        }


        ObjectId clienteObjectId =
                cliente.getObjectId(
                        "_id"
                );


        if (clienteObjectId == null) {

            throw new IllegalStateException(
                    "El cliente creado no tiene un ID válido."
            );
        }


        return clienteObjectId.toHexString();
    }


    // =========================================================
    // LIMPIAR FORMULARIO USUARIO
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
                    "Ingresá el email del usuario que querés eliminar.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        int respuesta =
                JOptionPane.showConfirmDialog(
                        view,
                        "¿Querés eliminar al usuario \""
                                + email
                                + "\"?",
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


        try {

            Document usuario =
                    usuarioController.buscarPorEmail(
                            email
                    );


            if (usuario == null) {

                throw new IllegalArgumentException(
                        "No existe un usuario con ese email."
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

            Map<String, Long> sesiones =
                    administracionController
                            .obtenerSesionesActivas();


            DefaultTableModel modelo =
                    (DefaultTableModel)
                            view.getTablaSesiones()
                                    .getModel();


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
    // CERRAR SESIÓN SELECCIONADA
    // =========================================================

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


        if (
                respuesta !=
                        JOptionPane.YES_OPTION
        ) {

            return;
        }


        try {

            administracionController
                    .cerrarSesion(
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


    // =========================================================
    // ACTUALIZACIÓN AUTOMÁTICA
    // =========================================================

    private void iniciarActualizacionAutomatica() {

        timer =
                new Timer(
                        1000,
                        e -> cargarSesiones()
                );


        timer.start();
    }


    // =========================================================
    // FORMATEAR TTL
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
}