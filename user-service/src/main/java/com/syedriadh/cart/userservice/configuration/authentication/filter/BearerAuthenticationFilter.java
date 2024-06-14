package com.syedriadh.cart.userservice.configuration.authentication.filter;

import com.syedriadh.cart.userservice.configuration.authentication.service.BearerAuthenticationService;
import com.syedriadh.cart.userservice.configuration.authentication.service.BearerAuthenticationUserDetailsService;
import com.syedriadh.cart.userservice.exception.ExpiredTokenException;
import com.syedriadh.cart.userservice.exception.InvalidTokenException;
import com.syedriadh.cart.userservice.exception.MalformedTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class BearerAuthenticationFilter extends OncePerRequestFilter {
    private static final String accessTokenPrefix = "Bearer ";

    private final BearerAuthenticationUserDetailsService bearerAuthenticationUserDetailsService;
    private final BearerAuthenticationService bearerAuthenticationService;
    private final JwtDecoder jwtDecoder;

    @Autowired
    public BearerAuthenticationFilter(BearerAuthenticationUserDetailsService bearerAuthenticationUserDetailsService,
                                      BearerAuthenticationService bearerAuthenticationService, JwtDecoder jwtDecoder) {
        this.bearerAuthenticationUserDetailsService = bearerAuthenticationUserDetailsService;
        this.bearerAuthenticationService = bearerAuthenticationService;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getHeader(HttpHeaders.AUTHORIZATION) != null) {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String principal;
            String accessToken;

            if (authHeader != null) {
                if (authHeader.startsWith(accessTokenPrefix)) {
                    accessToken = authHeader.substring(accessTokenPrefix.length());
                    try {
                        principal = this.jwtDecoder.decode(accessToken).getSubject();
                    } catch (JwtException e) {
                        throw new ExpiredTokenException("Access token expired", request);
                    } catch (IllegalArgumentException e) {
                        throw new JwtException("Unable to parse access token", e);
                    }
                } else {
                    throw new MalformedTokenException("Access token should be prepended with " +
                            "access token type, i.e.: 'Bearer <token>'", request);
                }
            } else {
                throw new InvalidTokenException("Missing / Invalid access token", request);
            }

            if (!this.bearerAuthenticationService.isAccessTokenValid(accessToken)) {
                throw new InvalidTokenException("Invalid access token");
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (principal != null && (authentication == null || authentication instanceof AnonymousAuthenticationToken)) {
                UserDetails userDetails = this.bearerAuthenticationUserDetailsService.loadUserByUsername(principal);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
