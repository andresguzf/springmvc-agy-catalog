package com.andres.course.agy.springboot.springmvc.app.controllers;

import com.andres.course.agy.springboot.springmvc.app.models.Company;
import com.andres.course.agy.springboot.springmvc.app.services.CompanyService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/company")
public class AdminCompanyController {

    private final CompanyService companyService;

    public AdminCompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public String editForm(Model model) {
        Company company = companyService.getCompany();
        model.addAttribute("title", "Configuración de Datos de la Empresa");
        model.addAttribute("company", company);
        return "company/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("company") Company company,
                       BindingResult result,
                       Model model,
                       RedirectAttributes flash) {

        if (result.hasErrors()) {
            model.addAttribute("title", "Configuración de Datos de la Empresa");
            return "company/form";
        }

        companyService.save(company);
        flash.addFlashAttribute("success", "Datos de la empresa actualizados con éxito.");

        return "redirect:/admin/company";
    }
}
