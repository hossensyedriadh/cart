package com.syedriadh.cart.authenticationservice.enumerator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    ROLE_ADMIN("Admin"),
    ROLE_SALES("Sales"),
    ROLE_CUSTOMER("Customer");

    private final String value;
}