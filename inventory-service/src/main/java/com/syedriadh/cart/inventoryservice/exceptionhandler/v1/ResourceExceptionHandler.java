package com.syedriadh.cart.inventoryservice.exceptionhandler.v1;

import com.syedriadh.cart.inventoryservice.exception.GlobalErrorResponse;
import com.syedriadh.cart.inventoryservice.exception.ResourceException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"com.syedriadh.cart.inventoryservice.controller.v1"})
public class ResourceExceptionHandler {
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public ResourceExceptionHandler(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @ExceptionHandler(ResourceException.class)
    public ResponseEntity<GlobalErrorResponse> handleResourceException(ResourceException exception) {
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(this.httpServletRequest, exception.getHttpStatus(), exception.getMessage());

        return new ResponseEntity<>(errorResponse, exception.getHttpStatus());
    }
}
