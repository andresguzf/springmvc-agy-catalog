package com.andres.course.agy.springboot.springmvc.app.repositories;

import com.andres.course.agy.springboot.springmvc.app.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
