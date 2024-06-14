package com.syedriadh.cart.userservice.configuration.authentication.service;

import com.syedriadh.cart.userservice.entity.User;
import com.syedriadh.cart.userservice.exception.UserAccountLockedException;
import com.syedriadh.cart.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class BearerAuthenticationUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public BearerAuthenticationUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = this.userRepository.findById(username);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        User user = userOptional.get();

        UserDetails userDetails = new UserDetails() {
            @Serial
            private static final long serialVersionUID = 2689506735248600264L;

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));

                return authorities;
            }

            @Override
            public String getPassword() {
                return user.getPassphrase();
            }

            @Override
            public String getUsername() {
                return user.getUsername();
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return user.getIsAccountNotLocked();
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return !user.getIsDeleted();
            }
        };

        if (user.getIsDeleted()) {
            throw new UsernameNotFoundException("User not found");
        }

        if (!user.getIsAccountNotLocked()) {
            throw new UserAccountLockedException("User account locked");
        }

        return org.springframework.security.core.userdetails.User.withUserDetails(userDetails).build();
    }
}
