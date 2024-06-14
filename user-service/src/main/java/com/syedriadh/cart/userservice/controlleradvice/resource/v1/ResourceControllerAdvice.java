package com.syedriadh.cart.userservice.controlleradvice.resource.v1;

import com.syedriadh.cart.userservice.exception.GlobalErrorResponse;
import com.syedriadh.cart.userservice.exception.ResourceException;
import com.syedriadh.cart.userservice.exception.ValidationErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice(basePackages = {"com.syedriadh.cart.userservice.controller.v1"})
public class ResourceControllerAdvice {
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public ResourceControllerAdvice(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @ExceptionHandler(ResourceException.class)
    public ResponseEntity<GlobalErrorResponse> handleResourceException(ResourceException e) {
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(this.httpServletRequest, e.getHttpStatus(), e);
        return new ResponseEntity<>(errorResponse, e.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> details = new ArrayList<>();
        for (var error : e.getFieldErrors()) {
            details.add(error.getField() + ": " + error.getDefaultMessage() + ", Rejected value: " + error.getRejectedValue());
        }

        ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse(this.httpServletRequest, HttpStatus.BAD_REQUEST, "Validation failed", details);

        return new ResponseEntity<>(validationErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<GlobalErrorResponse> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(this.httpServletRequest, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GlobalErrorResponse> handleRuntimeException(RuntimeException e) {
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(this.httpServletRequest, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
