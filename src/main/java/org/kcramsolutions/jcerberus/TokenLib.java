package org.kcramsolutions.jcerberus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.astonbitecode.j4rs.api.Instance;
import org.astonbitecode.j4rs.api.java2rust.Java2RustUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenLib {
    private static final Logger logger = LoggerFactory.getLogger(TokenLib.class);
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
            logger.info("Linux system");
            libPath = "/lib/libcerberus.so";
        }

        // Extraer a un fichero temporal
        InputStream in = TokenLib.class.getResourceAsStream(libPath);
        if (in == null) {
            logger.error("Not found local library");
            throw new FileNotFoundException("No se encontró la librería nativa: " + libPath);
        }

        File temp = File.createTempFile("tokenlib", (os.contains("win") ? ".dll" : ".so"));
        temp.deleteOnExit();

        try (OutputStream out = new FileOutputStream(temp)) {
            Files.copy(in, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        logger.info("Lib loaded");
        System.load(temp.getAbsolutePath());
    }

        public String createToken(String headerJson, String payloadJson, String privateKeyPath, String encryptedSymmetricKeyPath){
            Instance<String> header = Java2RustUtils.createInstance(headerJson);
            Instance<String> body = Java2RustUtils.createInstance(payloadJson);
            Instance<String> priv = Java2RustUtils.createInstance(privateKeyPath);
            Instance<String> rsa = Java2RustUtils.createInstance(encryptedSymmetricKeyPath);
            Instance<String> out = nCreateToken(header, body, priv, rsa);
            String result = Java2RustUtils.getObjectCasted(out);
            logger.debug("Result: {}", result);
            return result;
        }

    public boolean verify(String token, String publicKeyPath){
        logger.debug("Init verification");
        Instance<String> tokInstance = Java2RustUtils.createInstance(token);
        Instance<String> pubInstance = Java2RustUtils.createInstance(publicKeyPath);
        try{

            Instance<Boolean> out = nVerify(tokInstance, pubInstance);
            Boolean resul = Java2RustUtils.getObjectCasted(out);
            logger.debug("Result: {}", resul);
            return resul;
        } catch (Exception e) {
            logger.error("Error in verify", e);
            return false;
        }
    }

    public static String extractTokenData(String token, String privateKeyPath, String encryptedSymmetricKeyPath){
        logger.debug("Creating args");
        Instance<String> tokenInstance = Java2RustUtils.createInstance(token);
        Instance<String> privateInstance = Java2RustUtils.createInstance(privateKeyPath);
        Instance<String> rsaInstance = Java2RustUtils.createInstance(encryptedSymmetricKeyPath);
        try{
            Instance<String> out = nExtractTokenData(tokenInstance, privateInstance, rsaInstance);
            return Java2RustUtils.getObjectCasted(out);
        }catch (Throwable e) {
            logger.error("Error while extracting", e);
            return null;
        }
    }
    // Métodos nativos
    public static native Instance<String> nCreateToken(Instance<String> headerJson, Instance<String> payloadJson, Instance<String> privateKeyPath, Instance<String> encryptedSymmetricKeyPath);

    public static native Instance<Boolean> nVerify(Instance<String> token, Instance<String> publicKeyPath);

    public static native Instance<String> nExtractTokenData(Instance<String> token, Instance<String> privateKeyPath, Instance<String> encryptedSymmetricKeyPath);

}
 