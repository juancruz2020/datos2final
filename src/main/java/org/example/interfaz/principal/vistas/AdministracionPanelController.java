package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.ClienteController;
import org.example.mongoDB.controller.RolController;
import org.example.mongoDB.controller.UsuarioController;
import org.example.usuarios.controller.AdministracionController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdministracionPanelController {

    private final AdministracionPanel view;

    private final AdministracionController administracionController;

    private final UsuarioController usuarioController;

    private final RolController rolController;

    private final ClienteController clienteController;


    /*
     * Guardamos la relación:
     *
     * descripción del rol -> ObjectId
     *
     * Así el usuario ve "Administrador",
     * "Operador" o "Cliente",
     * pero Mongo recibe el ID real.
     */
    private final Map<String, String> roles;


    /*
     * Guardamos la relación:
     *
     * razón social -> ObjectId
     */
    private final Map<String, String> clientes;


    private Timer timer;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AdministracionPanelController(
            AdministracionPanel view
    ) {

        this.view =
                view;

        this.administracionController =
                new AdministracionController();

        this.usuarioController =
                new UsuarioController();

        this.rolController =
                new RolController();

        this.clienteController =
                new ClienteController();

        this.roles =
                new HashMap<>();

        this.clientes =
                new HashMap<>();


        configurarEventos();

        cargarRoles();

        cargarClientes();

        actualizarEstadoCliente();

        cargarSesiones();

        iniciarActualizacionAutomatica();
    }


    // =========================================================
    // EVENTOS
    // =========================================================

    private void configurarEventos() {

        view.getBtnActualizarSesiones()
                .addActionListener(
                        e -> cargarSesiones()
                );

        view.getBtnCerrarSesion()
                .addActionListener(
                        e -> cerrarSesionSeleccionada()
                );

        view.getBtnRegistrar()
                .addActionListener(
                        e -> registrarUsuario()
                );

        view.getBtnEliminar()
                .addActionListener(
                        e -> eliminarUsuario()
                );

        view.getCmbRol()
                .addActionListener(
                        e -> actualizarEstadoCliente()
                );
    }


    // =========================================================
    // CARGAR ROLES
    // =========================================================

    private void cargarRoles() {

        try {

            List<Document> listaRoles =
                    rolController.listarRoles();

            roles.clear();

            view.getCmbRol()
                    .removeAllItems();


            for (Document rol : listaRoles) {

                ObjectId id =
                        rol.getObjectId(
                                "_id"
                        );

                String descripcion =
                        rol.getString(
                                "descripcion"
                        );


                if (id == null
                        || descripcion == null
                        || descripcion.isBlank()) {

                    continue;
                }


                roles.put(
                        descripcion,
                        id.toHexString()
                );


                view.getCmbRol()
                        .addItem(
                                descripcion
                        );
            }

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar los roles.",
                    e
            );
        }
    }


    // =========================================================
    // CARGAR CLIENTES
    // =========================================================

    private void cargarClientes() {

        try {

            List<Document> listaClientes =
                    clienteController
                            .listarClientes();

            clientes.clear();

            view.getCmbCliente()
                    .removeAllItems();


            // Opción vacía para ADMIN / OPERADOR

            view.getCmbCliente()
                    .addItem(
                            "Sin cliente"
                    );


            for (Document cliente : listaClientes) {

                ObjectId id =
                        cliente.getObjectId(
                                "_id"
                        );

                String razonSocial =
                        cliente.getString(
                                "razon_social"
                        );


                if (id == null
                        || razonSocial == null
                        || razonSocial.isBlank()) {

                    continue;
                }


                clientes.put(
                        razonSocial,
                        id.toHexString()
                );


                view.getCmbCliente()
                        .addItem(
                                razonSocial
                        );
            }

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar los clientes.",
                    e
            );
        }
    }


    // =========================================================
    // HABILITAR CLIENTE SEGÚN ROL
    // =========================================================

    private void actualizarEstadoCliente() {

        Object seleccionado =
                view.getCmbRol()
                        .getSelectedItem();


        boolean esCliente =
                seleccionado != null
                        &&
                "Cliente".equalsIgnoreCase(
                        seleccionado.toString()
                );


        view.getCmbCliente()
                .setEnabled(
                        esCliente
                );


        if (!esCliente
                && view.getCmbCliente()
                .getItemCount() > 0) {

            view.getCmbCliente()
                    .setSelectedIndex(0);
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

        String contraseña =
                new String(
                        view.getTxtPassword()
                                .getPassword()
                );


        Object rolSeleccionado =
                view.getCmbRol()
                        .getSelectedItem();


        if (rolSeleccionado == null) {

            mostrarValidacion(
                    "Seleccioná un rol."
            );

            return;
        }


        String descripcionRol =
                rolSeleccionado.toString();


        String rolId =
                roles.get(
                        descripcionRol
                );


        if (rolId == null) {

            mostrarValidacion(
                    "El rol seleccionado no es válido."
            );

            return;
        }


        String clienteId = null;


        // -----------------------------------------------------
        // CLIENTE OBLIGATORIO PARA ROL CLIENTE
        // -----------------------------------------------------

        if ("Cliente".equalsIgnoreCase(
                descripcionRol
        )) {

            Object clienteSeleccionado =
                    view.getCmbCliente()
                            .getSelectedItem();


            if (clienteSeleccionado == null
                    ||
                    "Sin cliente".equals(
                            clienteSeleccionado.toString()
                    )) {

                mostrarValidacion(
                        "Seleccioná el cliente al que pertenece el usuario."
                );

                return;
            }


            clienteId =
                    clientes.get(
                            clienteSeleccionado.toString()
                    );


            if (clienteId == null) {

                mostrarValidacion(
                        "El cliente seleccionado no es válido."
                );

                return;
            }
        }


        try {

            usuarioController.crearUsuario(
                    clienteId,
                    rolId,
                    nombre,
                    apellido,
                    email,
                    contraseña
            );


            JOptionPane.showMessageDialog(
                    view,
                    "Usuario registrado correctamente.",
                    "Administración",
                    JOptionPane.INFORMATION_MESSAGE
            );


            view.limpiarFormulario();

            actualizarEstadoCliente();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo registrar el usuario.",
                    e
            );
        }
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

            mostrarValidacion(
                    "Ingresá el email del usuario que querés eliminar."
            );

            return;
        }


        try {

            Document usuario =
                    usuarioController
                            .buscarPorEmail(
                                    email
                            );


            if (usuario == null) {

                mostrarValidacion(
                        "No existe un usuario con ese email."
                );

                return;
            }


            ObjectId usuarioId =
                    usuario.getObjectId(
                            "_id"
                    );


            if (usuarioId == null) {

                throw new IllegalStateException(
                        "El usuario no tiene un ID válido."
                );
            }


            int respuesta =
                    JOptionPane.showConfirmDialog(
                            view,
                            "¿Querés eliminar al usuario "
                                    + email
                                    + "?",
                            "Eliminar usuario",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );


            if (respuesta
                    != JOptionPane.YES_OPTION) {

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


            view.limpiarFormulario();

            actualizarEstadoCliente();

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


            modelo.setRowCount(0);


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

            mostrarValidacion(
                    "Seleccioná una sesión."
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


        if (respuesta
                != JOptionPane.YES_OPTION) {

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
    // VALIDACIÓN
    // =========================================================

    private void mostrarValidacion(
            String mensaje
    ) {

        JOptionPane.showMessageDialog(
                view,
                mensaje,
                "Validación",
                JOptionPane.WARNING_MESSAGE
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