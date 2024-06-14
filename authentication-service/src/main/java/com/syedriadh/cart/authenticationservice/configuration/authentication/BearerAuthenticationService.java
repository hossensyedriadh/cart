package com.syedriadh.cart.authenticationservice.configuration.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.syedriadh.cart.authenticationservice.entity.auth.UserToken;
import com.syedriadh.cart.authenticationservice.entity.user.User;
import com.syedriadh.cart.authenticationservice.entity.user.UserProfile;
import com.syedriadh.cart.authenticationservice.enumerator.JwtClaim;
import com.syedriadh.cart.authenticationservice.enumerator.TokenType;
import com.syedriadh.cart.authenticationservice.exception.UserAccountLockedException;
import com.syedriadh.cart.authenticationservice.repository.auth.UserTokenRepository;
import com.syedriadh.cart.authenticationservice.repository.user.UserRepository;
import com.syedriadh.cart.authenticationservice.util.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Transactional(transactionManager = "authTransactionManager")
@Log4j2
@Service
public class BearerAuthenticationService {
    private static final String accessTokenType = "Access Token";
    private static final String refreshTokenType = "Refresh Token";

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final HttpServletRequest httpServletRequest;
    private final JwtDecoder jwtDecoder;
    private final RSAPublicKey rsaPublicKey;
    private final RSAPrivateKey rsaPrivateKey;

    @Autowired
    public BearerAuthenticationService(UserRepository userRepository, UserTokenRepository userTokenRepository, JwtDecoder jwtDecoder,
                                       HttpServletRequest httpServletRequest, RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {
        this.userRepository = userRepository;
        this.userTokenRepository = userTokenRepository;
        this.jwtDecoder = jwtDecoder;
        this.httpServletRequest = httpServletRequest;
        this.rsaPublicKey = rsaPublicKey;
        this.rsaPrivateKey = rsaPrivateKey;
    }

    @Value("${bearer-authentication.token.access-token-validity}")
    private Duration accessTokenValidity;

    @Value("${bearer-authentication.token.refresh-token-validity}")
    private Duration refreshTokenValidity;

    public boolean isAccessTokenValid(String token) {
        return isTokenValid(token, accessTokenType);
    }

    public boolean isRefreshTokenValid(String token) {
        return isTokenValid(token, refreshTokenType);
    }

    private boolean isTokenValid(String token, String tokenType) {
        try {
            Jwt decodedJwt = this.jwtDecoder.decode(token);

            String tokenId = decodedJwt.getId();
            String principal = decodedJwt.getSubject();
            String type = decodedJwt.getClaim(JwtClaim.TOKEN_TYPE.getClaimName());

            if (this.userRepository.findById(principal).isPresent()) {
                User user = this.userRepository.findById(principal).get();
                if (user.getIsDeleted()) {
                    throw new UsernameNotFoundException("User not found");
                }

                if (!user.getIsAccountNotLocked()) {
                    throw new UserAccountLockedException("User account locked");
                }

                Optional<UserToken> userTokenOptional = this.userTokenRepository.findById(tokenId);

                if (userTokenOptional.isPresent()) {
                    String userAgent = this.httpServletRequest.getHeader(HttpHeaders.USER_AGENT);

                    UserToken userToken = userTokenOptional.get();

                    if (userToken.getToken().equals(token) && userToken.getUserAgent().equals(userAgent)) {
                        return Objects.requireNonNull(decodedJwt.getExpiresAt()).isAfter(Instant.now()) && type.equals(tokenType);
                    }
                }
            }

            return false;
        } catch (Exception e) {
            log.error(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
            return false;
        }
    }

    public String getAccessToken(String principal) {
        String ipAddress = new HttpUtils(this.httpServletRequest).parseClientAddress();
        String userAgent = this.httpServletRequest.getHeader(HttpHeaders.USER_AGENT);

        Optional<UserToken> userAccessToken = this.userTokenRepository.findByForUserAndIpAddressAndUserAgentAndTokenType(principal,
                ipAddress, userAgent, TokenType.ACCESS_TOKEN);

        if (userAccessToken.isPresent()) {
            UserToken accessToken = userAccessToken.get();

            try {
                if (this.isAccessTokenValid(accessToken.getToken())) {
                    return accessToken.getToken();
                }

                return this.generateAccessToken(principal, ipAddress, userAgent);
            } catch (JWTVerificationException e) {
                log.warn("Invalid access token retrieved from database");
                this.userTokenRepository.delete(accessToken);
                return this.generateAccessToken(principal, ipAddress, userAgent);
            }
        }

        return this.generateAccessToken(principal, ipAddress, userAgent);
    }

    private String generateAccessToken(String principal, String ipAddress, String userAgent) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Instant.now().toEpochMilli() + this.accessTokenValidity.toMillis());


        UserProfile profile = this.userRepository.findById(principal).orElseThrow().getProfileRef();

        String tokenId = UUID.randomUUID().toString();

        JWTCreator.Builder accessTokenBuilder = JWT.create().withSubject(principal);
        accessTokenBuilder.withClaim(JwtClaim.TOKEN_TYPE.getClaimName(), accessTokenType);
        accessTokenBuilder.withClaim(JwtClaim.FULL_NAME.getClaimName(), profile.getFirstName().concat(" ").concat(profile.getLastName()));
        accessTokenBuilder.withClaim(JwtClaim.ROLE.getClaimName(), this.getUser(principal).getRole().toString());

        String accessToken = accessTokenBuilder.withNotBefore(Instant.now()).withIssuedAt(Instant.now())
                .withIssuer(getIssuer())
                .withExpiresAt(calendar.getTime()).withJWTId(tokenId)
                .sign(Algorithm.RSA256(this.rsaPublicKey, this.rsaPrivateKey));

        this.persistAccessToken(principal, tokenId, accessToken, ipAddress, userAgent);

        return accessToken;
    }

    public String getRefreshToken(String principal) {
        String ipAddress = new HttpUtils(this.httpServletRequest).parseClientAddress();
        String userAgent = this.httpServletRequest.getHeader(HttpHeaders.USER_AGENT);

        Optional<UserToken> userRefreshToken = this.userTokenRepository.findByForUserAndIpAddressAndUserAgentAndTokenType(principal,
                ipAddress, userAgent, TokenType.REFRESH_TOKEN);

        if (userRefreshToken.isPresent()) {
            UserToken refreshToken = userRefreshToken.get();

            try {
                if (this.isRefreshTokenValid(refreshToken.getToken())) {
                    return refreshToken.getToken();
                }

                return this.generateRefreshToken(principal, ipAddress, userAgent);
            } catch (JWTVerificationException e) {
                log.warn("Invalid refresh token retrieved from database, generating new token...");
                this.userTokenRepository.delete(refreshToken);
                return this.generateRefreshToken(principal, ipAddress, userAgent);
            }
        }

        return this.generateRefreshToken(principal, ipAddress, userAgent);
    }

    private String generateRefreshToken(String principal, String ipAddress, String userAgent) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Instant.now().toEpochMilli() + this.refreshTokenValidity.toMillis());

        UserProfile profile = this.userRepository.findById(principal).orElseThrow().getProfileRef();

        String tokenId = UUID.randomUUID().toString();

        JWTCreator.Builder refreshTokenBuilder = JWT.create().withSubject(principal);
        refreshTokenBuilder.withClaim(JwtClaim.TOKEN_TYPE.getClaimName(), refreshTokenType);
        refreshTokenBuilder.withClaim(JwtClaim.FULL_NAME.getClaimName(), profile.getFirstName().concat(" ").concat(profile.getLastName()));
        refreshTokenBuilder.withClaim(JwtClaim.ROLE.getClaimName(), this.getUser(principal).getRole().toString());

        String token = refreshTokenBuilder.withNotBefore(Instant.now()).withIssuedAt(Instant.now())
                .withIssuer(getIssuer()).withExpiresAt(calendar.getTime())
                .withJWTId(tokenId).sign(Algorithm.RSA256(this.rsaPublicKey, this.rsaPrivateKey));

        this.persistRefreshToken(principal, tokenId, token, ipAddress, userAgent);

        return token;
    }

    private User getUser(String username) {
        if (this.userRepository.findById(username).isPresent()) {
            return this.userRepository.findById(username).get();
        }
        throw new UsernameNotFoundException("User not found");
    }

    private void persistAccessToken(String principal, String jwtId, String token, String ipAddress, String userAgent) {
        UserToken accessToken = new UserToken(jwtId, token, ipAddress, userAgent, TokenType.ACCESS_TOKEN, Instant.now().toEpochMilli(),
                Instant.now().toEpochMilli() + this.accessTokenValidity.toMillis(), principal, null);
        this.userTokenRepository.saveAndFlush(accessToken);
    }

    private void persistRefreshToken(String principal, String jwtId, String token, String ipAddress, String userAgent) {
        UserToken refreshToken = new UserToken(jwtId, token, ipAddress, userAgent, TokenType.REFRESH_TOKEN, Instant.now().toEpochMilli(),
                Instant.now().toEpochMilli() + this.refreshTokenValidity.toMillis(), principal, null);
        this.userTokenRepository.saveAndFlush(refreshToken);
    }

    private String getIssuer() {
        return this.httpServletRequest.getScheme().concat("://").concat(this.httpServletRequest.getServerName())
                .concat(":").concat(String.valueOf(this.httpServletRequest.getLocalPort()));
    }
}
