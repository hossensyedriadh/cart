package com.syedriadh.cart.inventoryservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.time.Clock;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

@Getter
@Setter
public class ValidationErrorResponse {
    private int status;

    private Long timestamp;

    private String message;

    private String error;

    private String path;

    private List<String> details;

    public ValidationErrorResponse(HttpServletRequest httpServletRequest, HttpStatus httpStatus, String message, List<String> details) {
        TimeZone timeZone = RequestContextUtils.getTimeZone(httpServletRequest);
        this.timestamp = timeZone != null ? Long.now(Clock.system(ZoneId.of(timeZone.toZoneId().getId()))).getEpochSecond() : Long.now().getEpochSecond();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
        this.details = details;
        this.path = httpServletRequest.getRequestURI();
    }
}
