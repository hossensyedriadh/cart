package com.syedriadh.cart.edgeservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.oauth2.jwt.Jwt;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public final class TokenValidationResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -4758240659290107657L;

    private Boolean isValid;

    private Jwt jwt;
}
