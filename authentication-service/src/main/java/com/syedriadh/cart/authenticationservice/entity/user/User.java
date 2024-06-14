package com.syedriadh.cart.authenticationservice.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.syedriadh.cart.authenticationservice.enumerator.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "user")
public final class User {
    @Id
    @Size(max = 35)
    @Column(name = "username", updatable = false, nullable = false, length = 35)
    private String username;

    @JsonIgnore
    @Size(max = 60)
    @NotNull
    @Column(name = "passphrase", nullable = false, length = 60)
    private String passphrase;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", updatable = false, nullable = false)
    private Role role;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @NotNull
    @Column(name = "is_account_not_locked", nullable = false)
    private Boolean isAccountNotLocked;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "is_mfa_enabled", nullable = false)
    private Boolean isMfaEnabled;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Size(max = 72)
    @Column(name = "mfa_secret", length = 72)
    private String mfaSecret;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, optional = false)
    @JoinColumn(name = "profile_ref", referencedColumnName = "id", nullable = false)
    private UserProfile profileRef;
}