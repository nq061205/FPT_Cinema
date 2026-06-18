package com.group6.mvc.fpt_cinema.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.group6.mvc.fpt_cinema.entity.Permission;
import com.group6.mvc.fpt_cinema.entity.Role;
import com.group6.mvc.fpt_cinema.entity.User;
import com.group6.mvc.fpt_cinema.repository.PermissionRepository;
import com.group6.mvc.fpt_cinema.repository.RolePermissionRepository;
import com.group6.mvc.fpt_cinema.repository.RoleRepository;
import com.group6.mvc.fpt_cinema.repository.UserPermissionRepository;
import com.group6.mvc.fpt_cinema.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PermissionManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPermissionRepository userPermissionRepository;

    private Role customerRole;
    private Role staffRole;
    private User user;
    private Permission permission;

    @BeforeEach
    void setUp() {
        customerRole = roleRepository.save(new Role(null, "permission-test-customer"));
        staffRole = roleRepository.save(new Role(null, "permission-test-staff"));

        user = new User();
        user.setRole(customerRole);
        user.setFullName("Permission Test User");
        user.setEmail("permission.user@test.com");
        user.setPhone("0911111111");
        user.setPasswordHash("$2a$10$testHash");
        user = userRepository.save(user);

        permission = new Permission();
        permission.setPermissionCode("REPORT_EXPORT");
        permission.setPermissionName("Export report");
        permission = permissionRepository.save(permission);
    }

    @Test
    void adminCanCreateAndUpdatePermission() throws Exception {
        mockMvc.perform(post("/api/permissions")
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "permissionCode": "movie_publish",
                                  "permissionName": "Publish movie",
                                  "description": "Allows publishing movies"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.permissionCode").value("MOVIE_PUBLISH"))
                .andExpect(jsonPath("$.result.permissionName").value("Publish movie"));

        Permission created = permissionRepository
                .findByPermissionCodeIgnoreCase("MOVIE_PUBLISH")
                .orElseThrow();

        mockMvc.perform(put("/api/permissions/{id}", created.getId())
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "permissionName": "Publish cinema movie",
                                  "description": "Updated description"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(created.getId()))
                .andExpect(jsonPath("$.result.permissionName")
                        .value("Publish cinema movie"))
                .andExpect(jsonPath("$.result.description")
                        .value("Updated description"));
    }

    @Test
    void assigningPermissionToRoleIsIdempotent() throws Exception {
        String endpoint = "/api/roles/{roleId}/permissions/{permissionId}";

        mockMvc.perform(put(endpoint, staffRole.getId(), permission.getId())
                        .with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.roleId").value(staffRole.getId()))
                .andExpect(jsonPath("$.result.permissionCode").value("REPORT_EXPORT"));

        mockMvc.perform(put(endpoint, staffRole.getId(), permission.getId())
                        .with(adminJwt()))
                .andExpect(status().isOk());

        assertThat(rolePermissionRepository
                .findByRoleIdAndPermissionId(staffRole.getId(), permission.getId()))
                .isPresent();
        assertThat(rolePermissionRepository.count()).isEqualTo(1);
    }

    @Test
    void adminCanAssignAndOverridePermissionForUser() throws Exception {
        String endpoint = "/api/user/{userId}/permissions/{permissionId}";

        mockMvc.perform(put(endpoint, user.getId(), permission.getId())
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "isGranted": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.isGranted").value(false));

        mockMvc.perform(put(endpoint, user.getId(), permission.getId())
                        .with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "isGranted": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.isGranted").value(true));

        assertThat(userPermissionRepository
                .findByUserIdAndPermissionId(user.getId(), permission.getId()))
                .get()
                .extracting("isGranted")
                .isEqualTo(true);
        assertThat(userPermissionRepository.count()).isEqualTo(1);
    }

    @Test
    void adminCanAssignRoleToUser() throws Exception {
        mockMvc.perform(put("/api/user/{userId}/role/{roleId}",
                        user.getId(),
                        staffRole.getId())
                        .with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(user.getId()))
                .andExpect(jsonPath("$.result.roleId").value(staffRole.getId()))
                .andExpect(jsonPath("$.result.role").value("permission-test-staff"));

        assertThat(userRepository.findById(user.getId()))
                .get()
                .extracting(savedUser -> savedUser.getRole().getId())
                .isEqualTo(staffRole.getId());
    }

    @Test
    void userWithoutManagementAuthorityIsForbidden() throws Exception {
        mockMvc.perform(post("/api/permissions")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_CUSTOMER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "permissionCode": "FORBIDDEN_PERMISSION",
                                  "permissionName": "Forbidden"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(1008));
    }

    private org.springframework.test.web.servlet.request.RequestPostProcessor adminJwt() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
