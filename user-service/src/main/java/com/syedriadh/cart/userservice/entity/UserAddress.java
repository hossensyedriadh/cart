package com.syedriadh.cart.userservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_address")
public final class UserAddress implements Serializable {
    @Serial
    private static final long serialVersionUID = -1941192481360700619L;

    @EmbeddedId
    private UserAddressId id;

    @NotNull
    @MapsId("addressRef")
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Address.class, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "address_ref", referencedColumnName = "id", nullable = false)
    private Address address;

    @NotNull
    @MapsId("userRef")
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class, cascade = {CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "user_ref", referencedColumnName = "username", nullable = false)
    private User user;
}