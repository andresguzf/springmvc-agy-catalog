package com.andres.course.agy.springboot.springmvc.app.services;

import com.andres.course.agy.springboot.springmvc.app.models.Cart;
import com.andres.course.agy.springboot.springmvc.app.models.Product;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final Cart cart;
    private final ProductService productService;

    public CartServiceImpl(Cart cart, ProductService productService) {
        this.cart = cart;
        this.productService = productService;
    }

    @Override
    public void addProduct(Long productId) {
        if (productId != null) {
            Optional<Product> productOpt = productService.findById(productId);
            productOpt.ifPresent(cart::addItem);
        }
    }

    @Override
    public void removeProduct(Long productId) {
        if (productId != null) {
            cart.removeItem(productId);
        }
    }

    @Override
    public void updateQuantity(Long productId, int quantity) {
        if (productId != null) {
            cart.updateQuantity(productId, quantity);
        }
    }

    @Override
    public void clearCart() {
        cart.clear();
    }

    @Override
    public Cart getCart() {
        return cart;
    }
}
