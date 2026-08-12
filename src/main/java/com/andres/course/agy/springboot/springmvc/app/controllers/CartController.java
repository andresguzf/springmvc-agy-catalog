package com.andres.course.agy.springboot.springmvc.app.controllers;

import com.andres.course.agy.springboot.springmvc.app.services.CartService;
import com.andres.course.agy.springboot.springmvc.app.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @GetMapping({"", "/", "/view", "/detail"})
    public String viewCart(Model model) {
        model.addAttribute("title", "Carro de Compras | Spring Web MVC");
        model.addAttribute("cart", cartService.getCart());
        return "cart/view";
    }

    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirect) {
        return productService.findById(id).map(product -> {
            cartService.addProduct(id);
            redirect.addFlashAttribute("success", "El producto '" + product.getName() + "' ha sido agregado al carrito.");
            return getRedirectUrl(request);
        }).orElseGet(() -> {
            redirect.addFlashAttribute("error", "El producto especificado no existe.");
            return getRedirectUrl(request);
        });
    }

    @GetMapping("/add/{id}")
    public String addToCartGet(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirect) {
        return addToCart(id, request, redirect);
    }

    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirect) {
        cartService.removeProduct(id);
        redirect.addFlashAttribute("info", "Producto eliminado del carrito.");
        return getRedirectUrl(request);
    }

    @PostMapping("/update/{id}")
    public String updateQuantity(@PathVariable Long id, @RequestParam(name = "quantity", defaultValue = "1") int quantity, HttpServletRequest request, RedirectAttributes redirect) {
        cartService.updateQuantity(id, quantity);
        return getRedirectUrl(request);
    }

    @GetMapping("/clear")
    public String clearCart(HttpServletRequest request, RedirectAttributes redirect) {
        cartService.clearCart();
        redirect.addFlashAttribute("info", "El carrito de compras ha sido vaciado.");
        return getRedirectUrl(request);
    }

    private String getRedirectUrl(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null && (referer.contains("/index") || referer.contains("/home") || referer.endsWith(":8080/"))) {
            return "redirect:/index";
        }
        return "redirect:/cart";
    }
}
