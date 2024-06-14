package com.syedriadh.cart.authenticationservice.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public final class AccessTokenRenewalRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1582113429381011729L;

    @NotNull
    private String refreshToken;
}
