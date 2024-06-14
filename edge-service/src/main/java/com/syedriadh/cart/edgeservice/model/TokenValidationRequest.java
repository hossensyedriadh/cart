package com.syedriadh.cart.edgeservice.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public final class TokenValidationRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 2998144729856933536L;

    @NotNull
    private String token;

    @Override
    public String toString() {
        return "TokenValidationRequest{" +
                "token='" + token + '\'' +
                '}';
    }
}
