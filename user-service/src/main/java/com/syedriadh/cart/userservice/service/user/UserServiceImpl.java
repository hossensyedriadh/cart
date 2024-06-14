package com.syedriadh.cart.userservice.service.user;

import com.syedriadh.cart.userservice.entity.User;
import com.syedriadh.cart.userservice.enumerator.Role;
import com.syedriadh.cart.userservice.exception.ResourceException;
import com.syedriadh.cart.userservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, HttpServletRequest httpServletRequest) {
        this.userRepository = userRepository;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public Page<User> users(Pageable pageable) {
        return this.userRepository.findAll(pageable);
    }

    @Override
    public List<User> users() {
        return this.userRepository.findAll();
    }

    @Override
    public Optional<User> user(String username) {
        return this.userRepository.findById(username);
    }

    @Override
    public User save(User user) {
        if (user.getRole() != null && !user.getRole().equals(Role.ROLE_CUSTOMER)) {
            this.userRepository.saveAndFlush(user);
        }

        throw new ResourceException("Customer information can not be updated", HttpStatus.FORBIDDEN, this.httpServletRequest);
    }
}
