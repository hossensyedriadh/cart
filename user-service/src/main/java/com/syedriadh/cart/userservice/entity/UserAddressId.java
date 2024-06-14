package com.syedriadh.cart.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public final class UserAddressId implements Serializable {
    @Serial
    private static final long serialVersionUID = 9005743767726438969L;

    @NotNull
    @Column(name = "address_ref", nullable = false)
    private Long addressRef;

    @Size(max = 35)
    @NotNull
    @Column(name = "user_ref", nullable = false, length = 35)
    private String userRef;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserAddressId entity = (UserAddressId) o;
        return Objects.equals(this.addressRef, entity.addressRef) &&
                Objects.equals(this.userRef, entity.userRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressRef, userRef);
    }
}