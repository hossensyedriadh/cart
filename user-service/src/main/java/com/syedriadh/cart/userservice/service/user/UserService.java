package com.syedriadh.cart.userservice.service.user;

import com.syedriadh.cart.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Page<User> users(Pageable pageable);

    List<User> users();

    Optional<User> user(String username);

    User save(User user);
}