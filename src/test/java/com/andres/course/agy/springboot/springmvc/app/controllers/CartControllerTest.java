package com.andres.course.agy.springboot.springmvc.app.controllers;

import com.andres.course.agy.springboot.springmvc.app.models.Cart;
import com.andres.course.agy.springboot.springmvc.app.models.Product;
import com.andres.course.agy.springboot.springmvc.app.services.CartService;
import com.andres.course.agy.springboot.springmvc.app.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private CartController cartController;

    private Cart mockCart;
    private Product mockProduct;

    @BeforeEach
    void setUp() {
        mockCart = new Cart();
        mockProduct = new Product("iPhone 17 Pro Max", "Teléfono insignia", 1400.0, 5);
        mockProduct.setId(79L);
    }

    @Test
    @DisplayName("viewCart() renderiza la vista 'cart/view' y agrega cart al modelo")
    void viewCartReturnsCartView() {
        when(cartService.getCart()).thenReturn(mockCart);

        String view = cartController.viewCart(model);

        assertEquals("cart/view", view);
        verify(model).addAttribute("title", "Carro de Compras | Spring Web MVC");
        verify(model).addAttribute("cart", mockCart);
    }

    @Test
    @DisplayName("addToCart() agrega producto y redirige a la vista correspondiente según Referer")
    void addToCartAddsProductAndRedirects() {
        when(productService.findById(79L)).thenReturn(Optional.of(mockProduct));
        when(request.getHeader("Referer")).thenReturn("http://localhost:8080/cart");

        String view = cartController.addToCart(79L, request, redirectAttributes);

        assertEquals("redirect:/cart", view);
        verify(cartService).addProduct(79L);
        verify(redirectAttributes).addFlashAttribute(eq("success"), contains("iPhone 17 Pro Max"));
    }

    @Test
    @DisplayName("removeFromCart() remueve el producto del carro")
    void removeFromCartRemovesProduct() {
        when(request.getHeader("Referer")).thenReturn("http://localhost:8080/cart");

        String view = cartController.removeFromCart(79L, request, redirectAttributes);

        assertEquals("redirect:/cart", view);
        verify(cartService).removeProduct(79L);
        verify(redirectAttributes).addFlashAttribute(eq("info"), contains("eliminado"));
    }

    @Test
    @DisplayName("updateQuantity() actualiza la cantidad del producto")
    void updateQuantityUpdatesProductQuantity() {
        when(request.getHeader("Referer")).thenReturn("http://localhost:8080/cart");

        String view = cartController.updateQuantity(79L, 3, request, redirectAttributes);

        assertEquals("redirect:/cart", view);
        verify(cartService).updateQuantity(79L, 3);
    }

    @Test
    @DisplayName("clearCart() vacía el carrito por completo")
    void clearCartEmptiesCart() {
        when(request.getHeader("Referer")).thenReturn("http://localhost:8080/cart");

        String view = cartController.clearCart(request, redirectAttributes);

        assertEquals("redirect:/cart", view);
        verify(cartService).clearCart();
        verify(redirectAttributes).addFlashAttribute(eq("info"), contains("vaciado"));
    }
}
