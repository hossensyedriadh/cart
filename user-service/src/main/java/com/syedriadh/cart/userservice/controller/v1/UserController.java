package com.syedriadh.cart.userservice.controller.v1;

import com.syedriadh.cart.userservice.entity.User;
import com.syedriadh.cart.userservice.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/v1/users", produces = {MediaType.APPLICATION_JSON_VALUE})
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> users(Pageable pageable) {
        Page<User> users = this.userService.users(pageable);

        return users.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<?> users() {
        List<User> users = this.userService.users();

        return users.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> user(@PathVariable("username") String username) {
        Optional<User> user = this.userService.user(username);

        return user.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(user.get(), HttpStatus.OK);
    }

    @PostMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> save(@Validated @RequestBody User user) {
        User savedUser = this.userService.save(user);

        return user.getProfile().getId() != null ? new ResponseEntity<>(savedUser, HttpStatus.OK)
                : new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
}
