package com.andres.course.agy.springboot.springmvc.app.services;

import com.andres.course.agy.springboot.springmvc.app.models.Invoice;
import com.andres.course.agy.springboot.springmvc.app.models.Product;
import com.andres.course.agy.springboot.springmvc.app.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface InvoiceService {

    Page<Invoice> findAll(Pageable pageable);

    Page<Invoice> findByUser(User user, Pageable pageable);

    Optional<Invoice> findById(Long id);

    Optional<Invoice> findInvoiceWithDetails(Long id);

    Invoice save(Invoice invoice);

    void deleteById(Long id);

    List<Product> findProductByName(String term);
}
