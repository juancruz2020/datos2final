
package org.example.mongoDB.service;

import java.util.Locale;

public final class ValidacionesRegistro {

    private ValidacionesRegistro() {
    }

    // ==========================================
    // NOMBRE Y APELLIDO
    // ==========================================

    public static void nombre(String valor, String campo) {

        if (valor == null
                || !valor.trim().matches(
                        "^[\\p{L}]+(?:[ '\\u2019-][\\p{L}]+)*$"
                )) {

            throw new IllegalArgumentException(
                    "El " + campo
                            + " debe contener solo letras."
                            + " No se permiten números."
            );
        }
    }

    // ==========================================
    // EMAIL
    // ==========================================

    public static void email(String valor) {

        if (valor == null
                || !valor.trim().matches(
                        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+"
                                + "(\\.[A-Za-z0-9-]+)+$"
                )) {

            throw new IllegalArgumentException(
                    "Ingresá un email válido. "
                            + "Ejemplo: usuario@gmail.com"
            );
        }
    }

    // ==========================================
    // CONTRASEÑA
    // ==========================================

    public static void contrasena(String valor) {

        if (valor == null || valor.isBlank()) {

            throw new IllegalArgumentException(
                    "La contraseña es obligatoria."
            );
        }
    }

    // ==========================================
    // TELÉFONO ARGENTINO
    // ==========================================

    public static void telefono(String valor) {

        if (valor == null
                || !valor.trim().matches("^[0-9]{10}$")) {

            throw new IllegalArgumentException(
                    "El teléfono debe contener exactamente "
                            + "10 números, sin espacios, guiones, "
                            + "0, 15 ni +54."
            );
        }
    }

    // ==========================================
    // CUIT / CUIL
    // ==========================================

    public static void cuit(String valor) {
        normalizarCuit(valor);
    }

    // ==========================================
    // NORMALIZAR Y VALIDAR CUIT / CUIL
    // ==========================================


    public static String normalizarCuit(String valor) {

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    "El CUIT/CUIL es obligatorio."
            );
        }

        // Permitir números con o sin guiones.
        String numeros = valor.trim()
                .replace("-", "")
                .replace(" ", "");

        // Únicamente se exige que contenga números.
        if (numeros.isEmpty() || !numeros.matches("[0-9]+")) {
            throw new IllegalArgumentException(
                    "El CUIT/CUIL debe contener solamente números, "
                            + "con o sin guiones."
            );
        }

        // No validar cantidad de dígitos ni dígito verificador.
        return numeros;
    }


    // ==========================================
    // CAMPOS OBLIGATORIOS
    // ==========================================

    public static void obligatorio(
            String valor,
            String campo
    ) {

        if (valor == null || valor.isBlank()) {

            throw new IllegalArgumentException(
                    "El campo " + campo + " es obligatorio."
            );
        }
    }

    // ==========================================
    // NÚMERO DE DOMICILIO
    // ==========================================

    public static void numeroDomicilio(String valor) {

        if (valor == null
                || !valor.trim().matches("^[0-9]{1,6}$")) {

            throw new IllegalArgumentException(
                    "El número de domicilio debe contener "
                            + "entre 1 y 6 números."
            );
        }
    }

    // ==========================================
    // CÓDIGO POSTAL
    // ==========================================

    public static void codigoPostal(String valor) {

        if (valor == null
                || !valor.trim()
                        .toUpperCase(Locale.ROOT)
                        .matches(
                                "^(?:[0-9]{4}|[A-Z][0-9]{4}[A-Z]{3})$"
                        )) {

            throw new IllegalArgumentException(
                    "El código postal debe tener 4 números "
                            + "o formato CPA, por ejemplo C1425ABC."
            );
        }
    }

    // ==========================================
    // DATOS DE CLIENTE O PROVEEDOR
    // ==========================================

    public static void datosEmpresa(
            String razonSocial,
            String cuit,
            String email,
            String telefono,
            String calle,
            String numero,
            String ciudad,
            String codigoPostal,
            String pais
    ) {

        obligatorio(razonSocial, "razón social");

        cuit(cuit);

        email(email);

        telefono(telefono);

        obligatorio(calle, "calle");

        numeroDomicilio(numero);

        obligatorio(ciudad, "ciudad");

        codigoPostal(codigoPostal);

        obligatorio(pais, "país");
    }
}
