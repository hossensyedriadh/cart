package com.syedriadh.cart.authenticationservice.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public final class BearerTokenRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 8142704367063392275L;

    @NotNull
    private String principal;

    @NotNull
    private String passphrase;
}
