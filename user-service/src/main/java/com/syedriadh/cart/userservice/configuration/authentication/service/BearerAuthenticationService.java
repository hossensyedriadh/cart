package com.syedriadh.cart.userservice.configuration.authentication.service;

import com.syedriadh.cart.userservice.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Log4j2
@Service
public class BearerAuthenticationService {
    private static final String accessTokenType = "Access Token";

    private final JwtDecoder jwtDecoder;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public BearerAuthenticationService(JwtDecoder jwtDecoder, HttpServletRequest httpServletRequest) {
        this.jwtDecoder = jwtDecoder;
        this.httpServletRequest = httpServletRequest;
    }

    public boolean isAccessTokenValid(String token) {
        try {
            Jwt jwt = this.jwtDecoder.decode(token);

            return Objects.requireNonNull(jwt.getExpiresAt()).isAfter(Instant.now()) && jwt.getClaimAsString("tokenType").equals(accessTokenType)
                    && jwt.getNotBefore().isBefore(Instant.now());
        } catch (JwtException e) {
            log.error(e.getMessage());
            throw new InvalidTokenException(e.getMessage(), this.httpServletRequest);
        }
    }
}
