package com.syedriadh.cart.edgeservice.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.Instant;

@Getter
@Setter
public final class GlobalErrorResponse {
    private int status;

    private Long timestamp;

    private String message;

    private String error;

    private String path;

    public GlobalErrorResponse(ServerRequest serverRequest, HttpStatus httpStatus, String message) {
        this.timestamp = Instant.now().getEpochSecond();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
        this.path = serverRequest.path();
    }

    public GlobalErrorResponse(ServerRequest serverRequest, HttpStatus status, Throwable throwable) {
        this.timestamp = Instant.now().getEpochSecond();
        this.status = status.value();
        this.message = status.getReasonPhrase();
        this.error = (throwable.getCause() != null) ? throwable.getCause().getMessage() : throwable.getMessage();
        this.path = serverRequest.path();
    }
}
