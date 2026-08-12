package com.andres.course.agy.springboot.springmvc.app.util.paginator;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class PageRender<T> {

    private final String url;
    private final Page<T> page;

    private final int totalPaginas;
    private final int numElementosPorPagina;
    private final int paginaActual;

    private final List<PageItem> paginas;

    public PageRender(String url, Page<T> page) {
        this.url = url;
        this.page = page;
        this.paginas = new ArrayList<>();

        this.numElementosPorPagina = page.getSize();
        this.totalPaginas = page.getTotalPages();
        this.paginaActual = page.getNumber() + 1; // 1-indexed for UI display

        int range = 3; // Limite de 3 paginas a la izquierda y 3 a la derecha
        int desde, hasta;

        if (totalPaginas <= range * 2 + 1) {
            desde = 1;
            hasta = totalPaginas;
        } else {
            if (paginaActual <= range + 1) {
                desde = 1;
                hasta = range * 2 + 1;
            } else if (paginaActual >= totalPaginas - range) {
                desde = totalPaginas - range * 2;
                hasta = totalPaginas;
            } else {
                desde = paginaActual - range;
                hasta = paginaActual + range;
            }
        }

        for (int i = 0; i < hasta - desde + 1; i++) {
            paginas.add(new PageItem(desde + i, paginaActual == (desde + i)));
        }
    }

    public String getUrl() {
        return url;
    }

    public Page<T> getPage() {
        return page;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }

    public int getNumElementosPorPagina() {
        return numElementosPorPagina;
    }

    public int getPaginaActual() {
        return paginaActual;
    }

    public List<PageItem> getPaginas() {
        return paginas;
    }

    public boolean isFirst() {
        return page.isFirst();
    }

    public boolean isLast() {
        return page.isLast();
    }

    public boolean isHasNext() {
        return page.hasNext();
    }

    public boolean isHasPrevious() {
        return page.hasPrevious();
    }
}
