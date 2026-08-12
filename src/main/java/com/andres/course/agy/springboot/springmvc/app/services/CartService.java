package com.andres.course.agy.springboot.springmvc.app.services;

import com.andres.course.agy.springboot.springmvc.app.models.Cart;

public interface CartService {
    void addProduct(Long productId);
    void removeProduct(Long productId);
    void updateQuantity(Long productId, int quantity);
    void clearCart();
    Cart getCart();
}
