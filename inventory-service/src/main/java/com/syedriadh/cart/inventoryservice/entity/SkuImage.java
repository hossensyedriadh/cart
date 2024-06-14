package com.syedriadh.cart.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sku_image")
public class SkuImage {
    @EmbeddedId
    private SkuImageId id;

    @MapsId("imageRef")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "image_ref", nullable = false)
    private Image imageRef;
}