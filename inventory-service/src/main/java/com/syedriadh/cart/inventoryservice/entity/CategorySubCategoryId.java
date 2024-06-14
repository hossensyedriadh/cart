package com.syedriadh.cart.inventoryservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class CategorySubCategoryId implements Serializable {
    @Serial
    private static final long serialVersionUID = -7360058437470755744L;

    @NotNull
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @NotNull
    @Column(name = "sub_category_id", nullable = false)
    private Long subCategoryId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CategorySubCategoryId entity = (CategorySubCategoryId) o;
        return Objects.equals(this.subCategoryId, entity.subCategoryId) &&
                Objects.equals(this.categoryId, entity.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subCategoryId, categoryId);
    }
}