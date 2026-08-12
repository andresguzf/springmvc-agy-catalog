package com.andres.course.agy.springboot.springmvc.app.models;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@SessionScope
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<CartItem> items = new ArrayList<>();

    public List<CartItem> getItems() {
        return items;
    }

    public void addItem(Product product) {
        if (product == null || product.getId() == null) {
            return;
        }
        Optional<CartItem> existingItem = items.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + 1);
        } else {
            items.add(new CartItem(product, 1));
        }
    }

    public void removeItem(Long productId) {
        if (productId == null) {
            return;
        }
        items.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public void updateQuantity(Long productId, int quantity) {
        if (productId == null) {
            return;
        }
        if (quantity <= 0) {
            removeItem(productId);
            return;
        }
        items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));
    }

    public double getTotal() {
        return items.stream()
                .mapToDouble(CartItem::getAmount)
                .sum();
    }

    public int getTotalQuantity() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
