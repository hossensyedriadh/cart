package com.syedriadh.cart.authenticationservice.controlleradvice.v1;

import com.syedriadh.cart.authenticationservice.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = {"com.syedriadh.cart.authenticationservice.controller"})
public class GlobalControllerAdvice {
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public GlobalControllerAdvice(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @ExceptionHandler({InvalidCredentialsException.class, ExpiredTokenException.class})
    public ResponseEntity<GlobalErrorResponse> handleInvalidCredentialsException(Exception e) {
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(httpServletRequest, HttpStatus.UNAUTHORIZED, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({UsernameNotFoundException.class, InvalidTokenException.class, ResourceException.class})
    public ResponseEntity<GlobalErrorResponse> handleUsernameNotFoundException(Exception e) {
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(httpServletRequest, HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAccountLockedException.class)
    public ResponseEntity<GlobalErrorResponse> handleUserAccountLockedException(UserAccountLockedException e) {
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(httpServletRequest, HttpStatus.FORBIDDEN, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> details = new ArrayList<>();
        for (var error : e.getFieldErrors()) {
            details.add(error.getField() + ": " + error.getDefaultMessage() + ", Rejected value: " + error.getRejectedValue());
        }

        ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse(httpServletRequest, HttpStatus.BAD_REQUEST, "Validation failed", details);

        return new ResponseEntity<>(validationErrorResponse, HttpStatus.BAD_REQUEST);
    }
}
