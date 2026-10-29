
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

        if (valor == null || !valor.matches("^[0-9]{10}$")) {

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

        if (valor == null || !valor.matches("^[0-9]{11}$")) {

            throw new IllegalArgumentException(
                    "El CUIT/CUIL debe contener exactamente "
                            + "11 números, sin guiones."
            );
        }

        int[] pesos = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};

        int suma = 0;

        for (int i = 0; i < 10; i++) {

            suma += (valor.charAt(i) - '0') * pesos[i];
        }

        int digito = 11 - (suma % 11);

        if (digito == 11) {
            digito = 0;
        }

        if (digito == 10
                || digito != valor.charAt(10) - '0') {

            throw new IllegalArgumentException(
                    "El CUIT/CUIL no es válido. "
                            + "Revisá el dígito verificador."
            );
        }
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
                || !valor.matches("^[0-9]{1,6}$")) {

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
                || !valor.trim().toUpperCase(Locale.ROOT)
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
