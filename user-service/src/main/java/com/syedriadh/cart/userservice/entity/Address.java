package com.syedriadh.cart.userservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Entity
@Table(name = "address")
public final class Address implements Serializable {
    @Serial
    private static final long serialVersionUID = 3880583599756758388L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotNull
    @Column(name = "address_line_1", nullable = false, length = 50)
    private String addressLine1;

    @Size(max = 50)
    @Column(name = "address_line_2", length = 50)
    private String addressLine2;

    @Size(max = 50)
    @Column(name = "address_line_3", length = 50)
    private String addressLine3;

    @NotNull
    @Column(name = "city", nullable = false)
    private Long city;

    @Size(max = 2)
    @NotNull
    @Column(name = "country", nullable = false, length = 2)
    private String country;

    @NotNull
    @Column(name = "is_billing_address", nullable = false)
    private boolean billingAddress;

    @NotNull
    @Column(name = "is_shipping_address", nullable = false)
    private boolean shippingAddress;

    @NotNull
    @Column(name = "is_default", nullable = false)
    private boolean defaultAddress;
}