package com.andres.course.agy.springboot.springmvc.app.services;

import com.andres.course.agy.springboot.springmvc.app.models.Invoice;
import com.andres.course.agy.springboot.springmvc.app.models.Order;
import com.andres.course.agy.springboot.springmvc.app.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderService {

    Order save(Order order);

    Optional<Order> findById(Long id);

    Optional<Order> findOrderWithDetails(Long id);

    Page<Order> findAll(Pageable pageable);

    Page<Order> findByUser(User user, Pageable pageable);

    Invoice convertOrderToInvoice(Long orderId, User issuer);

    void deleteById(Long id);
}
