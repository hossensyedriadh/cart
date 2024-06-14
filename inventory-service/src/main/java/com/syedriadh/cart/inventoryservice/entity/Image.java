package com.syedriadh.cart.inventoryservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Lob
    @Column(name = "cdn_url", nullable = false)
    private String cdnUrl;

    @Size(max = 35)
    @NotNull
    @Column(name = "uploaded_by", nullable = false, length = 35)
    private String uploadedBy;

    @NotNull
    @Column(name = "uploaded_on", nullable = false)
    private Long uploadedOn;
}