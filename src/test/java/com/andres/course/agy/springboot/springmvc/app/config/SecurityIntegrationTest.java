package com.andres.course.agy.springboot.springmvc.app.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class SecurityIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Público general puede acceder al home, index, carrito, registro y detalle de producto sin estar autenticado")
    public void publicEndpointsPermitAll() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/index"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/admin/products/detail/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Usuario anónimo al intentar acceder a la administración de productos o usuarios debe redirigirse al login")
    public void anonymousAccessToAdminProductsRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/products"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Login exitoso con usuario admin")
    public void successfulLoginAsAdmin() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("12345"))
                .andExpect(authenticated().withUsername("admin").withRoles("ADMIN", "BILLING", "USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
    }

    @Test
    @DisplayName("Login exitoso con usuario billing")
    public void successfulLoginAsBilling() throws Exception {
        mockMvc.perform(formLogin("/login").user("billing").password("12345"))
                .andExpect(authenticated().withUsername("billing").withRoles("BILLING", "USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
    }

    @Test
    @DisplayName("Login exitoso con usuario común (user)")
    public void successfulLoginAsRegularUser() throws Exception {
        mockMvc.perform(formLogin("/login").user("user").password("12345"))
                .andExpect(authenticated().withUsername("user").withRoles("USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
    }

    @Test
    @DisplayName("Login fallido con contraseña incorrecta")
    public void failedLoginWithWrongPassword() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("wrongpassword"))
                .andExpect(unauthenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Usuario con rol ADMIN tiene acceso al panel de administración de productos y de usuarios")
    public void adminCanAccessAdminPanels() throws Exception {
        mockMvc.perform(get("/admin/products"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "billing", roles = {"BILLING", "USER"})
    @DisplayName("Usuario con rol BILLING tiene acceso denegado a la administración de usuarios")
    public void billingUserDeniedAccessToAdminUsers() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Usuario común (USER) tiene acceso denegado a la administración de productos y usuarios")
    public void regularUserDeniedAccessToAdminPanels() throws Exception {
        mockMvc.perform(get("/admin/products"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
    }

    @Test
    @DisplayName("Logout cierra la sesión correctamente")
    public void logoutSuccess() throws Exception {
        mockMvc.perform(logout("/logout"))
                .andExpect(unauthenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }
}
