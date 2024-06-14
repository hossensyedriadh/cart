package com.syedriadh.cart.authenticationservice.entity.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.syedriadh.cart.authenticationservice.enumerator.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_profile")
public final class UserProfile {
    @Id
    @Size(max = 36)
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @Size(max = 50)
    @NotNull
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Size(max = 50)
    @NotNull
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Email
    @Size(max = 255)
    @NotNull
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "is_email_verified", nullable = false)
    private boolean emailVerified;

    @Size(max = 18)
    @NotNull
    @Column(name = "phone", nullable = false, length = 18)
    private String phone;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "is_phone_verified", nullable = false)
    private boolean phoneVerified;

    @Size(max = 18)
    @Column(name = "alt_phone", length = 18)
    private String alternatePhone;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "is_alt_phone_verified", nullable = false)
    private boolean alternatePhoneVerified;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "member_since", updatable = false, nullable = false)
    private Date memberSince;

    @Setter(AccessLevel.NONE)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "last_modified_on")
    private Long lastModifiedOn;

    @PrePersist
    private void init() {
        this.id = UUID.randomUUID().toString();
        this.memberSince = Date.valueOf(LocalDate.now());
    }

    @PreUpdate
    private void update() {
        this.lastModifiedOn = Instant.now().toEpochMilli();
    }
}