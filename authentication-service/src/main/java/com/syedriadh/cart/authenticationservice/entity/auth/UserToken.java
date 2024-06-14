package com.syedriadh.cart.authenticationservice.entity.auth;

import com.syedriadh.cart.authenticationservice.enumerator.TokenType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_token")
public final class UserToken {
    @Id
    @Size(max = 36)
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @NotNull
    @Lob
    @Column(name = "token", updatable = false, nullable = false)
    private String token;

    @Pattern(regexp = "^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$", message = "Must be a valid IPv4 address")
    @NotNull
    @Column(name = "ip_addr", updatable = false, nullable = false, length = 15)
    private String ipAddress;

    @NotNull
    @Lob
    @Column(name = "user_agent", updatable = false, nullable = false)
    private String userAgent;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", updatable = false, nullable = false)
    private TokenType tokenType;

    @NotNull
    @Column(name = "created_on", updatable = false, nullable = false)
    private Long createdOn;

    @NotNull
    @Column(name = "expires_on", updatable = false, nullable = false)
    private Long expiresOn;

    @Size(max = 35)
    @NotNull
    @Column(name = "for_user", updatable = false, nullable = false, length = 35)
    private String forUser;

    @Column(name = "refresh_token_ref")
    private String refreshTokenReference;
}