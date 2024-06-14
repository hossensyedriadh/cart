package com.syedriadh.cart.authenticationservice.service;

import com.syedriadh.cart.authenticationservice.entity.auth.UserToken;
import com.syedriadh.cart.authenticationservice.exception.ResourceException;
import com.syedriadh.cart.authenticationservice.model.IntrospectionResponse;
import com.syedriadh.cart.authenticationservice.repository.auth.UserTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@Service
public class IntrospectionServiceImpl implements IntrospectionService {
    private final UserTokenRepository userTokenRepository;
    private final JwtDecoder jwtDecoder;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public IntrospectionServiceImpl(UserTokenRepository userTokenRepository, JwtDecoder jwtDecoder, HttpServletRequest httpServletRequest) {
        this.userTokenRepository = userTokenRepository;
        this.jwtDecoder = jwtDecoder;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public IntrospectionResponse introspect(String token) {
        IntrospectionResponse response = new IntrospectionResponse();

        try {
            Jwt decodedJwt = this.jwtDecoder.decode(token);

            Optional<UserToken> userTokenOptional = this.userTokenRepository.findById(decodedJwt.getId());

            if (userTokenOptional.isEmpty()) {
                response.setIsValid(false);
                return response;
            }

            response.setIsValid(Objects.requireNonNull(decodedJwt.getExpiresAt()).isAfter(Instant.now()));
            response.setJwt(decodedJwt);
        } catch (Exception e) {
            response.setIsValid(false);
        }

        return response;
    }

    @Override
    public void revokeToken(String token) {
        try {
            Jwt decodedJwt = this.jwtDecoder.decode(token);

            String tokenId = decodedJwt.getId();

            Optional<UserToken> userTokenOptional = this.userTokenRepository.findById(tokenId);

            if (userTokenOptional.isPresent()) {
                this.userTokenRepository.deleteById(tokenId);
            } else {
                throw new ResourceException("Token not found with id: " + tokenId, HttpStatus.BAD_REQUEST, this.httpServletRequest);
            }
        } catch (Exception e) {
            throw new ResourceException(e.getMessage(), HttpStatus.BAD_REQUEST, this.httpServletRequest);
        }
    }
}
