package com.andres.course.agy.springboot.springmvc.app.controllers;

import com.andres.course.agy.springboot.springmvc.app.models.Role;
import com.andres.course.agy.springboot.springmvc.app.models.User;
import com.andres.course.agy.springboot.springmvc.app.services.UserService;
import com.andres.course.agy.springboot.springmvc.app.util.paginator.PageRender;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String list(@RequestParam(name = "page", defaultValue = "0") int page,
                       Model model) {
        Pageable pageable = PageRequest.of(page, 8, Sort.by("id").ascending());
        Page<User> usersPage = userService.findAll(pageable);
        PageRender<User> pageRender = new PageRender<>("/admin/users", usersPage);

        model.addAttribute("title", "Administración de Usuarios y Roles");
        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("activeAdminsCount", userService.countActiveAdmins());
        model.addAttribute("page", pageRender);

        return "users/list";
    }

    @GetMapping("/form")
    public String createForm(Model model) {
        User user = new User();
        user.setEnabled(true);

        model.addAttribute("title", "Crear Nuevo Usuario");
        model.addAttribute("user", user);
        model.addAttribute("allRoles", userService.findAllRoles());
        model.addAttribute("activeAdminsCount", userService.countActiveAdmins());
        model.addAttribute("isNew", true);

        return "users/form";
    }

    @GetMapping("/form/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes flash) {
        Optional<User> optionalUser = userService.findById(id);
        if (optionalUser.isEmpty()) {
            flash.addFlashAttribute("error", "El usuario especificado no existe en el sistema.");
            return "redirect:/admin/users";
        }

        User user = optionalUser.get();
        // Clear password in form model for security reasons
        user.setPassword("");

        model.addAttribute("title", "Editar Usuario: " + user.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("allRoles", userService.findAllRoles());
        model.addAttribute("activeAdminsCount", userService.countActiveAdmins());
        model.addAttribute("isNew", false);

        return "users/form";
    }

    @PostMapping("/form")
    public String save(@Valid @ModelAttribute("user") User user,
                       BindingResult result,
                       @RequestParam(name = "roleIds", required = false) List<Long> roleIds,
                       Model model,
                       RedirectAttributes flash) {

        boolean isNew = (user.getId() == null);

        // Validation for password on new user creation
        if (isNew && (user.getPassword() == null || user.getPassword().isBlank())) {
            result.rejectValue("password", "field.required", "La contraseña es obligatoria para nuevos usuarios.");
        }

        // Uniqueness checks
        if (isNew) {
            if (userService.existsByUsername(user.getUsername())) {
                result.rejectValue("username", "field.exists", "El nombre de usuario ya está registrado.");
            }
            if (userService.existsByEmail(user.getEmail())) {
                result.rejectValue("email", "field.exists", "El correo electrónico ya está registrado.");
            }
        } else {
            Optional<User> dbUserOpt = userService.findById(user.getId());
            if (dbUserOpt.isPresent()) {
                User dbUser = dbUserOpt.get();
                if (!dbUser.getUsername().equals(user.getUsername()) && userService.existsByUsername(user.getUsername())) {
                    result.rejectValue("username", "field.exists", "El nombre de usuario ya está registrado por otro usuario.");
                }
                if (!dbUser.getEmail().equals(user.getEmail()) && userService.existsByEmail(user.getEmail())) {
                    result.rejectValue("email", "field.exists", "El correo electrónico ya está registrado por otro usuario.");
                }
            }
        }

        // Assign selected roles and pass to model so checkmarks persist on validation errors
        List<Role> selectedRoles = new ArrayList<>();
        List<Role> allRoles = userService.findAllRoles();
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Role r : allRoles) {
                if (roleIds.contains(r.getId())) {
                    selectedRoles.add(r);
                }
            }
        }
        user.setRoles(selectedRoles);

        if (roleIds == null || roleIds.isEmpty()) {
            model.addAttribute("roleError", "Debes seleccionar al menos un rol para el usuario.");
        }

        if (result.hasErrors() || roleIds == null || roleIds.isEmpty()) {
            model.addAttribute("title", isNew ? "Crear Nuevo Usuario" : "Editar Usuario");
            model.addAttribute("allRoles", allRoles);
            model.addAttribute("activeAdminsCount", userService.countActiveAdmins());
            model.addAttribute("selectedRoleIds", roleIds != null ? roleIds : new ArrayList<>());
            model.addAttribute("isNew", isNew);
            return "users/form";
        }

        // Protection: Ensure at least one active admin exists in the system
        if (!isNew) {
            Optional<User> dbUserOpt = userService.findById(user.getId());
            if (dbUserOpt.isPresent()) {
                boolean dbHasAdmin = dbUserOpt.get().getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));
                boolean selectedHasAdmin = selectedRoles.stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));

                if (dbHasAdmin && !selectedHasAdmin) {
                    long activeAdmins = userService.countActiveAdmins();
                    if (activeAdmins <= 1) {
                        model.addAttribute("roleError", "No se puede remover el rol ROLE_ADMIN: Debe existir al menos un administrador activo en el sistema.");
                        model.addAttribute("title", "Editar Usuario");
                        model.addAttribute("allRoles", allRoles);
                        model.addAttribute("activeAdminsCount", activeAdmins);
                        model.addAttribute("selectedRoleIds", roleIds);
                        model.addAttribute("isNew", false);
                        return "users/form";
                    }
                }
            }
        }

        userService.save(user);
        flash.addFlashAttribute("success", isNew ? "Usuario creado con éxito." : "Usuario actualizado con éxito.");

        return "redirect:/admin/users";
    }

    @GetMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id, Authentication authentication, RedirectAttributes flash) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getUsername().equals(authentication.getName())) {
                flash.addFlashAttribute("error", "No puedes desactivar tu propia cuenta activa.");
                return "redirect:/admin/users";
            }

            boolean hasAdminRole = user.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));
            if (hasAdminRole && Boolean.TRUE.equals(user.getEnabled())) {
                long activeAdmins = userService.countActiveAdmins();
                if (activeAdmins <= 1) {
                    flash.addFlashAttribute("error", "No se puede desactivar al único administrador activo del sistema.");
                    return "redirect:/admin/users";
                }
            }

            userService.toggleUserStatus(id);
            flash.addFlashAttribute("info", "Estado del usuario '" + user.getUsername() + "' actualizado.");
        } else {
            flash.addFlashAttribute("error", "Usuario no encontrado.");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Authentication authentication, RedirectAttributes flash) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getUsername().equals(authentication.getName())) {
                flash.addFlashAttribute("error", "No puedes eliminar tu propia cuenta.");
                return "redirect:/admin/users";
            }

            boolean hasAdminRole = user.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));
            if (hasAdminRole) {
                long activeAdmins = userService.countActiveAdmins();
                if (activeAdmins <= 1) {
                    flash.addFlashAttribute("error", "No se puede eliminar al único administrador del sistema.");
                    return "redirect:/admin/users";
                }
            }

            userService.deleteById(id);
            flash.addFlashAttribute("success", "Usuario '" + user.getUsername() + "' eliminado con éxito.");
        } else {
            flash.addFlashAttribute("error", "El usuario no existe.");
        }
        return "redirect:/admin/users";
    }
}
