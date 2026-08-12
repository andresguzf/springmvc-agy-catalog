package com.andres.course.agy.springboot.springmvc.app.services;

import com.andres.course.agy.springboot.springmvc.app.models.Cart;
import com.andres.course.agy.springboot.springmvc.app.models.CartItem;
import com.andres.course.agy.springboot.springmvc.app.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private ProductService productService;

    private Cart cart;
    private CartServiceImpl cartService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cartService = new CartServiceImpl(cart, productService);

        product1 = new Product("Laptop Gaming", "Laptop de alta gama", 1500.0, 10);
        product1.setId(101L);

        product2 = new Product("Mouse RGB", "Mouse ergonómico", 50.0, 20);
        product2.setId(102L);
    }

    @Test
    @DisplayName("addProduct() agrega un nuevo producto al carrito")
    void addProductAddsNewItem() {
        when(productService.findById(101L)).thenReturn(Optional.of(product1));

        cartService.addProduct(101L);

        assertEquals(1, cart.getItems().size());
        assertEquals(1, cart.getTotalQuantity());
        assertEquals(1500.0, cart.getTotal());
        assertEquals("Laptop Gaming", cart.getItems().get(0).getProduct().getName());
    }

    @Test
    @DisplayName("addProduct() incrementa cantidad si el producto ya existe en el carrito")
    void addProductIncrementsQuantityForExistingItem() {
        when(productService.findById(101L)).thenReturn(Optional.of(product1));

        cartService.addProduct(101L);
        cartService.addProduct(101L);

        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getTotalQuantity());
        assertEquals(3000.0, cart.getTotal());
    }

    @Test
    @DisplayName("removeProduct() elimina el item especificado")
    void removeProductDeletesItem() {
        when(productService.findById(101L)).thenReturn(Optional.of(product1));
        when(productService.findById(102L)).thenReturn(Optional.of(product2));

        cartService.addProduct(101L);
        cartService.addProduct(102L);
        assertEquals(2, cart.getItems().size());

        cartService.removeProduct(101L);

        assertEquals(1, cart.getItems().size());
        assertEquals(102L, cart.getItems().get(0).getProduct().getId());
        assertEquals(50.0, cart.getTotal());
    }

    @Test
    @DisplayName("updateQuantity() actualiza la cantidad o elimina el producto si es 0")
    void updateQuantityModifiesItemQuantity() {
        when(productService.findById(101L)).thenReturn(Optional.of(product1));
        cartService.addProduct(101L);

        cartService.updateQuantity(101L, 3);
        assertEquals(3, cart.getTotalQuantity());
        assertEquals(4500.0, cart.getTotal());

        cartService.updateQuantity(101L, 0);
        assertTrue(cart.isEmpty());
    }

    @Test
    @DisplayName("clearCart() remueve todos los elementos del carrito")
    void clearCartEmptiesCart() {
        when(productService.findById(101L)).thenReturn(Optional.of(product1));
        cartService.addProduct(101L);
        assertFalse(cart.isEmpty());

        cartService.clearCart();
        assertTrue(cart.isEmpty());
        assertEquals(0.0, cart.getTotal());
    }
}
