# JCerberus

## Overview
`JCerberus` is a Java wrapper library for the Rust-based `cerberus` token management system. It provides native methods to create, verify, and extract secure tokens similar to JWT but with added symmetric encryption and compression for enhanced security and smaller token size.

This library communicates with the Rust native code via JNI (Java Native Interface), exposing a clean Java API for easy integration in Spring or any Java application.

## Features

- Create secure tokens by encrypting header and payload with AES-GCM.
- Compress tokens using Zstd compression.
- Sign tokens using RSA private keys.
- Verify tokens with RSA public keys.
- Extract and decrypt token data back to JSON.
- Native methods implemented in Rust for performance and security.
- Java wrapper for smooth and type-safe usage.

## Setup and Usage

1. Clone this Java wrapper project.
2. Build with Maven:

```bash
mvn clean package
```
3. Add the resulting .jar to your Java project’s classpath.

## Example Usage

```java
import org.kcramsolutions.jcerberus.TokenLib;

public class TokenExample {
    static {
        System.loadLibrary("jcerberus"); // Load native Rust library
    }

    public static void main(String[] args) throws Exception {
        TokenLib tokenLib = new TokenLib();

        String headerJson = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";
        String payloadJson = "{\"user\":\"alice\",\"exp\":1687000000}";
        String privateKeyPath = "/path/to/private_key.pem";
        String encryptedSymKeyPath = "/path/to/encrypted_sym_key.bin";

        String token = tokenLib.createToken(headerJson, payloadJson, privateKeyPath, encryptedSymKeyPath);
        System.out.println("Token: " + token);

        boolean valid = tokenLib.verify(token, "/path/to/public_key.pem");
        System.out.println("Is valid? " + valid);

        String dataJson = tokenLib.extractTokenData(token, encryptedSymKeyPath, privateKeyPath);
        System.out.println("Data: " + dataJson);
    }
}
```
Cerberus provides a built-in generic helper class named Standar (in package org.kcramsolutions.jcerberus.jwt) to simplify token operations such as creation, verification, and extraction.

This class enables you to:

- Use typed JSON objects for headers and payload.
- Handle token expiration (TTL) via the included Headers class.
- Easily create, verify, and extract token data with minimal boilerplate.

```java
JCerberus cerberus = new JCerberus();
Standar<MyPayloadClass> standardToken = new Standar<>(cerberus);

MyPayloadClass payload = new MyPayloadClass(...);
standardToken.setBody(payload);

String token = standardToken.create();

if (standardToken.verify(token)) {
    Standar.Token<MyPayloadClass> data = standardToken.extractAndVerify(token);
    System.out.println("Payload: " + data.getPayload());
}
```
You can extend or customize the Headers and Token inner classes as needed.