package com.andres.course.agy.springboot.springmvc.app.controllers;

import com.andres.course.agy.springboot.springmvc.app.models.Product;
import com.andres.course.agy.springboot.springmvc.app.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping({"", "/", "/list"})
    public String list(Model model) {
        model.addAttribute("title", "Catálogo de Productos | Spring Web MVC");
        model.addAttribute("products", service.findAll());
        return "products/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        Optional<Product> optionalProduct = service.findById(id);
        if (optionalProduct.isPresent()) {
            service.deleteById(id);
            redirect.addFlashAttribute("success", "El producto '" + optionalProduct.get().getName() + "' ha sido eliminado con éxito.");
        } else {
            redirect.addFlashAttribute("error", "El producto especificado no existe.");
        }
        return "redirect:/products";
    }
}
