package com.syedriadh.cart.authenticationservice.service;

import com.syedriadh.cart.authenticationservice.configuration.authentication.BearerAuthenticationService;
import com.syedriadh.cart.authenticationservice.configuration.authentication.BearerAuthenticationUserDetailsService;
import com.syedriadh.cart.authenticationservice.entity.auth.UserToken;
import com.syedriadh.cart.authenticationservice.entity.user.User;
import com.syedriadh.cart.authenticationservice.enumerator.JwtClaim;
import com.syedriadh.cart.authenticationservice.enumerator.TokenType;
import com.syedriadh.cart.authenticationservice.exception.InvalidCredentialsException;
import com.syedriadh.cart.authenticationservice.exception.InvalidTokenException;
import com.syedriadh.cart.authenticationservice.exception.ResourceException;
import com.syedriadh.cart.authenticationservice.model.AccessTokenRenewalRequest;
import com.syedriadh.cart.authenticationservice.model.BearerTokenRequest;
import com.syedriadh.cart.authenticationservice.model.BearerTokenResponse;
import com.syedriadh.cart.authenticationservice.model.LogoutRequest;
import com.syedriadh.cart.authenticationservice.repository.auth.UserTokenRepository;
import com.syedriadh.cart.authenticationservice.repository.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final BearerAuthenticationService bearerAuthenticationService;
    private final BearerAuthenticationUserDetailsService bearerAuthenticationUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final NimbusJwtDecoder jwtDecoder;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository, UserTokenRepository userTokenRepository,
                                     BearerAuthenticationService bearerAuthenticationService,
                                     BearerAuthenticationUserDetailsService bearerAuthenticationUserDetailsService,
                                     PasswordEncoder passwordEncoder, RSAPublicKey rsaPublicKey,
                                     HttpServletRequest httpServletRequest) {
        this.userRepository = userRepository;
        this.userTokenRepository = userTokenRepository;
        this.bearerAuthenticationService = bearerAuthenticationService;
        this.bearerAuthenticationUserDetailsService = bearerAuthenticationUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public BearerTokenResponse authenticate(BearerTokenRequest bearerTokenRequest) {
        Optional<User> userOptional = this.userRepository.findById(bearerTokenRequest.getPrincipal());

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + bearerTokenRequest.getPrincipal());
        }

        User user = userOptional.get();

        UserDetails userDetails = this.bearerAuthenticationUserDetailsService.loadUserByUsername(user.getUsername());

        if (this.passwordEncoder.matches(bearerTokenRequest.getPassphrase(), userDetails.getPassword())) {
            String accessToken = this.bearerAuthenticationService.getAccessToken(userDetails.getUsername());
            String refreshToken = this.bearerAuthenticationService.getRefreshToken(userDetails.getUsername());

            this.bindAccessTokenWithRefreshToken(accessToken, refreshToken);

            return new BearerTokenResponse(accessToken, "Bearer", refreshToken);
        } else {
            throw new InvalidCredentialsException("Invalid login credentials", this.httpServletRequest);
        }
    }

    @Override
    public BearerTokenResponse renewAuthentication(AccessTokenRenewalRequest tokenRenewalRequest) {
        if (this.bearerAuthenticationService.isRefreshTokenValid(tokenRenewalRequest.getRefreshToken())) {
            Jwt decodedRefreshToken = this.jwtDecoder.decode(tokenRenewalRequest.getRefreshToken());

            String accessToken = this.bearerAuthenticationService.getAccessToken(decodedRefreshToken.getSubject());
            String refreshToken = this.bearerAuthenticationService.getRefreshToken(decodedRefreshToken.getSubject());

            this.bindAccessTokenWithRefreshToken(accessToken, refreshToken);

            return new BearerTokenResponse(accessToken, "Bearer", refreshToken);
        }

        throw new InvalidTokenException("Invalid refresh token", this.httpServletRequest);
    }

    @Transactional("authTransactionManager")
    @Override
    public void logout(LogoutRequest logoutRequest) {
        if (this.bearerAuthenticationService.isAccessTokenValid(logoutRequest.getAccessToken())) {
            Jwt decodedAccessToken = this.jwtDecoder.decode(logoutRequest.getAccessToken());

            String tokenId = decodedAccessToken.getId();
            String tokenType = decodedAccessToken.getClaimAsString(JwtClaim.TOKEN_TYPE.getClaimName());
            Optional<UserToken> userTokenOptional = this.userTokenRepository.findById(tokenId);

            if (userTokenOptional.isPresent() && tokenType.equals(TokenType.ACCESS_TOKEN.toString())) {
                UserToken userToken = userTokenOptional.get();
                String refreshTokenId = userToken.getRefreshTokenReference();

                if (refreshTokenId != null) {
                    this.userTokenRepository.deleteAllByIdInBatch(List.of(tokenId, refreshTokenId));
                } else {
                    this.userTokenRepository.deleteById(tokenId);
                }
            } else {
                throw new ResourceException("Expected access token, received refresh token", HttpStatus.BAD_REQUEST, this.httpServletRequest);
            }
        } else {
            throw new InvalidTokenException("Invalid/Missing access token", this.httpServletRequest);
        }
    }

    @Async
    protected void bindAccessTokenWithRefreshToken(String accessToken, String refreshToken) {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            executorService.submit(() -> {
                completableFuture.completeAsync(() -> {
                    Optional<UserToken> accessTokenOptional = this.userTokenRepository.findByTokenTypeAndToken(TokenType.ACCESS_TOKEN, accessToken);
                    Optional<UserToken> refreshTokenOptional = this.userTokenRepository.findByTokenTypeAndToken(TokenType.REFRESH_TOKEN, refreshToken);

                    if (accessTokenOptional.isPresent() && refreshTokenOptional.isPresent()) {
                        UserToken userAccessToken = accessTokenOptional.get();
                        UserToken userRefreshToken = refreshTokenOptional.get();

                        if (userAccessToken.getRefreshTokenReference() == null) {
                            userAccessToken.setRefreshTokenReference(userRefreshToken.getId());
                            this.userTokenRepository.saveAndFlush(userAccessToken);
                        }
                    }

                    return null;
                });
            });
        }
    }
}
