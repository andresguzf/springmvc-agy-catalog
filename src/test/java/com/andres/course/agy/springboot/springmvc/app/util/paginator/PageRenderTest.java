package com.andres.course.agy.springboot.springmvc.app.util.paginator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageRenderTest {

    @Test
    @DisplayName("Debe calcular el rango de páginas correctamente cuando hay menos o igual a 7 páginas")
    void testSmallTotalPages() {
        // 4 páginas totales (32 items / 8 per page)
        List<String> items = Collections.nCopies(8, "Product");
        Page<String> page = new PageImpl<>(items, PageRequest.of(0, 8), 32);

        PageRender<String> pageRender = new PageRender<>("/admin/products", page);

        assertEquals(4, pageRender.getTotalPaginas());
        assertEquals(1, pageRender.getPaginaActual());
        assertTrue(pageRender.isFirst());
        assertFalse(pageRender.isLast());
        assertTrue(pageRender.isHasNext());
        assertFalse(pageRender.isHasPrevious());

        List<PageItem> paginas = pageRender.getPaginas();
        assertEquals(4, paginas.size());
        assertEquals(1, paginas.get(0).getNumero());
        assertTrue(paginas.get(0).isActual());
        assertEquals(4, paginas.get(3).getNumero());
        assertFalse(paginas.get(3).isActual());
    }

    @Test
    @DisplayName("Debe calcular ventana deslizante de 3 a la izquierda y 3 a la derecha cuando totalPaginas > 7 (ej: 10 páginas, en página 5)")
    void testSlidingWindowMiddlePage() {
        // 10 páginas totales (80 items / 8 per page), página actual = 4 (index 4 -> página 5 en la UI)
        List<String> items = Collections.nCopies(8, "Product");
        Page<String> page = new PageImpl<>(items, PageRequest.of(4, 8), 80);

        PageRender<String> pageRender = new PageRender<>("/admin/products", page);

        assertEquals(10, pageRender.getTotalPaginas());
        assertEquals(5, pageRender.getPaginaActual());
        assertFalse(pageRender.isFirst());
        assertFalse(pageRender.isLast());
        assertTrue(pageRender.isHasNext());
        assertTrue(pageRender.isHasPrevious());

        List<PageItem> paginas = pageRender.getPaginas();
        assertEquals(7, paginas.size()); // Rango de 7 páginas: 2, 3, 4, [5], 6, 7, 8
        assertEquals(2, paginas.get(0).getNumero()); // 3 a la izquierda: 2, 3, 4
        assertEquals(8, paginas.get(6).getNumero()); // 3 a la derecha: 6, 7, 8
        assertTrue(paginas.stream().filter(p -> p.getNumero() == 5).findFirst().get().isActual());
    }

    @Test
    @DisplayName("Debe ajustar rango al inicio cuando totalPaginas > 7 y paginaActual <= 4")
    void testSlidingWindowStartPage() {
        List<String> items = Collections.nCopies(8, "Product");
        Page<String> page = new PageImpl<>(items, PageRequest.of(1, 8), 80); // Página 2

        PageRender<String> pageRender = new PageRender<>("/admin/products", page);

        List<PageItem> paginas = pageRender.getPaginas();
        assertEquals(7, paginas.size());
        assertEquals(1, paginas.get(0).getNumero());
        assertEquals(7, paginas.get(6).getNumero());
        assertTrue(paginas.get(1).isActual()); // Página 2 activa
    }

    @Test
    @DisplayName("Debe ajustar rango al final cuando totalPaginas > 7 y paginaActual >= totalPaginas - 3")
    void testSlidingWindowEndPage() {
        List<String> items = Collections.nCopies(8, "Product");
        Page<String> page = new PageImpl<>(items, PageRequest.of(9, 8), 80); // Página 10 de 10

        PageRender<String> pageRender = new PageRender<>("/admin/products", page);

        assertTrue(pageRender.isLast());
        assertFalse(pageRender.isHasNext());
        assertTrue(pageRender.isHasPrevious());

        List<PageItem> paginas = pageRender.getPaginas();
        assertEquals(7, paginas.size());
        assertEquals(4, paginas.get(0).getNumero());
        assertEquals(10, paginas.get(6).getNumero());
        assertTrue(paginas.get(6).isActual()); // Página 10 activa
    }

    @Test
    @DisplayName("Debe conservar la URL formateada con parámetros de búsqueda de query y rango de fechas")
    void testUrlWithSearchQueryParameters() {
        List<String> items = Collections.nCopies(8, "Product");
        Page<String> page = new PageImpl<>(items, PageRequest.of(0, 8), 16);

        String searchUrl = "/admin/products?query=gaming&startDate=2026-08-01&endDate=2026-08-11";
        PageRender<String> pageRender = new PageRender<>(searchUrl, page);

        assertEquals(searchUrl, pageRender.getUrl());
        assertEquals(2, pageRender.getTotalPaginas());
    }
}
