package com.syedriadh.cart.inventoryservice.configuration.handler;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.syedriadh.cart.inventoryservice.exception.GlobalErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Component
public class ResourceAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        TimeZone timeZone = RequestContextUtils.getTimeZone(request);
        response.setHeader(HttpHeaders.DATE, String.valueOf(Date.from(Instant.now(Clock.system(timeZone != null ? ZoneId.of(timeZone.toZoneId().getId()) : ZoneId.systemDefault())))));
        response.setLocale(Locale.ENGLISH);

        GlobalErrorResponse errorResponse = new GlobalErrorResponse(request, HttpStatus.FORBIDDEN, "You do not have appropriate permission(s) to access this resource");

        JsonMapper jsonMapper = new JsonMapper();
        String json = jsonMapper.writeValueAsString(errorResponse);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(json);
    }
}
