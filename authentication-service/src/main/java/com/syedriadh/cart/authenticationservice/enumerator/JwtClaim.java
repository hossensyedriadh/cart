package com.syedriadh.cart.authenticationservice.enumerator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JwtClaim {
    TOKEN_TYPE("tokenType"),
    FULL_NAME("fullName"),
    ROLE("role"),
    PERMISSIONS("permissions");

    private final String claimName;
}
