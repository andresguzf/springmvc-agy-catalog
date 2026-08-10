package com.andres.course.agy.springboot.springmvc.app.controllers;

import com.andres.course.agy.springboot.springmvc.app.models.Product;
import com.andres.course.agy.springboot.springmvc.app.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/form")
    public String createForm(Model model) {
        model.addAttribute("title", "Crear Nuevo Producto | Spring Web MVC");
        model.addAttribute("product", new Product());
        return "products/form";
    }

    @PostMapping("/form")
    public String save(@Valid @ModelAttribute("product") Product product, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Crear Nuevo Producto | Spring Web MVC");
            return "products/form";
        }
        service.save(product);
        redirect.addFlashAttribute("success", "El producto '" + product.getName() + "' ha sido guardado con éxito en PostgreSQL.");
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        if (service.deleteById(id)) {
            redirect.addFlashAttribute("success", "El producto ha sido eliminado con éxito.");
        } else {
            redirect.addFlashAttribute("error", "El producto especificado no existe.");
        }
        return "redirect:/products";
    }
}
