package com.andres.course.agy.springboot.springmvc.app.util.paginator;

public class PageItem {
    private final int numero;
    private final boolean actual;

    public PageItem(int numero, boolean actual) {
        this.numero = numero;
        this.actual = actual;
    }

    public int getNumero() {
        return numero;
    }

    public boolean isActual() {
        return actual;
    }
}
