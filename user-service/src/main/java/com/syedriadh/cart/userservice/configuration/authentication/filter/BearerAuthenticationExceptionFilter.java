package com.syedriadh.cart.userservice.configuration.authentication.filter;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.syedriadh.cart.userservice.exception.ExpiredTokenException;
import com.syedriadh.cart.userservice.exception.GlobalErrorResponse;
import com.syedriadh.cart.userservice.exception.InvalidTokenException;
import com.syedriadh.cart.userservice.exception.MalformedTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@Component
public class BearerAuthenticationExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException | InvalidTokenException | ExpiredTokenException e) {
            log.error("JWT Exception: {}", e.getMessage());
            this.setErrorResponse(HttpStatus.UNAUTHORIZED, request, response, e);
        } catch (MalformedTokenException e) {
            log.error("Malformed Token Exception: {}", e.getMessage());
            this.setErrorResponse(HttpStatus.FORBIDDEN, request, response, e);
        } catch (RuntimeException e) {
            log.error("Exception occurred while processing JWT: {}", e.getMessage());
            this.setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, request, response, e);
        }
    }

    private void setErrorResponse(HttpStatus httpStatus, HttpServletRequest request,
                                  HttpServletResponse response, Throwable throwable) {
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(request, httpStatus, throwable);

        try {
            JsonMapper jsonMapper = new JsonMapper();
            String json = jsonMapper.writeValueAsString(errorResponse);
            response.setStatus(httpStatus.value());
            response.getWriter().write(json);
        } catch (IOException e) {
            log.error("JSON mapping exception: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
