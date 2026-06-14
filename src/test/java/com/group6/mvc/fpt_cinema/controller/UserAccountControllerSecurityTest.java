package com.group6.mvc.fpt_cinema.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.group6.mvc.fpt_cinema.entity.Role;
import com.group6.mvc.fpt_cinema.entity.User;
import com.group6.mvc.fpt_cinema.repository.RoleRepository;
import com.group6.mvc.fpt_cinema.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserAccountControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    private Integer userId;

    @BeforeEach
    void createUser() {
        Role customerRole = roleRepository.save(new Role(null, "customer-test"));

        User user = new User();
        user.setRole(customerRole);
        user.setFullName("Postman Customer");
        user.setEmail("postman.customer@test.com");
        user.setPhone("0901234567");
        user.setPasswordHash("$2a$10$secretHashMustNeverBeReturned");
        userId = userRepository.save(user).getId();
    }

    @Test
    void unauthenticatedUserReceivesUnauthorized() throws Exception {
        mockMvc.perform(get("/api/user/user-list"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void customerReceivesForbidden() throws Exception {
        mockMvc.perform(get("/api/user/user-list")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void managerCanViewUsersWithoutPasswordHash() throws Exception {
        mockMvc.perform(get("/api/user/user-list")
                        .with(jwt().authorities(new SimpleGrantedAuthority("USER_VIEW_LIST"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].email").value("postman.customer@test.com"))
                .andExpect(jsonPath("$.result[0].role").value("customer-test"))
                .andExpect(jsonPath("$.result[0].passwordHash").doesNotExist());
    }

    @Test
    void adminCanViewUsers() throws Exception {
        mockMvc.perform(get("/api/user/user-list")
                        .with(jwt().authorities(new SimpleGrantedAuthority("USER_VIEW_LIST"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].email").value("postman.customer@test.com"));
    }

    @Test
    void authorizedUserCanViewUserDetailWithoutPasswordHash() throws Exception {
        mockMvc.perform(get("/api/user/{id}", userId)
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("USER_VIEW_DETAIL"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(userId))
                .andExpect(jsonPath("$.result.email").value("postman.customer@test.com"))
                .andExpect(jsonPath("$.result.passwordHash").doesNotExist());
    }

    @Test
    void authorizedUserCanUpdateUser() throws Exception {
        mockMvc.perform(put("/api/user/{id}", userId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("USER_UPDATE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Updated Customer",
                                  "email": "updated.customer@test.com",
                                  "phone": "0907654321",
                                  "status": "INACTIVE"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.result.fullName").value("Updated Customer"))
                .andExpect(jsonPath("$.result.email").value("updated.customer@test.com"))
                .andExpect(jsonPath("$.result.status").value("INACTIVE"))
                .andExpect(jsonPath("$.result.passwordHash").doesNotExist());
    }
}
