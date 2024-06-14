package com.syedriadh.cart.inventoryservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "sku")
public class Sku {
    @Id
    @Size(max = 10)
    @Column(name = "id", nullable = false, length = 10)
    private String id;

    @Lob
    @Column(name = "specifications")
    private String specifications;

    @PositiveOrZero
    @NotNull
    @Column(name = "available_stock", nullable = false)
    private Long availableStock;

    @PositiveOrZero
    @NotNull
    @Column(name = "price_per_unit", nullable = false, precision = 8, scale = 2)
    private BigDecimal pricePerUnit;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "sku_image", joinColumns = @JoinColumn(name = "sku_ref"), inverseJoinColumns = @JoinColumn(name = "image_ref"))
    private Set<Image> images;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "product_ref", nullable = false)
    private Product productRef;

    @CreatedBy
    @Size(max = 35)
    @NotNull
    @Column(name = "added_by", nullable = false, length = 35)
    private String addedBy;

    @CreatedDate
    @NotNull
    @Column(name = "added_on", nullable = false)
    private Long addedOn;

    @LastModifiedBy
    @Size(max = 35)
    @Column(name = "last_modified_by", length = 35)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_on")
    private Long lastModifiedOn;
}