package com.syedriadh.cart.authenticationservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public final class BearerTokenResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -5457596208128853165L;

    private String accessToken;

    private String accessTokenType;

    private String refreshToken;
}
