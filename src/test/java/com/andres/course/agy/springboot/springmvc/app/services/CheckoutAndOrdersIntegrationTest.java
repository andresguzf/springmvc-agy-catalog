package com.andres.course.agy.springboot.springmvc.app.services;

import com.andres.course.agy.springboot.springmvc.app.models.Order;
import com.andres.course.agy.springboot.springmvc.app.models.Product;
import com.andres.course.agy.springboot.springmvc.app.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
public class CheckoutAndOrdersIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void unauthenticatedUserRedirectedToLoginOnCheckout() throws Exception {
        mockMvc.perform(get("/cart/checkout"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void authenticatedUserCanProcessCheckout() throws Exception {
        Product product = productRepository.findAll().get(0);
        int initialStock = product.getStock();

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/cart/add/" + product.getId()).session(session).with(csrf()))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/cart/checkout").session(session).with(csrf())
                        .param("firstName", "Carlos")
                        .param("lastName", "González")
                        .param("rut", "15.999.888-7")
                        .param("email", "carlos@test.com")
                        .param("phone", "+56911223344")
                        .param("address", "Av. Brasil 500")
                        .param("city", "Valparaíso")
                        .param("shippingMethod", "ESTANDAR")
                        .param("paymentMethod", "TARJETA"))
                .andExpect(status().is3xxRedirection());

        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertEquals(initialStock - 1, updatedProduct.getStock());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void userCanViewOwnPurchaseHistory() throws Exception {
        mockMvc.perform(get("/user/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/orders/list"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    @WithMockUser(username = "billing", roles = {"BILLING"})
    public void billingUserCanViewAllOrders() throws Exception {
        mockMvc.perform(get("/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/orders/list"))
                .andExpect(model().attributeExists("orders"));
    }
}
