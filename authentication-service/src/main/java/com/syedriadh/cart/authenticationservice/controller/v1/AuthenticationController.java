package com.syedriadh.cart.authenticationservice.controller.v1;

import com.syedriadh.cart.authenticationservice.model.AccessTokenRenewalRequest;
import com.syedriadh.cart.authenticationservice.model.BearerTokenRequest;
import com.syedriadh.cart.authenticationservice.model.BearerTokenResponse;
import com.syedriadh.cart.authenticationservice.model.LogoutRequest;
import com.syedriadh.cart.authenticationservice.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/authentication", produces = {MediaType.APPLICATION_JSON_VALUE},
        consumes = {MediaType.APPLICATION_JSON_VALUE})
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@Validated @RequestBody BearerTokenRequest bearerTokenRequest) {
        BearerTokenResponse bearerTokenResponse = this.authenticationService.authenticate(bearerTokenRequest);
        return new ResponseEntity<>(bearerTokenResponse, HttpStatus.OK);
    }

    @PostMapping("/renew")
    public ResponseEntity<?> renewAuthentication(@Validated @RequestBody AccessTokenRenewalRequest renewalRequest) {
        BearerTokenResponse bearerTokenResponse = this.authenticationService.renewAuthentication(renewalRequest);
        return new ResponseEntity<>(bearerTokenResponse, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Validated @RequestBody LogoutRequest logoutRequest) {
        this.authenticationService.logout(logoutRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
