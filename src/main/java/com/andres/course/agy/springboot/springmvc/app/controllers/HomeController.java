package com.andres.course.agy.springboot.springmvc.app.controllers;

import com.andres.course.agy.springboot.springmvc.app.models.Product;
import com.andres.course.agy.springboot.springmvc.app.services.ProductService;
import com.andres.course.agy.springboot.springmvc.app.services.CartService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final ProductService productService;
    private final CartService cartService;

    public HomeController(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    @GetMapping({"", "/", "/index", "/home"})
    public String index(
            @RequestParam(name = "query", required = false) String query,
            Model model) {
        Pageable topTenPageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("id"), Sort.Order.desc("createdAt")));
        Page<Product> latestProducts = productService.findBySearchCriteria(query, null, null, topTenPageable);

        model.addAttribute("title", "Inicio | Catálogo E-Commerce Spring Web MVC");
        model.addAttribute("welcomeMessage", "¡Bienvenido a la Tienda Tecnológica!");
        model.addAttribute("products", latestProducts.getContent());
        model.addAttribute("query", query != null ? query.trim() : "");
        model.addAttribute("cart", cartService.getCart());
        return "index";
    }
}
