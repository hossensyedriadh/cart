package com.syedriadh.cart.analyticsservice.util;

import com.syedriadh.cart.analyticsservice.exception.ResourceException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class CurrentAuthenticatedPrincipal {
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public CurrentAuthenticatedPrincipal(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public String getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof UsernamePasswordAuthenticationToken && authentication.isAuthenticated()) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }

        throw new ResourceException("No authentication found", HttpStatus.UNAUTHORIZED, this.httpServletRequest);
    }
}
