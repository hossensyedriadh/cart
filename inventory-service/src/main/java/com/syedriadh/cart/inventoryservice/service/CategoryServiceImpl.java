package com.syedriadh.cart.inventoryservice.service;

import com.syedriadh.cart.inventoryservice.entity.Category;
import com.syedriadh.cart.inventoryservice.exception.ResourceException;
import com.syedriadh.cart.inventoryservice.repository.CategoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, HttpServletRequest httpServletRequest) {
        this.categoryRepository = categoryRepository;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public Page<Category> categories(Pageable pageable) {
        return this.categoryRepository.findAll(pageable);
    }

    @Override
    public List<Category> categories() {
        return this.categoryRepository.findAll();
    }

    @Override
    public Category add(Category category) {
        return this.categoryRepository.saveAndFlush(category);
    }

    @Override
    public Category update(Category category) {
        if (this.categoryRepository.findById(category.getId()).isPresent()) {
            return this.categoryRepository.saveAndFlush(category);
        }

        throw new ResourceException("Category not found with ID: " + category.getId(), HttpStatus.BAD_REQUEST, this.httpServletRequest);
    }
}
