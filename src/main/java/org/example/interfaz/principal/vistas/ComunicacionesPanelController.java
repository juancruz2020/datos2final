package org.example.interfaz.principal.vistas;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.mongoDB.controller.ConversacionController;
import org.example.mongoDB.controller.MensajeController;
import org.example.mongoDB.dao.UsuarioMongoDAO;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ComunicacionesPanelController {

    private final ComunicacionesPanel view;

    private final String usuarioActualId;

    private final ConversacionController conversacionController;
    private final MensajeController mensajeController;
    private final UsuarioMongoDAO usuarioDAO;

    private final List<Document> conversacionesActuales;
    private final List<Document> mensajesActuales;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ComunicacionesPanelController(
            ComunicacionesPanel view,
            String usuarioActualId
    ) {

        this.view =
                view;

        this.usuarioActualId =
                usuarioActualId;

        this.conversacionController =
                new ConversacionController();

        this.mensajeController =
                new MensajeController();

        this.usuarioDAO =
                new UsuarioMongoDAO();

        this.conversacionesActuales =
                new ArrayList<>();

        this.mensajesActuales =
                new ArrayList<>();

        configurarEventos();

        cargarConversaciones();
    }


    // =========================================================
    // CONFIGURAR EVENTOS
    // =========================================================

    private void configurarEventos() {

        view.getBtnNuevaPrivada()
                .addActionListener(
                        e -> crearConversacionPrivada()
                );

        view.getBtnNuevoGrupo()
                .addActionListener(
                        e -> crearConversacionGrupal()
                );

        view.getBtnActualizar()
                .addActionListener(
                        e -> cargarConversaciones()
                );

        view.getBtnEnviar()
                .addActionListener(
                        e -> enviarMensaje()
                );

        view.getBtnEditar()
                .addActionListener(
                        e -> editarMensaje()
                );

        view.getBtnEliminar()
                .addActionListener(
                        e -> eliminarMensaje()
                );

        view.getListaConversaciones()
                .addListSelectionListener(
                        e -> {

                            if (!e.getValueIsAdjusting()) {

                                cargarMensajesConversacionSeleccionada();
                            }
                        }
                );
    }


    // =========================================================
    // CARGAR CONVERSACIONES
    // =========================================================

    private void cargarConversaciones() {

        try {

            conversacionesActuales.clear();

            view.getModeloConversaciones()
                    .clear();

            view.getModeloMensajes()
                    .clear();

            mensajesActuales.clear();


            List<Document> conversaciones =
                    conversacionController
                            .obtenerConversacionesUsuario(
                                    usuarioActualId
                            );


            conversacionesActuales.addAll(
                    conversaciones
            );


            for (Document conversacion : conversaciones) {

                String nombre =
                        conversacion.getString(
                                "nombre"
                        );

                String tipo =
                        conversacion.getString(
                                "tipo"
                        );


                if (
                        nombre == null
                                ||
                        nombre.isBlank()
                ) {

                    nombre =
                            obtenerNombreConversacion(
                                    conversacion
                            );
                }


                String texto =
                        nombre
                                + " ["
                                + tipo
                                + "]";


                view.getModeloConversaciones()
                        .addElement(
                                texto
                        );
            }


            if (
                    !conversacionesActuales.isEmpty()
            ) {

                view.getListaConversaciones()
                        .setSelectedIndex(
                                0
                        );
            }

        } catch (Exception e) {

            mostrarError(
                    "No se pudieron cargar las conversaciones:\n"
                            + e.getMessage()
            );
        }
    }


    // =========================================================
    // CARGAR MENSAJES DE CONVERSACIÓN SELECCIONADA
    // =========================================================

    private void cargarMensajesConversacionSeleccionada() {

        int indice =
                view.getListaConversaciones()
                        .getSelectedIndex();


        if (
                indice < 0
                        ||
                indice >= conversacionesActuales.size()
        ) {

            return;
        }


        Document conversacion =
                conversacionesActuales.get(
                        indice
                );


        ObjectId id =
                conversacion.getObjectId(
                        "_id"
                );


        if (id == null) {

            return;
        }


        cargarMensajes(
                id.toHexString()
        );
    }


    // =========================================================
    // CARGAR MENSAJES
    // =========================================================

    private void cargarMensajes(
            String conversacionId
    ) {

        try {

            mensajesActuales.clear();

            view.getModeloMensajes()
                    .clear();


            List<Document> mensajes =
                    mensajeController
                            .obtenerHistorial(
                                    conversacionId
                            );


            mensajesActuales.addAll(
                    mensajes
            );


            SimpleDateFormat formato =
                    new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm"
                    );


            for (Document mensaje : mensajes) {

                String remitenteId =
                        mensaje.getString(
                                "remitente_id"
                        );


                String contenido =
                        mensaje.getString(
                                "contenido"
                        );


                Date fecha =
                        mensaje.getDate(
                                "fecha_envio"
                        );


                String nombreRemitente =
                        obtenerNombreUsuario(
                                remitenteId
                        );


                String fechaTexto =
                        fecha != null
                                ? formato.format(fecha)
                                : "";


                String texto =
                        nombreRemitente
                                + " - "
                                + fechaTexto
                                + " | "
                                + contenido;


                view.getModeloMensajes()
                        .addElement(
                                texto
                        );
            }


            if (
                    !mensajesActuales.isEmpty()
            ) {

                int ultimo =
                        mensajesActuales.size() - 1;

                view.getListaMensajes()
                        .ensureIndexIsVisible(
                                ultimo
                        );
            }

        } catch (Exception e) {

            mostrarError(
                    "No se pudo cargar el historial:\n"
                            + e.getMessage()
            );
        }
    }


    // =========================================================
    // CREAR CONVERSACIÓN PRIVADA
    // =========================================================

    private void crearConversacionPrivada() {

        try {

            List<Document> usuarios =
                    obtenerOtrosUsuarios();


            if (usuarios.isEmpty()) {

                mostrarError(
                        "No hay otros usuarios disponibles."
                );

                return;
            }


            String[] opciones =
                    new String[
                            usuarios.size()
                            ];


            for (
                    int i = 0;
                    i < usuarios.size();
                    i++
            ) {

                opciones[i] =
                        obtenerNombreCompleto(
                                usuarios.get(i)
                        );
            }


            String seleccionado =
                    (String)
                            JOptionPane.showInputDialog(
                                    view,
                                    "Seleccioná el usuario:",
                                    "Nueva conversación privada",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    opciones,
                                    opciones[0]
                            );


            if (seleccionado == null) {

                return;
            }


            int indiceSeleccionado =
                    buscarIndice(
                            opciones,
                            seleccionado
                    );


            if (indiceSeleccionado < 0) {

                return;
            }


            Document usuarioDestino =
                    usuarios.get(
                            indiceSeleccionado
                    );


            ObjectId usuarioDestinoObjectId =
                    usuarioDestino.getObjectId(
                            "_id"
                    );


            if (usuarioDestinoObjectId == null) {

                mostrarError(
                        "El usuario seleccionado no tiene un ID válido."
                );

                return;
            }


            String usuarioDestinoId =
                    usuarioDestinoObjectId
                            .toHexString();


            String nombreDestino =
                    obtenerNombreCompleto(
                            usuarioDestino
                    );


            String nombreActual =
                    obtenerNombreUsuario(
                            usuarioActualId
                    );


            String nombreConversacion =
                    nombreActual
                            + " - "
                            + nombreDestino;


            conversacionController
                    .crearConversacionPrivada(
                            nombreConversacion,
                            usuarioActualId,
                            usuarioDestinoId
                    );


            JOptionPane.showMessageDialog(
                    view,
                    "Conversación creada correctamente.",
                    "Comunicaciones",
                    JOptionPane.INFORMATION_MESSAGE
            );


            cargarConversaciones();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo crear la conversación:\n"
                            + e.getMessage()
            );
        }
    }


    // =========================================================
    // CREAR CONVERSACIÓN GRUPAL
    // =========================================================

    private void crearConversacionGrupal() {

        try {

            List<Document> usuarios =
                    obtenerOtrosUsuarios();


            if (usuarios.isEmpty()) {

                mostrarError(
                        "No hay usuarios disponibles para crear un grupo."
                );

                return;
            }


            String nombreGrupo =
                    JOptionPane.showInputDialog(
                            view,
                            "Nombre del grupo:",
                            "Nueva conversación grupal",
                            JOptionPane.PLAIN_MESSAGE
                    );


            if (nombreGrupo == null) {

                return;
            }


            nombreGrupo =
                    nombreGrupo.trim();


            if (nombreGrupo.isBlank()) {

                mostrarError(
                        "El nombre del grupo es obligatorio."
                );

                return;
            }


            DefaultListModel<String> modelo =
                    new DefaultListModel<>();


            for (Document usuario : usuarios) {

                modelo.addElement(
                        obtenerNombreCompleto(
                                usuario
                        )
                );
            }


            JList<String> listaUsuarios =
                    new JList<>(
                            modelo
                    );


            listaUsuarios.setSelectionMode(
                    ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
            );


            JScrollPane scroll =
                    new JScrollPane(
                            listaUsuarios
                    );


            scroll.setPreferredSize(
                    new java.awt.Dimension(
                            350,
                            220
                    )
            );


            int resultado =
                    JOptionPane.showConfirmDialog(
                            view,
                            scroll,
                            "Seleccioná los participantes",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE
                    );


            if (
                    resultado
                            !=
                    JOptionPane.OK_OPTION
            ) {

                return;
            }


            int[] indices =
                    listaUsuarios
                            .getSelectedIndices();


            if (indices.length == 0) {

                mostrarError(
                        "Seleccioná al menos un usuario para el grupo."
                );

                return;
            }


            List<String> participantesIds =
                    new ArrayList<>();


            participantesIds.add(
                    usuarioActualId
            );


            for (int indice : indices) {

                Document usuario =
                        usuarios.get(
                                indice
                        );


                ObjectId usuarioId =
                        usuario.getObjectId(
                                "_id"
                        );


                if (usuarioId != null) {

                    participantesIds.add(
                            usuarioId.toHexString()
                    );
                }
            }


            conversacionController
                    .crearConversacionGrupal(
                            nombreGrupo,
                            participantesIds
                    );


            JOptionPane.showMessageDialog(
                    view,
                    "Grupo creado correctamente.",
                    "Comunicaciones",
                    JOptionPane.INFORMATION_MESSAGE
            );


            cargarConversaciones();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo crear el grupo:\n"
                            + e.getMessage()
            );
        }
    }


    // =========================================================
    // ENVIAR MENSAJE
    // =========================================================

    private void enviarMensaje() {

        try {

            String conversacionId =
                    obtenerConversacionSeleccionadaId();


            if (conversacionId == null) {

                mostrarError(
                        "Seleccioná una conversación."
                );

                return;
            }


            String contenido =
                    view.getTxtMensaje()
                            .getText()
                            .trim();


            if (contenido.isBlank()) {

                mostrarError(
                        "Escribí un mensaje."
                );

                return;
            }


            mensajeController
                    .enviarMensaje(
                            conversacionId,
                            usuarioActualId,
                            contenido
                    );


            view.getTxtMensaje()
                    .setText("");


            cargarMensajes(
                    conversacionId
            );


            view.getTxtMensaje()
                    .requestFocus();

        } catch (Exception e) {

            mostrarError(
                    "No se pudo enviar el mensaje:\n"
                            + e.getMessage()
            );
        }
    }


    // =========================================================
    // EDITAR MENSAJE
    // =========================================================

    private void editarMensaje() {

        try {

            Document mensaje =
                    obtenerMensajeSeleccionado();


            if (mensaje == null) {

                mostrarError(
                        "Seleccioná un mensaje."
                );

                return;
            }


            String remitenteId =
                    mensaje.getString(
                            "remitente_id"
                    );


            if (
                    !usuarioActualId.equals(
                            remitenteId
                    )
            ) {

                mostrarError(
                        "Solo podés editar tus propios mensajes."
                );

                return;
            }


            String contenidoActual =
                    mensaje.getString(
                            "contenido"
                    );


            String nuevoContenido =
                    (String)
                            JOptionPane.showInputDialog(
                                    view,
                                    "Modificar mensaje:",
                                    "Editar mensaje",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    null,
                                    contenidoActual
                            );


            if (nuevoContenido == null) {

                return;
            }


            nuevoContenido =
                    nuevoContenido.trim();


            if (nuevoContenido.isBlank()) {

                mostrarError(
                        "El mensaje no puede estar vacío."
                );

                return;
            }


            ObjectId mensajeObjectId =
                    mensaje.getObjectId(
                            "_id"
                    );


            if (mensajeObjectId == null) {

                mostrarError(
                        "El mensaje seleccionado no tiene un ID válido."
                );

                return;
            }


            String mensajeId =
                    mensajeObjectId
                            .toHexString();


            mensajeController
                    .modificarMensaje(
                            mensajeId,
                            usuarioActualId,
                            nuevoContenido
                    );


            String conversacionId =
                    mensaje.getString(
                            "conversacion_id"
                    );


            cargarMensajes(
                    conversacionId
            );

        } catch (Exception e) {

            mostrarError(
                    "No se pudo modificar el mensaje:\n"
                            + e.getMessage()
            );
        }
    }


    // =========================================================
    // ELIMINAR MENSAJE
    // =========================================================

    private void eliminarMensaje() {

        try {

            Document mensaje =
                    obtenerMensajeSeleccionado();


            if (mensaje == null) {

                mostrarError(
                        "Seleccioná un mensaje."
                );

                return;
            }


            String remitenteId =
                    mensaje.getString(
                            "remitente_id"
                    );


            if (
                    !usuarioActualId.equals(
                            remitenteId
                    )
            ) {

                mostrarError(
                        "Solo podés eliminar tus propios mensajes."
                );

                return;
            }


            int respuesta =
                    JOptionPane.showConfirmDialog(
                            view,
                            "¿Querés eliminar este mensaje?",
                            "Eliminar mensaje",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );


            if (
                    respuesta
                            !=
                    JOptionPane.YES_OPTION
            ) {

                return;
            }


            ObjectId mensajeObjectId =
                    mensaje.getObjectId(
                            "_id"
                    );


            if (mensajeObjectId == null) {

                mostrarError(
                        "El mensaje seleccionado no tiene un ID válido."
                );

                return;
            }


            String mensajeId =
                    mensajeObjectId
                            .toHexString();


            String conversacionId =
                    mensaje.getString(
                            "conversacion_id"
                    );


            mensajeController
                    .eliminarMensaje(
                            mensajeId,
                            usuarioActualId
                    );


            cargarMensajes(
                    conversacionId
            );

        } catch (Exception e) {

            mostrarError(
                    "No se pudo eliminar el mensaje:\n"
                            + e.getMessage()
            );
        }
    }


    // =========================================================
    // OBTENER CONVERSACIÓN SELECCIONADA
    // =========================================================

    private String obtenerConversacionSeleccionadaId() {

        int indice =
                view.getListaConversaciones()
                        .getSelectedIndex();


        if (
                indice < 0
                        ||
                indice >= conversacionesActuales.size()
        ) {

            return null;
        }


        Document conversacion =
                conversacionesActuales.get(
                        indice
                );


        ObjectId id =
                conversacion.getObjectId(
                        "_id"
                );


        if (id == null) {

            return null;
        }


        return id.toHexString();
    }


    // =========================================================
    // OBTENER MENSAJE SELECCIONADO
    // =========================================================

    private Document obtenerMensajeSeleccionado() {

        int indice =
                view.getListaMensajes()
                        .getSelectedIndex();


        if (
                indice < 0
                        ||
                indice >= mensajesActuales.size()
        ) {

            return null;
        }


        return mensajesActuales.get(
                indice
        );
    }


    // =========================================================
    // OBTENER OTROS USUARIOS
    // =========================================================

    private List<Document> obtenerOtrosUsuarios() {

        List<Document> todos =
                usuarioDAO.listarTodos();


        List<Document> resultado =
                new ArrayList<>();


        for (Document usuario : todos) {

            ObjectId id =
                    usuario.getObjectId(
                            "_id"
                    );


            if (id == null) {

                continue;
            }


            String estado =
                    usuario.getString(
                            "estado"
                    );


            if (
                    usuarioActualId.equals(
                            id.toHexString()
                    )
            ) {

                continue;
            }


            if (
                    estado != null
                            &&
                    !"ACTIVO".equalsIgnoreCase(
                            estado
                    )
            ) {

                continue;
            }


            resultado.add(
                    usuario
            );
        }


        return resultado;
    }


    // =========================================================
    // OBTENER NOMBRE DE USUARIO
    // =========================================================

    private String obtenerNombreUsuario(
            String usuarioId
    ) {

        if (
                usuarioId == null
                        ||
                usuarioId.isBlank()
        ) {

            return "Usuario";
        }


        try {

            Document usuario =
                    usuarioDAO.buscarPorId(
                            usuarioId
                    );


            if (usuario == null) {

                return "Usuario";
            }


            return obtenerNombreCompleto(
                    usuario
            );

        } catch (Exception e) {

            return "Usuario";
        }
    }


    // =========================================================
    // OBTENER NOMBRE COMPLETO
    // =========================================================

    private String obtenerNombreCompleto(
            Document usuario
    ) {

        String nombre =
                usuario.getString(
                        "nombre"
                );


        String apellido =
                usuario.getString(
                        "apellido"
                );


        String nombreCompleto =
                (
                        (nombre != null ? nombre : "")
                                + " "
                                + (apellido != null ? apellido : "")
                ).trim();


        if (nombreCompleto.isBlank()) {

            String email =
                    usuario.getString(
                            "email"
                    );


            if (
                    email != null
                            &&
                    !email.isBlank()
            ) {

                return email;
            }


            return "Usuario";
        }


        return nombreCompleto;
    }


    // =========================================================
    // OBTENER NOMBRE DE CONVERSACIÓN
    // =========================================================

    private String obtenerNombreConversacion(
            Document conversacion
    ) {

        List<String> participantes =
                conversacion.getList(
                        "participantes_ids",
                        String.class
                );


        if (
                participantes == null
                        ||
                participantes.isEmpty()
        ) {

            return "Conversación";
        }


        List<String> nombres =
                new ArrayList<>();


        for (String participanteId : participantes) {

            if (
                    !usuarioActualId.equals(
                            participanteId
                    )
            ) {

                nombres.add(
                        obtenerNombreUsuario(
                                participanteId
                        )
                );
            }
        }


        if (nombres.isEmpty()) {

            return "Conversación";
        }


        return String.join(
                ", ",
                nombres
        );
    }


    // =========================================================
    // BUSCAR ÍNDICE
    // =========================================================

    private int buscarIndice(
            String[] opciones,
            String seleccionado
    ) {

        for (
                int i = 0;
                i < opciones.length;
                i++
        ) {

            if (
                    opciones[i].equals(
                            seleccionado
                    )
            ) {

                return i;
            }
        }


        return -1;
    }


    // =========================================================
    // MOSTRAR ERROR
    // =========================================================

    private void mostrarError(
            String mensaje
    ) {

        JOptionPane.showMessageDialog(
                view,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}