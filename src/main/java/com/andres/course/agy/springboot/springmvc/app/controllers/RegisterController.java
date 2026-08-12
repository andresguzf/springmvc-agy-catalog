package com.andres.course.agy.springboot.springmvc.app.controllers;

import com.andres.course.agy.springboot.springmvc.app.models.User;
import com.andres.course.agy.springboot.springmvc.app.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showRegisterForm(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/index";
        }

        model.addAttribute("title", "Crear una Cuenta | E-Commerce");
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               @RequestParam(name = "confirmPassword") String confirmPassword,
                               Model model,
                               RedirectAttributes flash) {

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            result.rejectValue("password", "field.required", "La contraseña es obligatoria.");
        } else if (!user.getPassword().equals(confirmPassword)) {
            result.rejectValue("password", "field.mismatch", "Las contraseñas ingresadas no coinciden.");
        }

        if (userService.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "field.exists", "El nombre de usuario ya se encuentra registrado.");
        }

        if (userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "field.exists", "El correo electrónico ya se encuentra registrado.");
        }

        if (result.hasErrors()) {
            model.addAttribute("title", "Crear una Cuenta | E-Commerce");
            return "register";
        }

        userService.registerUser(user);
        flash.addFlashAttribute("success", "¡Cuenta creada con éxito! Ya puedes iniciar sesión en la plataforma.");

        return "redirect:/login";
    }
}
