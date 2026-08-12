package com.andres.course.agy.springboot.springmvc.app.repositories.specs;

import com.andres.course.agy.springboot.springmvc.app.models.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductSpecificationTest {

    @Test
    @DisplayName("Debe construir la especificación JPA sin arrojar excepciones")
    void testFilterByCriteria() {
        Specification<Product> specNull = ProductSpecification.filterByCriteria(null, null, null);
        assertNotNull(specNull);

        Specification<Product> specWithQuery = ProductSpecification.filterByCriteria("gaming", LocalDateTime.now().minusDays(5), LocalDateTime.now());
        assertNotNull(specWithQuery);
    }
}
