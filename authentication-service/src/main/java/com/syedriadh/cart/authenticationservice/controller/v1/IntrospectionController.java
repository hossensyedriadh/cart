package com.syedriadh.cart.authenticationservice.controller.v1;

import com.syedriadh.cart.authenticationservice.model.IntrospectionRequest;
import com.syedriadh.cart.authenticationservice.model.IntrospectionResponse;
import com.syedriadh.cart.authenticationservice.service.IntrospectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/introspection", produces = {MediaType.APPLICATION_JSON_VALUE},
        consumes = {MediaType.APPLICATION_JSON_VALUE})
public class IntrospectionController {
    private final IntrospectionService introspectionService;

    @Autowired
    public IntrospectionController(IntrospectionService introspectionService) {
        this.introspectionService = introspectionService;
    }

    @PostMapping("/introspect")
    public ResponseEntity<?> introspect(@Validated @RequestBody IntrospectionRequest introspectionRequest) {
        IntrospectionResponse introspectionResponse = this.introspectionService.introspect(introspectionRequest.getToken());

        return new ResponseEntity<>(introspectionResponse, HttpStatus.OK);
    }

    @PostMapping("/revoke")
    public ResponseEntity<?> revoke(@Validated @RequestBody IntrospectionRequest introspectionRequest) {
        this.introspectionService.revokeToken(introspectionRequest.getToken());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
