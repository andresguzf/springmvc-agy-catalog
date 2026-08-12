package com.andres.course.agy.springboot.springmvc.app.controllers;

import com.andres.course.agy.springboot.springmvc.app.models.Product;
import com.andres.course.agy.springboot.springmvc.app.services.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping({"", "/", "/index", "/home"})
    public String index(Model model) {
        Pageable topTenPageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("id"), Sort.Order.asc("createdAt")));
        Page<Product> latestProducts = productService.findAll(topTenPageable);

        model.addAttribute("title", "Inicio | Catálogo E-Commerce Spring Web MVC");
        model.addAttribute("welcomeMessage", "¡Bienvenido a la Tienda Tecnológica!");
        model.addAttribute("products", latestProducts.getContent());
        return "index";
    }
}
