package com.andres.course.agy.springboot.springmvc.app.services;

import com.andres.course.agy.springboot.springmvc.app.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> findAll();
    Page<Product> findAll(Pageable pageable);
    Page<Product> findBySearchCriteria(String query, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Optional<Product> findById(Long id);
    Product save(Product product);
    boolean deleteById(Long id);
}

