package com.group6.mvc.fpt_cinema.service.impl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.group6.mvc.fpt_cinema.dto.request.CreateAccountRequest;
import com.group6.mvc.fpt_cinema.dto.request.LoginRequest;
import com.group6.mvc.fpt_cinema.dto.response.LoginResponse;
import com.group6.mvc.fpt_cinema.entity.Permission;
import com.group6.mvc.fpt_cinema.entity.Role;
import com.group6.mvc.fpt_cinema.entity.Role_Permission;
import com.group6.mvc.fpt_cinema.entity.User;
import com.group6.mvc.fpt_cinema.entity.User_Permission;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.enums.UserStatus;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.mapper.UserMapper;
import com.group6.mvc.fpt_cinema.repository.RolePermissionRepository;
import com.group6.mvc.fpt_cinema.repository.RoleRepository;
import com.group6.mvc.fpt_cinema.repository.UserPermissionRepository;
import com.group6.mvc.fpt_cinema.repository.UserRepository;
import com.group6.mvc.fpt_cinema.security.JwtService;
import com.group6.mvc.fpt_cinema.security.JwtService.GeneratedToken;
import com.group6.mvc.fpt_cinema.security.RoleIds;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

        @Mock
        private UserRepository userRepository;
        @Mock
        private RoleRepository roleRepository;
        @Mock
        private RolePermissionRepository rolePermissionRepository;
        @Mock
        private UserPermissionRepository userPermissionRepository;
        @Mock
        private UserMapper userMapper;
        @Mock
        private PasswordEncoder passwordEncoder;
        @Mock
        private JwtService jwtService;

        private UserServiceImpl userService;

        @BeforeEach
        void setUp() {
                userService = new UserServiceImpl(
                                userRepository,
                                roleRepository,
                                rolePermissionRepository,
                                userPermissionRepository,
                                userMapper,
                                passwordEncoder,
                                jwtService);
        }

        @Test
        void loginMergesRolePermissionsWithUserOverrides() {
                User user = activeUser();
                when(userRepository.findOneByEmailIgnoreCase("manager@cinema.test"))
                                .thenReturn(Optional.of(user));
                when(passwordEncoder.matches("correct-password", user.getPasswordHash())).thenReturn(true);
                when(rolePermissionRepository.findAllWithPermissionByRoleId(2))
                                .thenReturn(List.of(
                                                rolePermission("MOVIE_READ"),
                                                rolePermission("MOVIE_UPDATE")));
                when(userPermissionRepository.findAllWithPermissionByUserId(10))
                                .thenReturn(List.of(
                                                userPermission("MOVIE_UPDATE", false),
                                                userPermission("REPORT_READ", true)));
                when(jwtService.generateAccessToken(any(User.class), any()))
                                .thenReturn(new GeneratedToken("signed-jwt", 3600));

                LoginResponse response = userService.login(
                                new LoginRequest(" manager@cinema.test ", "correct-password"));

                assertThat(response.getAccessToken()).isEqualTo("signed-jwt");
                assertThat(response.getRole()).isEqualTo("manager");
                assertThat(response.getPermissions())
                                .containsExactly("MOVIE_READ", "REPORT_READ");

                @SuppressWarnings("unchecked")
                ArgumentCaptor<List<String>> permissionsCaptor = ArgumentCaptor.forClass(List.class);
                verify(jwtService).generateAccessToken(any(User.class), permissionsCaptor.capture());
                assertThat(permissionsCaptor.getValue())
                                .containsExactly("MOVIE_READ", "REPORT_READ");
        }

        @Test
        void loginRejectsInvalidPasswordWithoutGeneratingToken() {
                User user = activeUser();
                when(userRepository.findOneByEmailIgnoreCase("manager@cinema.test"))
                                .thenReturn(Optional.of(user));
                when(passwordEncoder.matches("wrong-password", user.getPasswordHash())).thenReturn(false);

                assertThatThrownBy(() -> userService.login(new LoginRequest("manager@cinema.test", "wrong-password")))
                                .isInstanceOf(AppException.class)
                                .extracting(exception -> ((AppException) exception).getErrorCode())
                                .isEqualTo(ErrorCode.INVALID_CREDENTIALS);

                verify(jwtService, never()).generateAccessToken(any(), any());
        }

        @Test
        void createAccountAlwaysAssignsCustomerRole() {
                CreateAccountRequest request = new CreateAccountRequest(
                                "Cinema Customer",
                                "customer@cinema.test",
                                "0900000000",
                                "password");
                User mappedUser = new User();
                mappedUser.setEmail(request.getEmail());
                Role customerRole = new Role(RoleIds.CUSTOMER, "customer");

                when(userRepository.getUserByEmail(request.getEmail())).thenReturn(null);
                when(userRepository.getUserByPhone(request.getPhone())).thenReturn(null);
                when(userMapper.toEntity(request)).thenReturn(mappedUser);
                when(roleRepository.findById(RoleIds.CUSTOMER)).thenReturn(Optional.of(customerRole));
                when(userRepository.save(mappedUser)).thenReturn(mappedUser);

                userService.createAccount(request);

                assertThat(mappedUser.getRole()).isSameAs(customerRole);
                verify(roleRepository).findById(RoleIds.CUSTOMER);
                verify(userRepository).save(mappedUser);
        }

        private User activeUser() {
                Role role = new Role(2, "manager");
                User user = new User();
                user.setId(10);
                user.setRole(role);
                user.setFullName("Cinema Manager");
                user.setEmail("manager@cinema.test");
                user.setPasswordHash("$2a$10$existingHash");
                user.setStatus(UserStatus.ACTIVE);
                return user;
        }

        private Role_Permission rolePermission(String code) {
                Role_Permission rolePermission = new Role_Permission();
                rolePermission.setPermission(permission(code));
                return rolePermission;
        }

        private User_Permission userPermission(String code, boolean isGranted) {
                User_Permission userPermission = new User_Permission();
                userPermission.setPermission(permission(code));
                userPermission.setIsGranted(isGranted);
                return userPermission;
        }

        private Permission permission(String code) {
                Permission permission = new Permission();
                permission.setPermissionCode(code);
                permission.setPermissionName(code);
                return permission;
        }
}
