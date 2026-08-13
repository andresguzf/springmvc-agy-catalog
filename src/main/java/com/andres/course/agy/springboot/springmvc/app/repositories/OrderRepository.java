package com.andres.course.agy.springboot.springmvc.app.repositories;

import com.andres.course.agy.springboot.springmvc.app.models.Order;
import com.andres.course.agy.springboot.springmvc.app.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUser(User user, Pageable pageable);

    @Query("select o from Order o left join fetch o.items i left join fetch i.product left join fetch o.invoice where o.id = :id")
    Optional<Order> findOrderWithDetails(@Param("id") Long id);
}
