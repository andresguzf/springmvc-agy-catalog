package com.andres.course.agy.springboot.springmvc.app.services;

import com.andres.course.agy.springboot.springmvc.app.models.Invoice;
import com.andres.course.agy.springboot.springmvc.app.models.InvoiceItem;
import com.andres.course.agy.springboot.springmvc.app.models.Product;
import com.andres.course.agy.springboot.springmvc.app.models.User;
import com.andres.course.agy.springboot.springmvc.app.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
public class InvoiceIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "billing", roles = {"BILLING", "USER"})
    public void billingUserCanCreateInvoiceAndDeductStock() throws Exception {
        List<Product> products = productRepository.findAll();
        assertFalse(products.isEmpty(), "DB debe tener productos sembrados.");
        Product product = products.get(0);
        int initialStock = product.getStock();
        assertTrue(initialStock >= 2, "Stock inicial debe ser mayor o igual a 2.");

        mockMvc.perform(post("/admin/invoices/form").with(csrf())
                        .param("customerName", "Empresa Cliente Test SpA")
                        .param("taxId", "77.123.456-7")
                        .param("description", "Facturación de prueba por equipos")
                        .param("observation", "Términos a 30 días")
                        .param("item_id[]", String.valueOf(product.getId()))
                        .param("quantity[]", "2"))
                .andExpect(status().is3xxRedirection());

        // Verify stock deducted by 2
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertEquals(initialStock - 2, updatedProduct.getStock());
    }

    @Test
    @WithMockUser(username = "billing", roles = {"BILLING", "USER"})
    public void validationFailurePreservesSelectedItemsInModel() throws Exception {
        List<Product> products = productRepository.findAll();
        Product product = products.get(0);

        mockMvc.perform(post("/admin/invoices/form").with(csrf())
                        .param("customerName", "") // Invalid: blank customer name
                        .param("description", "")  // Invalid: blank description
                        .param("item_id[]", String.valueOf(product.getId()))
                        .param("quantity[]", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("invoices/form"))
                .andExpect(model().attributeExists("selectedItems"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithMockUser(username = "billing", roles = {"BILLING", "USER"})
    public void invoiceCreationFailsIfQuantityExceedsStock() {
        List<Product> products = productRepository.findAll();
        assertFalse(products.isEmpty());
        Product product = products.get(0);

        User billingUser = userService.findByUsername("billing").orElseThrow();

        Invoice invoice = new Invoice();
        invoice.setCustomerName("Cliente Test");
        invoice.setDescription("Prueba exceso de stock");
        invoice.setUser(billingUser);

        InvoiceItem item = new InvoiceItem();
        item.setProduct(product);
        item.setQuantity(product.getStock() + 100);
        invoice.addItem(item);

        assertThrows(IllegalArgumentException.class, () -> {
            invoiceService.save(invoice);
        });
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "BILLING", "USER"})
    public void adminCanAccessInvoicePdfExport() throws Exception {
        User adminUser = userService.findByUsername("admin").orElseThrow();
        List<Product> products = productRepository.findAll();
        Product product = products.get(0);

        Invoice invoice = new Invoice();
        invoice.setCustomerName("Empresa PDF Test");
        invoice.setDescription("Factura de exportación a PDF");
        invoice.setTaxId("12.345.678-9");
        invoice.setUser(adminUser);

        InvoiceItem item = new InvoiceItem();
        item.setProduct(product);
        item.setQuantity(1);
        invoice.addItem(item);

        Invoice saved = invoiceService.save(invoice);

        mockMvc.perform(get("/admin/invoices/view/" + saved.getId()).param("format", "pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=factura_" + saved.getId() + ".pdf"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void regularUserDeniedAccessToInvoices() throws Exception {
        mockMvc.perform(get("/admin/invoices"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
    }
}
