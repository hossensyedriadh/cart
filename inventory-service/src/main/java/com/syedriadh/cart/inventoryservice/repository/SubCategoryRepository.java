package com.syedriadh.cart.inventoryservice.repository;

import com.syedriadh.cart.inventoryservice.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
}