package com.andres.course.agy.springboot.springmvc.app.services;

import com.andres.course.agy.springboot.springmvc.app.models.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    void deleteById(Long id);
}
