package com.syedriadh.cart.inventoryservice.controller.v1;

import com.syedriadh.cart.inventoryservice.entity.Category;
import com.syedriadh.cart.inventoryservice.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/categories", produces = {MediaType.APPLICATION_JSON_VALUE})
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<?> categories(@RequestParam(value = "page", defaultValue = "1") int page,
                                        @RequestParam(value = "size", defaultValue = "16") int size,
                                        @RequestParam(value = "sort", defaultValue = "id,asc") String... sort) {
        List<Sort.Order> sortOrders = new ArrayList<>();
        sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1]).orElse(Sort.Direction.ASC), sort[0]));
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        Page<Category> categories = this.categoryService.categories(pageable);

        return categories.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<?> categories() {
        List<Category> categories = this.categoryService.categories();

        return categories.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PostMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> add(@RequestBody @Validated Category category) {
        Category savedCategory = this.categoryService.add(category);

        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(@RequestBody @Validated Category category) {
        Category updatedCategory = this.categoryService.update(category);

        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }
}
