package com.andres.course.agy.springboot.springmvc.app.services;

import com.andres.course.agy.springboot.springmvc.app.models.Product;
import com.andres.course.agy.springboot.springmvc.app.repositories.ProductRepository;
import com.andres.course.agy.springboot.springmvc.app.repositories.specs.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return repository.findAll(Sort.by(Sort.Order.asc("id"), Sort.Order.asc("createdAt")));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findBySearchCriteria(String query, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.filterByCriteria(query, startDate, endDate);
        return repository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Product save(Product product) {
        return repository.save(product);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
