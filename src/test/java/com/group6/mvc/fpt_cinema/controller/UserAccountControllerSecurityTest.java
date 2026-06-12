package com.group6.mvc.fpt_cinema.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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

    @BeforeEach
    void createUser() {
        Role customerRole = roleRepository.save(new Role(null, "customer-test"));

        User user = new User();
        user.setRole(customerRole);
        user.setFullName("Postman Customer");
        user.setEmail("postman.customer@test.com");
        user.setPhone("0901234567");
        user.setPasswordHash("$2a$10$secretHashMustNeverBeReturned");
        userRepository.save(user);
    }

    @Test
    void unauthenticatedUserReceivesUnauthorized() throws Exception {
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void customerReceivesForbidden() throws Exception {
        mockMvc.perform(get("/api/user")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void managerCanViewUsersWithoutPasswordHash() throws Exception {
        mockMvc.perform(get("/api/user")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_MANAGER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].email").value("postman.customer@test.com"))
                .andExpect(jsonPath("$.result[0].role").value("customer-test"))
                .andExpect(jsonPath("$.result[0].passwordHash").doesNotExist());
    }

    @Test
    void adminCanViewUsers() throws Exception {
        mockMvc.perform(get("/api/user")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].email").value("postman.customer@test.com"));
    }
}
