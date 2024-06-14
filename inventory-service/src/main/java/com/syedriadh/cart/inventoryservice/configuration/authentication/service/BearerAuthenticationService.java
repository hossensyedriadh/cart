package com.syedriadh.cart.inventoryservice.configuration.authentication.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Log4j2
@Component
public class BearerAuthenticationService {
    @Value("${bearer-authentication.keystore.public-key.path}")
    private String publicKeyPath;

    private RSAPublicKey rsaPublicKey() {
        try {
            File file = ResourceUtils.getFile(this.publicKeyPath);
            try (InputStream inputStream = new FileInputStream(file)) {
                byte[] bytes = inputStream.readAllBytes();
                String key = new String(bytes);

                key = key.replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replaceAll("\\s", "").trim();

                byte[] keyBytes = Base64.getDecoder().decode(key);

                X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PublicKey publicKey = keyFactory.generatePublic(spec);

                if (publicKey instanceof RSAPublicKey) {
                    return (RSAPublicKey) publicKey;
                }
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        throw new RuntimeException("Unable to load RSA Public Key");
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.rsaPublicKey()).build();
    }
}
