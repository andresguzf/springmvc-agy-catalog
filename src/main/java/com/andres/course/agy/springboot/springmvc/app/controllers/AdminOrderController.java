package com.andres.course.agy.springboot.springmvc.app.controllers;

import com.andres.course.agy.springboot.springmvc.app.models.Company;
import com.andres.course.agy.springboot.springmvc.app.models.Invoice;
import com.andres.course.agy.springboot.springmvc.app.models.Order;
import com.andres.course.agy.springboot.springmvc.app.models.User;
import com.andres.course.agy.springboot.springmvc.app.services.CompanyService;
import com.andres.course.agy.springboot.springmvc.app.services.OrderService;
import com.andres.course.agy.springboot.springmvc.app.services.UserService;
import com.andres.course.agy.springboot.springmvc.app.util.paginator.PageRender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/orders")
@PreAuthorize("hasAnyRole('ADMIN', 'BILLING')")
public class AdminOrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final CompanyService companyService;

    public AdminOrderController(OrderService orderService, UserService userService, CompanyService companyService) {
        this.orderService = orderService;
        this.userService = userService;
        this.companyService = companyService;
    }

    @GetMapping
    public String list(@RequestParam(name = "page", defaultValue = "0") int page,
                       Model model) {

        Pageable pageable = PageRequest.of(page, 8, Sort.by("id").descending());
        Page<Order> ordersPage = orderService.findAll(pageable);
        PageRender<Order> pageRender = new PageRender<>("/admin/orders", ordersPage);

        model.addAttribute("title", "Gestión de Órdenes de Compra");
        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("page", pageRender);

        return "admin/orders/list";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id,
                       Model model,
                       RedirectAttributes flash) {

        Optional<Order> orderOpt = orderService.findOrderWithDetails(id);
        if (orderOpt.isEmpty()) {
            flash.addFlashAttribute("error", "La orden de compra N° " + id + " no existe.");
            return "redirect:/admin/orders";
        }

        Order order = orderOpt.get();
        Company company = companyService.getCompany();

        model.addAttribute("title", "Revisión de Orden de Compra N° " + order.getId());
        model.addAttribute("order", order);
        model.addAttribute("company", company);

        return "admin/orders/view";
    }

    @GetMapping("/emit/{id}")
    public String convertToInvoice(@PathVariable Long id,
                                  Authentication authentication,
                                  RedirectAttributes flash) {

        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no autenticado."));

        try {
            Invoice invoice = orderService.convertOrderToInvoice(id, currentUser);
            flash.addFlashAttribute("success", "¡Orden de Compra N° " + id + " convertida exitosamente a Factura N° " + invoice.getId() + "!");
            return "redirect:/admin/invoices/view/" + invoice.getId();
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/orders";
        }
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        orderService.deleteById(id);
        flash.addFlashAttribute("info", "Orden de compra N° " + id + " eliminada y stock devuelto al catálogo.");
        return "redirect:/admin/orders";
    }
}
