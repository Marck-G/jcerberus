package org.kcramsolutions.jcerberus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class TokenLib {
static {
        try {
            loadNativeLibrary();
        } catch (IOException e) {
            throw new RuntimeException("Error cargando la librería nativa: " + e.getMessage(), e);
        } 
    }

    private static void loadNativeLibrary() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        String libPath;
        if (os.contains("win")) {
            libPath = "/lib/libcerberus.dll";
        // } else if (os.contains("mac")) {
        //     libPath = "/lib/libcerberus.dylib";
        } 
        else {
            libPath = "/lib/libcerberus.so";
        }

        // Extraer a un fichero temporal
        InputStream in = TokenLib.class.getResourceAsStream(libPath);
        if (in == null) {
            throw new FileNotFoundException("No se encontró la librería nativa: " + libPath);
        }

        File temp = File.createTempFile("tokenlib", (os.contains("win") ? ".dll" : ".so"));
        temp.deleteOnExit();

        try (OutputStream out = new FileOutputStream(temp)) {
            Files.copy(in, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        System.load(temp.getAbsolutePath());
    }

    // Métodos nativos
    public native String createToken(String headerJson, String payloadJson, String privateKeyPath, String encryptedSymmetricKeyPath);

    public native boolean verify(String token, String publicKeyPath);

    public native String extractTokenData(String token, String privateKeyPath, String encryptedSymmetricKeyPath);

}
