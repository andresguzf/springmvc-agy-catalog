package com.andres.course.agy.springboot.springmvc.app.services;

import com.andres.course.agy.springboot.springmvc.app.models.Company;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
public class CompanyIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private CompanyService companyService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void adminCanViewAndEditCompanyDetails() throws Exception {
        mockMvc.perform(get("/admin/company"))
                .andExpect(status().isOk())
                .andExpect(view().name("company/form"))
                .andExpect(model().attributeExists("company"));

        mockMvc.perform(post("/admin/company").with(csrf())
                        .param("name", "Mi Empresa SpA")
                        .param("taxId", "99.888.777-6")
                        .param("address", "Av. Providencia 456, Santiago")
                        .param("phone", "+56 2 2345 6789")
                        .param("email", "contacto@miempresa.cl")
                        .param("website", "www.miempresa.cl"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/company"));

        Company updated = companyService.getCompany();
        assertEquals("Mi Empresa SpA", updated.getName());
        assertEquals("99.888.777-6", updated.getTaxId());
        assertEquals("Av. Providencia 456, Santiago", updated.getAddress());
        assertEquals("+56 2 2345 6789", updated.getPhone());
    }

    @Test
    @WithMockUser(username = "billing", roles = {"BILLING", "USER"})
    public void billingUserDeniedAccessToCompanyEdit() throws Exception {
        mockMvc.perform(get("/admin/company"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
    }
}
