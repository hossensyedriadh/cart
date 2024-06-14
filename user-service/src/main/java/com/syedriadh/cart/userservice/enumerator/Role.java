package com.syedriadh.cart.userservice.enumerator;

import lombok.Getter;

@Getter
public enum Role {
    ROLE_ADMIN ("Admin"),
    ROLE_SALES ("Sales"),
    ROLE_CUSTOMER ("Customer");

    private final String value;

    Role(String value) {
        this.value = value;
    }
}