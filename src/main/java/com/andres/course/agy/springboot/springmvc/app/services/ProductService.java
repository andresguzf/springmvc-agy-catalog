package com.andres.course.agy.springboot.springmvc.app.services;

import com.andres.course.agy.springboot.springmvc.app.models.Product;
import java.util.List;

public interface ProductService {
    List<Product> findAll();
}
