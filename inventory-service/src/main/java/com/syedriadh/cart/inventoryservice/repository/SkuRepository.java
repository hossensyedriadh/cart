package com.syedriadh.cart.inventoryservice.repository;

import com.syedriadh.cart.inventoryservice.entity.Sku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkuRepository extends JpaRepository<Sku, String> {
}