package com.syedriadh.cart.authenticationservice.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
public final class IntrospectionRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 2998144729856933536L;

    @NotNull
    private String token;
}
