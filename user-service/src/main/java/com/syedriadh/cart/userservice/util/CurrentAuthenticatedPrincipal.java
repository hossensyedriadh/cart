package com.syedriadh.cart.userservice.util;

import com.syedriadh.cart.userservice.entity.User;
import com.syedriadh.cart.userservice.exception.ResourceException;
import com.syedriadh.cart.userservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrentAuthenticatedPrincipal {
    private final UserRepository userRepository;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public CurrentAuthenticatedPrincipal(UserRepository userRepository, HttpServletRequest httpServletRequest) {
        this.userRepository = userRepository;
        this.httpServletRequest = httpServletRequest;
    }

    public User getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof UsernamePasswordAuthenticationToken && authentication.isAuthenticated()) {
            String principalName = ((UserDetails) authentication.getPrincipal()).getUsername();

            Optional<User> user = this.userRepository.findById(principalName);

            if (user.isPresent()) {
                return user.get();
            }

            throw new UsernameNotFoundException("User not found");
        }

        throw new ResourceException("No authentication found", HttpStatus.UNAUTHORIZED, this.httpServletRequest);
    }
}
