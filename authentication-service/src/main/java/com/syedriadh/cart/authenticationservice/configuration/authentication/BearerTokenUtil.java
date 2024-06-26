package com.syedriadh.cart.authenticationservice.configuration.authentication;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Log4j2
@Component
public class BearerTokenUtil {
    @Value("${bearer-authentication.keystore.location}")
    private String keyStorePath;

    @Value("${bearer-authentication.keystore.passphrase}")
    private String keyStorePassphrase;

    @Value("${bearer-authentication.keystore.key-alias}")
    private String keyAlias;

    @Value("${bearer-authentication.keystore.private-key-passphrase}")
    private String privateKeyPassphrase;

    @Bean
    public KeyStore keyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream inputStream = new FileInputStream(this.keyStorePath);
            keyStore.load(inputStream, this.keyStorePassphrase.toCharArray());

            return keyStore;
        } catch (KeyStoreException e) {
            log.error("Unable to load keystore: ", e);
            throw new RuntimeException("Unable to load keystore", e);
        } catch (CertificateException e) {
            log.error("Invalid Certificate: ", e);
            throw new RuntimeException("Invalid Certificate", e);
        } catch (IOException e) {
            log.error("IO exception: ", e);
            throw new RuntimeException("IO exception", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("Algorithm not found: ", e);
            throw new RuntimeException("Algorithm not found", e);
        }
    }

    @Bean
    public RSAPrivateKey tokenSigningKey(KeyStore keyStore) {
        try {
            Key key = keyStore.getKey(this.keyAlias, this.privateKeyPassphrase.toCharArray());

            if (key instanceof RSAPrivateKey) {
                return (RSAPrivateKey) key;
            }
        } catch (UnrecoverableKeyException e) {
            log.error("Unrecoverable key exception: ", e);
            throw new RuntimeException("Unrecoverable key", e);
        } catch (KeyStoreException e) {
            log.error("Keystore exception: ", e);
            throw new RuntimeException("Bad keystore", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("Algorithm not found: ", e);
            throw new RuntimeException("Algorithm not found", e);
        }

        throw new RuntimeException("Invalid RSA Private Key");
    }

    @Bean
    public RSAPublicKey tokenValidationKey(KeyStore keyStore) {
        try {
            Certificate certificate = keyStore.getCertificate(this.keyAlias);
            PublicKey publicKey = certificate.getPublicKey();

            if (publicKey instanceof RSAPublicKey) {
                return (RSAPublicKey) publicKey;
            }
        } catch (KeyStoreException e) {
            log.error("Keystore exception: ", e);
            throw new RuntimeException("Bad Keystore", e);
        }

        throw new RuntimeException("Unable to load RSA Public Key");
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey rsaPublicKey) {
        return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
    }
}
