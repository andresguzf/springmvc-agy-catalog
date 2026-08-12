package com.andres.course.agy.springboot.springmvc.app.repositories;

import com.andres.course.agy.springboot.springmvc.app.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
}

