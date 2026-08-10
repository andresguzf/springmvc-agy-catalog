package com.andres.course.agy.springboot.springmvc.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"", "/", "/index", "/home"})
    public String index(Model model) {
        model.addAttribute("title", "12-springmvc-app | Spring Web MVC");
        model.addAttribute("welcomeMessage", "¡Bienvenido al nuevo proyecto Spring MVC!");
        return "index";
    }
}
