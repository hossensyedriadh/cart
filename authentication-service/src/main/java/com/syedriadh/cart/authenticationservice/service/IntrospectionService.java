package com.syedriadh.cart.authenticationservice.service;

import com.syedriadh.cart.authenticationservice.model.IntrospectionResponse;

public interface IntrospectionService {
    IntrospectionResponse introspect(String token);

    void revokeToken(String token);
}
