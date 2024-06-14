package com.syedriadh.cart.userservice.controller.v1;

import com.syedriadh.cart.userservice.entity.User;
import com.syedriadh.cart.userservice.entity.UserProfile;
import com.syedriadh.cart.userservice.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/account", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/")
    public ResponseEntity<?> fetch() {
        User user = this.accountService.fetch();

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(@Validated @RequestBody UserProfile userProfile) {
        User user = this.accountService.update(userProfile);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
