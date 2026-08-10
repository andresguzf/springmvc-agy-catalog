package com.andres.course.agy.springboot.springmvc.app.config;

import com.andres.course.agy.springboot.springmvc.app.models.Product;
import com.andres.course.agy.springboot.springmvc.app.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            Product p1 = new Product("Laptop Gaming ASUS ROG", "Laptop de alto rendimiento con RTX 4070, 32GB RAM y Ryzen 9", 1499.99, 12);
            Product p2 = new Product("Teclado Mecánico RGB", "Teclado mecánico con switches Cherry MX Red e iluminación RGB", 89.50, 45);
            Product p3 = new Product("Monitor UltraWide 34\"", "Monitor curvo 144Hz 1ms con soporte HDR10 y FreeSync", 450.00, 8);
            Product p4 = new Product("Ratón Inalámbrico Pro", "Ratón ergonómico con sensor óptico de 26,000 DPI y carga rápida", 65.00, 30);

            productRepository.saveAll(List.of(p1, p2, p3, p4));
        }
    }
}
