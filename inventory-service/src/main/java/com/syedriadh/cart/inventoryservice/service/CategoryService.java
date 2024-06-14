package com.syedriadh.cart.inventoryservice.service;

import com.syedriadh.cart.inventoryservice.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    Page<Category> categories(Pageable pageable);

    List<Category> categories();

    Category add(Category category);

    Category update(Category category);
}
