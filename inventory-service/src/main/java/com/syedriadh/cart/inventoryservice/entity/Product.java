package com.syedriadh.cart.inventoryservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 120)
    @NotNull
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "sub_category", referencedColumnName = "id")
    private SubCategory subCategory;

    @NotNull
    @Column(name = "on_hold", nullable = false)
    private Boolean onHold = false;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "productRef", orphanRemoval = true)
    private Set<Sku> skus;

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