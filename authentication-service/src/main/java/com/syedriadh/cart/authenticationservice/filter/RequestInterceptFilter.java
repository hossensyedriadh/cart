package com.syedriadh.cart.authenticationservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class RequestInterceptFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String url = request.getScheme().concat("://").concat(request.getServerName()).concat(":")
                .concat(String.valueOf(request.getLocalPort()));

        Locale locale = request.getLocale();
        Calendar calendar = Calendar.getInstance(locale);
        TimeZone timeZone = calendar.getTimeZone();
        TimeZone.setDefault(timeZone);

        request.setAttribute("clientTimeZone", timeZone.getID());

        response.addHeader("Served-By", url);

        filterChain.doFilter(request, response);
    }
}
