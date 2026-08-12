package com.andres.course.agy.springboot.springmvc.app.controllers;

import com.andres.course.agy.springboot.springmvc.app.models.Cart;
import com.andres.course.agy.springboot.springmvc.app.models.Product;
import com.andres.course.agy.springboot.springmvc.app.services.CartService;
import com.andres.course.agy.springboot.springmvc.app.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @Mock
    private Model model;

    @InjectMocks
    private HomeController homeController;

    private List<Product> mockProducts;
    private Cart mockCart;

    @BeforeEach
    void setUp() {
        Product p1 = new Product("Laptop Gaming", "Laptop de alta gama", 1500.0, 5);
        p1.setId(10L);
        Product p2 = new Product("Teclado Mecánico", "Teclado RGB", 100.0, 15);
        p2.setId(9L);
        mockProducts = List.of(p1, p2);
        mockCart = new Cart();
    }

    @Test
    @DisplayName("index() debe ordenar por últimos 10 productos (DESC) y agregar cart al modelo")
    void indexWithoutQueryReturnsLatestTenProducts() {
        Page<Product> productPage = new PageImpl<>(mockProducts);
        when(productService.findBySearchCriteria(isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(productPage);
        when(cartService.getCart()).thenReturn(mockCart);

        String viewName = homeController.index(null, model);

        assertEquals("index", viewName);
        verify(model).addAttribute("title", "Inicio | Catálogo E-Commerce Spring Web MVC");
        verify(model).addAttribute("welcomeMessage", "¡Bienvenido a la Tienda Tecnológica!");
        verify(model).addAttribute("products", mockProducts);
        verify(model).addAttribute("query", "");
        verify(model).addAttribute("cart", mockCart);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(productService).findBySearchCriteria(isNull(), isNull(), isNull(), pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());

        Sort sort = pageable.getSort();
        Sort.Order idOrder = sort.getOrderFor("id");
        assertNotNull(idOrder);
        assertTrue(idOrder.isDescending(), "El orden de ID debe ser descendente para obtener los últimos productos");
    }

    @Test
    @DisplayName("index() con query debe filtrar por nombre/descripción y ordenar descendentemente")
    void indexWithQueryFiltersProducts() {
        String searchQuery = "Laptop";
        Page<Product> productPage = new PageImpl<>(List.of(mockProducts.get(0)));
        when(productService.findBySearchCriteria(eq(searchQuery), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(productPage);
        when(cartService.getCart()).thenReturn(mockCart);

        String viewName = homeController.index(searchQuery, model);

        assertEquals("index", viewName);
        verify(model).addAttribute("query", "Laptop");
        verify(model).addAttribute("products", List.of(mockProducts.get(0)));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(productService).findBySearchCriteria(eq(searchQuery), isNull(), isNull(), pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
        assertTrue(pageable.getSort().getOrderFor("id").isDescending());
    }
}
