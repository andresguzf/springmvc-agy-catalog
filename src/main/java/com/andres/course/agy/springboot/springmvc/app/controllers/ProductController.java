package com.andres.course.agy.springboot.springmvc.app.controllers;

import com.andres.course.agy.springboot.springmvc.app.models.Product;
import com.andres.course.agy.springboot.springmvc.app.services.CloudinaryService;
import com.andres.course.agy.springboot.springmvc.app.services.ProductService;
import com.andres.course.agy.springboot.springmvc.app.util.paginator.PageRender;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;
    private final CloudinaryService cloudinaryService;

    public ProductController(ProductService service, CloudinaryService cloudinaryService) {
        this.service = service;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping({ "", "/", "/list" })
    public String list(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        Pageable pageable = PageRequest.of(page, 8);

        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;

        Page<Product> products = service.findBySearchCriteria(query, startDateTime, endDateTime, pageable);

        StringBuilder urlParams = new StringBuilder("/products?");
        if (query != null && !query.trim().isEmpty()) {
            urlParams.append("query=").append(URLEncoder.encode(query.trim(), StandardCharsets.UTF_8)).append("&");
        }
        if (startDate != null) {
            urlParams.append("startDate=").append(startDate).append("&");
        }
        if (endDate != null) {
            urlParams.append("endDate=").append(endDate).append("&");
        }

        String pageUrl = urlParams.toString();
        if (pageUrl.endsWith("&") || pageUrl.endsWith("?")) {
            pageUrl = pageUrl.substring(0, pageUrl.length() - 1);
        }

        PageRender<Product> pageRender = new PageRender<>(pageUrl, products);

        model.addAttribute("title", "Catálogo de Productos | Spring Web MVC");
        model.addAttribute("products", products);
        model.addAttribute("page", pageRender);
        model.addAttribute("query", query != null ? query.trim() : "");
        model.addAttribute("startDate", startDate != null ? startDate.toString() : "");
        model.addAttribute("endDate", endDate != null ? endDate.toString() : "");

        return "products/list";
    }

    @GetMapping("/form")
    public String createForm(Model model) {
        model.addAttribute("title", "Crear Nuevo Producto | Spring Web MVC");
        model.addAttribute("product", new Product());
        return "products/form";
    }

    @GetMapping("/form/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        return service.findById(id).map(product -> {
            model.addAttribute("title", "Editar Producto | Spring Web MVC");
            model.addAttribute("product", product);
            return "products/form";
        }).orElseGet(() -> {
            redirect.addFlashAttribute("error", "El producto especificado no existe.");
            return "redirect:/products";
        });
    }

    @PostMapping("/form")
    public String save(@Valid @ModelAttribute("product") Product product, BindingResult result,
            @RequestParam(name = "file", required = false) MultipartFile file,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            String pageTitle = (product.getId() != null) ? "Editar Producto | Spring Web MVC"
                    : "Crear Nuevo Producto | Spring Web MVC";
            model.addAttribute("title", pageTitle);
            return "products/form";
        }

        if (file != null && !file.isEmpty()) {
            try {
                // Delete previous image if updating product
                if (product.getImagePublicId() != null && !product.getImagePublicId().trim().isEmpty()) {
                    try {
                        cloudinaryService.delete(product.getImagePublicId());
                    } catch (Exception e) {
                        // Log and ignore delete failure for old image
                    }
                }

                Map<String, Object> uploadResult = cloudinaryService.upload(file);
                String imageUrl = (String) uploadResult.get("secure_url");
                String publicId = (String) uploadResult.get("public_id");

                product.setImage(imageUrl);
                product.setImagePublicId(publicId);
            } catch (Exception e) {
                redirect.addFlashAttribute("error", "Error al subir la imagen a Cloudinary: " + e.getMessage());
                return "redirect:/products/form" + (product.getId() != null ? "/" + product.getId() : "");
            }
        }

        String actionMessage = (product.getId() != null) ? "actualizado" : "creado";
        service.save(product);
        redirect.addFlashAttribute("success",
                "El producto '" + product.getName() + "' ha sido " + actionMessage + " con éxito.");
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        service.findById(id).ifPresent(product -> {
            if (product.getImagePublicId() != null && !product.getImagePublicId().trim().isEmpty()) {
                try {
                    cloudinaryService.delete(product.getImagePublicId());
                } catch (Exception e) {
                    // Log and ignore deletion failure from Cloudinary
                }
            }
        });
        if (service.deleteById(id)) {
            redirect.addFlashAttribute("success", "El producto ha sido eliminado con éxito.");
        } else {
            redirect.addFlashAttribute("error", "El producto especificado no existe.");
        }
        return "redirect:/products";
    }
}
