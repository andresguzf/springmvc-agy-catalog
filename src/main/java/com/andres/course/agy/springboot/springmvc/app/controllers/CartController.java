package com.andres.course.agy.springboot.springmvc.app.controllers;

import com.andres.course.agy.springboot.springmvc.app.models.Cart;
import com.andres.course.agy.springboot.springmvc.app.models.CartItem;
import com.andres.course.agy.springboot.springmvc.app.models.CheckoutForm;
import com.andres.course.agy.springboot.springmvc.app.models.Invoice;
import com.andres.course.agy.springboot.springmvc.app.models.InvoiceItem;
import com.andres.course.agy.springboot.springmvc.app.models.User;
import com.andres.course.agy.springboot.springmvc.app.services.CartService;
import com.andres.course.agy.springboot.springmvc.app.services.CompanyService;
import com.andres.course.agy.springboot.springmvc.app.services.InvoiceService;
import com.andres.course.agy.springboot.springmvc.app.services.ProductService;
import com.andres.course.agy.springboot.springmvc.app.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;
    private final UserService userService;
    private final InvoiceService invoiceService;
    private final CompanyService companyService;

    public CartController(CartService cartService,
                          ProductService productService,
                          UserService userService,
                          InvoiceService invoiceService,
                          CompanyService companyService) {
        this.cartService = cartService;
        this.productService = productService;
        this.userService = userService;
        this.invoiceService = invoiceService;
        this.companyService = companyService;
    }

    @GetMapping({"", "/", "/view", "/detail"})
    public String viewCart(Model model) {
        model.addAttribute("title", "Carro de Compras | Spring Web MVC");
        model.addAttribute("cart", cartService.getCart());
        return "cart/view";
    }

    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirect) {
        return productService.findById(id).map(product -> {
            cartService.addProduct(id);
            redirect.addFlashAttribute("success", "El producto '" + product.getName() + "' ha sido agregado al carrito.");
            return getRedirectUrl(request);
        }).orElseGet(() -> {
            redirect.addFlashAttribute("error", "El producto especificado no existe.");
            return getRedirectUrl(request);
        });
    }

    @GetMapping("/add/{id}")
    public String addToCartGet(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirect) {
        return addToCart(id, request, redirect);
    }

    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirect) {
        cartService.removeProduct(id);
        redirect.addFlashAttribute("info", "Producto eliminado del carrito.");
        return getRedirectUrl(request);
    }

    @PostMapping("/update/{id}")
    public String updateQuantity(@PathVariable Long id, @RequestParam(name = "quantity", defaultValue = "1") int quantity, HttpServletRequest request, RedirectAttributes redirect) {
        cartService.updateQuantity(id, quantity);
        return getRedirectUrl(request);
    }

    @GetMapping("/clear")
    public String clearCart(HttpServletRequest request, RedirectAttributes redirect) {
        cartService.clearCart();
        redirect.addFlashAttribute("info", "El carrito de compras ha sido vaciado.");
        return getRedirectUrl(request);
    }

    @GetMapping("/checkout")
    public String checkoutForm(Authentication authentication, Model model, RedirectAttributes flash) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            flash.addFlashAttribute("error", "Debes iniciar sesión para procesar y finalizar tu compra.");
            return "redirect:/login";
        }

        Cart cart = cartService.getCart();
        if (cart.isEmpty()) {
            flash.addFlashAttribute("info", "Tu carrito de compras está vacío.");
            return "redirect:/cart";
        }

        User currentUser = userService.findByUsername(authentication.getName()).orElse(null);
        CheckoutForm checkoutForm = new CheckoutForm();
        if (currentUser != null) {
            String[] parts = currentUser.getName() != null ? currentUser.getName().split(" ", 2) : new String[]{currentUser.getUsername(), ""};
            checkoutForm.setFirstName(parts[0]);
            checkoutForm.setLastName(parts.length > 1 ? parts[1] : "");
            checkoutForm.setEmail(currentUser.getEmail() != null ? currentUser.getEmail() : "");
        }

        model.addAttribute("title", "Finalizar Compra - Checkout");
        model.addAttribute("cart", cart);
        model.addAttribute("checkoutForm", checkoutForm);
        model.addAttribute("company", companyService.getCompany());

        return "cart/checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(@Valid @ModelAttribute("checkoutForm") CheckoutForm checkoutForm,
                                  BindingResult result,
                                  Authentication authentication,
                                  Model model,
                                  RedirectAttributes flash) {

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            flash.addFlashAttribute("error", "Sesión expirada. Por favor inicia sesión nuevamente.");
            return "redirect:/login";
        }

        Cart cart = cartService.getCart();
        if (cart.isEmpty()) {
            flash.addFlashAttribute("error", "El carrito de compras está vacío.");
            return "redirect:/cart";
        }

        if (result.hasErrors()) {
            model.addAttribute("title", "Finalizar Compra - Checkout");
            model.addAttribute("cart", cart);
            model.addAttribute("company", companyService.getCompany());
            return "cart/checkout";
        }

        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        Invoice invoice = new Invoice();
        invoice.setCustomerName(checkoutForm.getFirstName() + " " + checkoutForm.getLastName());
        invoice.setTaxId(checkoutForm.getRut());
        
        String shippingLabel = getShippingLabel(checkoutForm.getShippingMethod());
        String paymentLabel = getPaymentLabel(checkoutForm.getPaymentMethod());
        
        invoice.setDescription("Orden de Compra Web | " + shippingLabel + " | " + paymentLabel);
        invoice.setObservation("Envío: " + checkoutForm.getAddress() + ", " + checkoutForm.getCity() + 
                               " | Tel: " + checkoutForm.getPhone() + " | Email: " + checkoutForm.getEmail());
        invoice.setUser(currentUser);

        List<InvoiceItem> items = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            InvoiceItem item = new InvoiceItem();
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            items.add(item);
        }
        invoice.setItems(items);

        try {
            Invoice savedInvoice = invoiceService.save(invoice);
            cartService.clearCart();
            flash.addFlashAttribute("success", "¡Pago realizado con éxito! Tu orden de compra N° " + savedInvoice.getId() + " ha sido procesada.");
            return "redirect:/user/orders/view/" + savedInvoice.getId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("title", "Finalizar Compra - Checkout");
            model.addAttribute("cart", cart);
            model.addAttribute("company", companyService.getCompany());
            return "cart/checkout";
        }
    }

    private String getShippingLabel(String code) {
        if ("EXPRESS".equalsIgnoreCase(code)) return "Envío Express Mismo Día";
        if ("RETIRO".equalsIgnoreCase(code)) return "Retiro en Tienda / Sucursal";
        return "Envío Estándar a Domicilio";
    }

    private String getPaymentLabel(String code) {
        if ("TRANSFERENCIA".equalsIgnoreCase(code)) return "Transferencia Bancaria Directa";
        if ("MERCADOPAGO".equalsIgnoreCase(code)) return "Mercado Pago / PayPal";
        return "Tarjeta de Crédito / Débito (Webpay)";
    }

    private String getRedirectUrl(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null && (referer.contains("/index") || referer.contains("/home") || referer.endsWith(":8080/"))) {
            return "redirect:/index";
        }
        return "redirect:/cart";
    }
}
