package org.example.pdf;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class PDFOpener {

    public void abrir(File archivo) throws IOException {

        if (!Desktop.isDesktopSupported()) {
            throw new IOException(
                    "El sistema no permite abrir archivos automáticamente."
            );
        }

        Desktop.getDesktop().open(archivo);
    }
}