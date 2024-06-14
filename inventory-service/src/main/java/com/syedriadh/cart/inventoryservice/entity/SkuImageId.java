package com.syedriadh.cart.inventoryservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class SkuImageId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1866441669401624725L;

    @Size(max = 10)
    @NotNull
    @Column(name = "sku_ref", nullable = false, length = 10)
    private String skuRef;

    @NotNull
    @Column(name = "image_ref", nullable = false)
    private Long imageRef;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SkuImageId entity = (SkuImageId) o;
        return Objects.equals(this.skuRef, entity.skuRef) &&
                Objects.equals(this.imageRef, entity.imageRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skuRef, imageRef);
    }
}