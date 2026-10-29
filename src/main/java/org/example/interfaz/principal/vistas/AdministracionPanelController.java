
package org.example.interfaz.principal.vistas;

import org.example.mongoDB.controller.ControllerMongoDB;

import org.example.logging.AppLog;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.usuarios.controller.AdministracionController;
import org.example.usuarios.controller.SesionController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Map;

public class AdministracionPanelController {

    private final ControllerMongoDB mongo = new ControllerMongoDB();

    private String usuarioIdEnEdicion = null;
    private String clienteIdEnEdicion = null;
    private String proveedorIdEnEdicion = null;
    private String emailOriginalEnEdicion = null;

    private final AdministracionPanel view;

    private final AdministracionController administracionController;




    private final SesionController sesionController;

    private Timer timer;
    private final Map<String, String> mailsPorUsuario = new java.util.HashMap<>();


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AdministracionPanelController(
            AdministracionPanel view
    ) {

        this.view = view;

        this.administracionController =
                new AdministracionController();




        this.sesionController =
                new SesionController();


        configurarEventos();

        cargarRoles();

        actualizarFormularioPorRol();

        actualizarBotonesUsuario();

        view.getTablaSesiones().getColumnModel().getColumn(0)
                .setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
                    @Override
                    protected void setValue(Object value) {
                        super.setValue(mailDeUsuario(String.valueOf(value)));
                    }
                });
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
                        e -> actualizarFormularioPorRol()
                );


        // -----------------------------------------------------
        // USUARIOS
        // -----------------------------------------------------

        view.getBtnBuscar()
                .addActionListener(
                        e -> buscarUsuarioParaModificar()
                );


        view.getBtnRegistrar()
                .addActionListener(
                        e -> registrarUsuario()
                );


        view.getBtnModificar()
                .addActionListener(
                        e -> modificarUsuario()
                );


        view.getBtnEliminar()
                .addActionListener(
                        e -> eliminarUsuario()
                );
    }


    // =========================================================
    // ACTUALIZAR BOTONES DE USUARIO
    // =========================================================

    private void actualizarBotonesUsuario() {

        boolean usuarioSeleccionado = usuarioIdEnEdicion != null;

        view.getBtnRegistrar().setVisible(!usuarioSeleccionado);

        view.getBtnBuscar().setVisible(true);

        view.getBtnModificar().setVisible(usuarioSeleccionado);

        view.getBtnEliminar().setVisible(usuarioSeleccionado);

        view.revalidate();
        view.repaint();
    }


    // =========================================================
    // CARGAR ROLES
    // =========================================================

    private void cargarRoles() {

        try {

            List<Document> roles =
                    mongo.listarRoles();


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
    // MOSTRAR FORMULARIO SEGÚN ROL
    // =========================================================

    private void actualizarFormularioPorRol() {
        Object seleccionado = view.getCmbRol().getSelectedItem();
        String rol = seleccionado == null ? "" : seleccionado.toString().trim();
        boolean esCliente = "Cliente".equalsIgnoreCase(rol);
        boolean esProveedor = "Proveedor".equalsIgnoreCase(rol);

        // Para Cliente y Proveedor el correo de acceso se carga
        // en la ficha de su empresa. Admin/Operador utilizan
        // el campo de correo del usuario.
        if (usuarioIdEnEdicion != null) {
            String correoActual = view.getTxtEmail().getText();
            if (!correoActual.isBlank()) {
                if (esCliente) view.getTxtEmailCliente().setText(correoActual);
                if (esProveedor) view.getTxtEmailProveedor().setText(correoActual);
            }
        }

        view.mostrarEmailUsuario(!esCliente && !esProveedor);

        if (esCliente) view.mostrarDatosCliente();
        else view.ocultarDatosCliente();

        if (esProveedor) view.mostrarDatosProveedor();
        else view.ocultarDatosProveedor();

        if (usuarioIdEnEdicion == null) {
            if (!esCliente) view.limpiarDatosCliente();
            if (!esProveedor) view.limpiarDatosProveedor();
        }
    }


    // =========================================================
    // BUSCAR USUARIO PARA MODIFICAR O ELIMINAR
    // =========================================================

    private void buscarUsuarioParaModificar() {

        String email = JOptionPane.showInputDialog(
                view,
                "Ingresá el email del usuario que querés modificar o eliminar:",
                "Buscar usuario",
                JOptionPane.QUESTION_MESSAGE
        );

        if (email == null) {
            return;
        }

        if (email.isBlank()) {

            JOptionPane.showMessageDialog(
                    view,
                    "Ingresá un email.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            Document usuario = mongo.buscarUsuarioPorEmail(
                    email.trim().toLowerCase()
            );

            if (usuario == null) {

                throw new IllegalArgumentException(
                        "No se encontró un usuario con ese email."
                );
            }

            ObjectId id = usuario.getObjectId("_id");
            ObjectId clienteId = usuario.getObjectId("cliente_id");
            ObjectId proveedorId = usuario.getObjectId("proveedor_id");
            ObjectId rolId = usuario.getObjectId("rol_id");

            if (id == null || rolId == null) {

                throw new IllegalArgumentException(
                        "El usuario tiene datos incompletos."
                );
            }

            Document rol = null;

            for (Document r : mongo.listarRoles()) {

                if (rolId.equals(r.getObjectId("_id"))) {

                    rol = r;
                    break;
                }
            }

            if (rol == null) {

                throw new IllegalArgumentException(
                        "No se encontró el rol del usuario."
                );
            }

            // Limpiamos los datos anteriores de búsqueda.
            usuarioIdEnEdicion = null;
            clienteIdEnEdicion = null;
            proveedorIdEnEdicion = null;
            emailOriginalEnEdicion = null;

            actualizarBotonesUsuario();

            view.limpiarDatosCliente();
            view.limpiarDatosProveedor();

            // Cambiar el rol actualiza el formulario visible.
            view.getCmbRol().setSelectedItem(
                    rol.getString("descripcion")
            );

            view.getTxtNombre().setText(
                    usuario.getString("nombre")
            );

            view.getTxtApellido().setText(
                    usuario.getString("apellido")
            );

            view.getTxtEmail().setText(
                    usuario.getString("email")
            );

            view.getTxtEmailCliente().setText(usuario.getString("email"));
            view.getTxtEmailProveedor().setText(usuario.getString("email"));

            // Cargar la ficha existente de proveedor sin crear otro.
            if (proveedorId != null) {
                Document proveedor = mongo.buscarProveedorPorId(proveedorId.toHexString());
                cargarDatosProveedor(proveedor);
            }

            // Nunca mostrar la contraseña almacenada.
            view.getTxtContrasena().setText("");

            usuarioIdEnEdicion = id.toHexString();

            clienteIdEnEdicion = clienteId == null
                    ? null
                    : clienteId.toHexString();
            proveedorIdEnEdicion = proveedorId == null
                    ? null
                    : proveedorId.toHexString();

            emailOriginalEnEdicion = usuario.getString("email");

            actualizarBotonesUsuario();

            JOptionPane.showMessageDialog(
                    view,
                    "Usuario encontrado. Ya podés modificarlo o eliminarlo.",
                    "Administración",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {

            usuarioIdEnEdicion = null;
            clienteIdEnEdicion = null;
            proveedorIdEnEdicion = null;
            emailOriginalEnEdicion = null;

            actualizarBotonesUsuario();

            mostrarError(
                    "No se pudo buscar el usuario.",
                    e
            );
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

        // Para Cliente, el email de Datos del cliente se usa
        // en ambas colecciones: clientes y usuarios.

        String email = descripcionRol.equalsIgnoreCase("Cliente")
                ? view.getTxtEmailCliente().getText().trim()
                : descripcionRol.equalsIgnoreCase("Proveedor")
                ? view.getTxtEmailProveedor().getText().trim()
                : view.getTxtEmail().getText().trim();

        if (email.isBlank()) {

            JOptionPane.showMessageDialog(
                    view,
                    "Ingresá el email.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        try {

            // =================================================
            // BUSCAR ROL
            // =================================================

            Document rol =
                    mongo.buscarRolPorDescripcion(
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

            String clienteId = null;
            String proveedorId = null;

            if (
                    descripcionRol.equalsIgnoreCase(
                            "Cliente"
                    )
            ) {

                clienteId =
                        crearClienteYObtenerId();
            }


            if (descripcionRol.equalsIgnoreCase("Proveedor")) {
                proveedorId = crearProveedorYObtenerId();
            }

            // =================================================
            // CREAR USUARIO
            // =================================================

            mongo.crearUsuario(
                    clienteId,
                    proveedorId,
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
                            : descripcionRol.equalsIgnoreCase("Proveedor")
                            ? "Proveedor, cuenta corriente y usuario registrados correctamente."
                            : "Usuario registrado correctamente.",
                    "Administración",
                    JOptionPane.INFORMATION_MESSAGE
            );


            limpiarFormularioUsuario();

            view.limpiarDatosCliente();
            view.limpiarDatosProveedor();

            actualizarFormularioPorRol();


        } catch (Exception e) {

            mostrarError(
                    "No se pudo registrar el usuario.",
                    e
            );
        }
    }


    // =========================================================
    // MODIFICAR USUARIO
    // =========================================================

    private void modificarUsuario() {

        if (usuarioIdEnEdicion == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "Primero buscá el usuario que querés modificar.",
                    "Administración",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            String nombre =
                    view.getTxtNombre().getText().trim();

            String apellido =
                    view.getTxtApellido().getText().trim();

            String contraseña =
                    new String(
                            view.getTxtContrasena().getPassword()
                    );

            Object rolSeleccionado =
                    view.getCmbRol().getSelectedItem();

            if (rolSeleccionado == null) {

                throw new IllegalArgumentException(
                        "Seleccioná un rol."
                );
            }

            String descripcionRol =
                    rolSeleccionado.toString().trim();

            Document rol =
                    mongo.buscarRolPorDescripcion(descripcionRol);

            if (rol == null || rol.getObjectId("_id") == null) {

                throw new IllegalArgumentException(
                        "No se encontró el rol seleccionado."
                );
            }

            String rolId =
                    rol.getObjectId("_id").toHexString();

            boolean esCliente =
                    "Cliente".equalsIgnoreCase(descripcionRol);

            boolean esProveedor =
                    "Proveedor".equalsIgnoreCase(descripcionRol);

            String email = esCliente
                    ? view.getTxtEmailCliente().getText().trim()
                    : esProveedor
                    ? view.getTxtEmailProveedor().getText().trim()
                    : view.getTxtEmail().getText().trim();

            // Conservamos la empresa asociada.
            // No creamos clientes nuevos al modificar usuarios.

            if (esCliente && clienteIdEnEdicion == null) {

                throw new IllegalArgumentException(
                        "Este usuario no tiene una empresa asociada. "
                                + "No se puede cambiar a Cliente desde este formulario."
                );
            }

            String clienteId = esCliente ? clienteIdEnEdicion : null;

            if (esProveedor && proveedorIdEnEdicion == null) {
                throw new IllegalArgumentException(
                        "Este usuario no tiene un proveedor asociado. "
                                + "No se puede cambiar a Proveedor desde este formulario."
                );
            }

            String proveedorId = esProveedor ? proveedorIdEnEdicion : null;

            // Recuperar el usuario original para conservar su estado.

            Document usuarioActual =
                    mongo.buscarUsuarioPorEmail(emailOriginalEnEdicion);

            if (usuarioActual == null
                    || usuarioActual.getObjectId("_id") == null
                    || !usuarioIdEnEdicion.equals(
                            usuarioActual.getObjectId("_id").toHexString()
                    )) {

                throw new IllegalArgumentException(
                        "No se pudo recuperar el usuario original."
                );
            }

            String estado =
                    usuarioActual.getString("estado");

            // Confirmación antes de guardar.

            int respuesta =
                    JOptionPane.showConfirmDialog(
                            view,
                            "¿Querés guardar los cambios de este usuario?",
                            "Modificar usuario",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }

            // =================================================
            // GUARDAR MODIFICACIONES
            // =================================================

            // La ficha del proveedor y su usuario son operaciones
            // independientes. No cambiamos los datos de otro proveedor.
            if (esProveedor) {
                modificarProveedorExistente(proveedorId);
            }

            mongo.modificarUsuario(
                    usuarioIdEnEdicion,
                    clienteId,
                    proveedorId,
                    rolId,
                    nombre,
                    apellido,
                    email,
                    contraseña,
                    estado
            );

            JOptionPane.showMessageDialog(
                    view,
                    "Usuario modificado correctamente.",
                    "Administración",
                    JOptionPane.INFORMATION_MESSAGE
            );

            limpiarFormularioUsuario();

            view.limpiarDatosCliente();
            view.limpiarDatosProveedor();

            actualizarFormularioPorRol();

            cargarSesiones();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo modificar el usuario.",
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

        mongo.crearCliente(
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
                mongo.buscarClientePorCuit(
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
    // PROVEEDOR: CREAR Y RECUPERAR ID
    // =========================================================

    private String crearProveedorYObtenerId() {
        String razonSocial = view.getTxtRazonSocialProveedor().getText().trim();
        String cuit = view.getTxtCuitProveedor().getText().trim();
        String email = view.getTxtEmailProveedor().getText().trim();
        String telefono = view.getTxtTelefonoProveedor().getText().trim();
        String calle = view.getTxtCalleProveedor().getText().trim();
        String numero = view.getTxtNumeroProveedor().getText().trim();
        String ciudad = view.getTxtCiudadProveedor().getText().trim();
        String codigoPostal = view.getTxtCodigoPostalProveedor().getText().trim();
        String pais = view.getTxtPaisProveedor().getText().trim();

        mongo.crearProveedor(razonSocial, cuit, email, telefono,
                calle, numero, ciudad, codigoPostal, pais);

        Document proveedor = mongo.buscarProveedorPorCuit(cuit);
        if (proveedor == null || proveedor.getObjectId("_id") == null) {
            throw new IllegalStateException(
                    "El proveedor fue creado, pero no se pudo recuperar su ID.");
        }
        return proveedor.getObjectId("_id").toHexString();
    }

    // =========================================================
    // PROVEEDOR: MOSTRAR DATOS EXISTENTES
    // =========================================================

    private void cargarDatosProveedor(Document proveedor) {
        if (proveedor == null) return;
        view.getTxtRazonSocialProveedor().setText(proveedor.getString("razon_social"));
        view.getTxtCuitProveedor().setText(proveedor.getString("cuit"));
        view.getTxtEmailProveedor().setText(proveedor.getString("email"));
        view.getTxtTelefonoProveedor().setText(proveedor.getString("telefono"));
        view.getTxtPaisProveedor().setText(proveedor.getString("pais"));
        Document direccion = proveedor.get("direccion", Document.class);
        if (direccion != null) {
            view.getTxtCalleProveedor().setText(direccion.getString("calle"));
            view.getTxtNumeroProveedor().setText(direccion.getString("numero"));
            view.getTxtCiudadProveedor().setText(direccion.getString("ciudad"));
            view.getTxtCodigoPostalProveedor().setText(direccion.getString("codigo_postal"));
        }
    }

    // =========================================================
    // PROVEEDOR: MODIFICAR FICHA EXISTENTE
    // =========================================================

    private void modificarProveedorExistente(String proveedorId) {
        Document proveedorActual = mongo.buscarProveedorPorId(proveedorId);
        String estado = proveedorActual.getString("estado");
        mongo.modificarProveedor(
                proveedorId,
                view.getTxtRazonSocialProveedor().getText().trim(),
                view.getTxtCuitProveedor().getText().trim(),
                view.getTxtEmailProveedor().getText().trim(),
                view.getTxtTelefonoProveedor().getText().trim(),
                view.getTxtCalleProveedor().getText().trim(),
                view.getTxtNumeroProveedor().getText().trim(),
                view.getTxtCiudadProveedor().getText().trim(),
                view.getTxtCodigoPostalProveedor().getText().trim(),
                view.getTxtPaisProveedor().getText().trim(),
                estado == null ? "ACTIVO" : estado
        );
    }


    // =========================================================
    // ELIMINAR USUARIO
    // =========================================================

    private void eliminarUsuario() {

        if (usuarioIdEnEdicion == null) {

            JOptionPane.showMessageDialog(
                    view,
                    "Primero buscá el usuario que querés eliminar.",
                    "Administración",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            // Eliminar por el ID del usuario encontrado.
            // No usamos el email que pueda haber sido editado.

            String usuarioId = usuarioIdEnEdicion;

            Document usuario = null;

            for (Document candidato : mongo.listarUsuarios()) {

                ObjectId id = candidato.getObjectId("_id");

                if (id != null
                        && id.toHexString().equals(usuarioId)) {

                    usuario = candidato;
                    break;
                }
            }

            if (usuario == null) {

                throw new IllegalArgumentException(
                        "El usuario seleccionado ya no existe."
                );
            }

            String emailOriginal =
                    usuario.getString("email");

            int respuesta =
                    JOptionPane.showConfirmDialog(
                            view,
                            "¿Querés eliminar al usuario "
                                    + emailOriginal
                                    + "?",
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


            mongo.eliminarUsuario(
                    usuarioId
            );


            JOptionPane.showMessageDialog(
                    view,
                    "Usuario eliminado correctamente.",
                    "Administración",
                    JOptionPane.INFORMATION_MESSAGE
            );


            limpiarFormularioUsuario();

            view.limpiarDatosCliente();
            view.limpiarDatosProveedor();

            actualizarFormularioPorRol();

            cargarSesiones();


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

    private String mailDeUsuario(String id) {
        return mailsPorUsuario.getOrDefault(id, "Mail no disponible");
    }

    private void actualizarMailsDeUsuarios() {

        try {

            Map<String, String> mails =
                    new java.util.HashMap<>();

            for (Document usuario : mongo.listarUsuarios()) {

                ObjectId id =
                        usuario.getObjectId("_id");

                String email =
                        usuario.getString("email");

                if (id != null && email != null && !email.isBlank()) {

                    mails.put(id.toHexString(), email);
                }
            }

            mailsPorUsuario.clear();
            mailsPorUsuario.putAll(mails);

        } catch (Exception e) {

            // La información visual no debe impedir administrar las sesiones.

            AppLog.warning(
                    AdministracionPanelController.class,
                    "No se pudieron cargar las sesiones activas."
            );
        }
    }

    private void cargarSesiones() {

        actualizarMailsDeUsuarios();

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

        usuarioIdEnEdicion = null;
        clienteIdEnEdicion = null;
        proveedorIdEnEdicion = null;
        emailOriginalEnEdicion = null;

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

        actualizarBotonesUsuario();
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
                                + mailDeUsuario(usuario)
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
