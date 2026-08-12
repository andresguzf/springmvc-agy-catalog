package com.andres.course.agy.springboot.springmvc.app.repositories;

import com.andres.course.agy.springboot.springmvc.app.models.Invoice;
import com.andres.course.agy.springboot.springmvc.app.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Page<Invoice> findByUser(User user, Pageable pageable);

    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.user u LEFT JOIN FETCH i.items item LEFT JOIN FETCH item.product WHERE i.id = :id")
    Optional<Invoice> fetchByIdWithUserWithInvoiceItemWithProduct(@Param("id") Long id);
}
