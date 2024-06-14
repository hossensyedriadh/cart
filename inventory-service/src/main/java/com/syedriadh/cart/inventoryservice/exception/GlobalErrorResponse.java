package com.syedriadh.cart.inventoryservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.TimeZone;

@Getter
@Setter
public final class GlobalErrorResponse {
    private int status;

    private Long timestamp;

    private String message;

    private String error;

    private String path;

    public GlobalErrorResponse(HttpServletRequest httpServletRequest, HttpStatus httpStatus, String message) {
        TimeZone timeZone = RequestContextUtils.getTimeZone(httpServletRequest);

        this.timestamp = timeZone != null ? Instant.now(Clock.system(ZoneId.of(timeZone.toZoneId().getId()))).getEpochSecond() : Instant.now().getEpochSecond();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
        this.path = httpServletRequest.getRequestURI();
    }

    public GlobalErrorResponse(HttpServletRequest httpServletRequest, HttpStatus status, Throwable throwable) {
        TimeZone timeZone = RequestContextUtils.getTimeZone(httpServletRequest);
        this.timestamp = timeZone != null ? Instant.now(Clock.system(ZoneId.of(timeZone.toZoneId().getId()))).getEpochSecond() : Instant.now().getEpochSecond();
        this.status = status.value();
        this.message = status.getReasonPhrase();
        this.error = (throwable.getCause() != null) ? throwable.getCause().getMessage() : throwable.getMessage();
        this.path = httpServletRequest.getRequestURI();
    }
}
