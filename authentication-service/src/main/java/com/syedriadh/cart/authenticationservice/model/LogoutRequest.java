package com.syedriadh.cart.authenticationservice.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public final class LogoutRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 6971013445055447290L;

    @NotNull
    private String accessToken;
}
