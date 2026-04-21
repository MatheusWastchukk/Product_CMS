package com.aztech.productcms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aztech.productcms.dto.UserCreateRequestDTO;
import com.aztech.productcms.dto.UserResponseDTO;
import com.aztech.productcms.dto.UserUpdateRequestDTO;
import com.aztech.productcms.exception.BusinessException;
import com.aztech.productcms.exception.ResourceNotFoundException;
import com.aztech.productcms.model.AppUser;
import com.aztech.productcms.repository.AppUserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private AuthService authService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder, authService);
    }

    @Test
    void shouldListUsersWithPagination() {
        AppUser user = user(1L, "Maria", "maria", "maria@email.com");
        PageRequest pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(user), pageable, 1));
        when(authService.toResponse(user)).thenReturn(new UserResponseDTO(1L, "Maria", "maria", "maria@email.com", "ADMIN"));

        Page<UserResponseDTO> response = userService.list(pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals("maria", response.getContent().getFirst().username());
    }

    @Test
    void shouldCreateUserWithDefaultPassword() {
        AppUser savedUser = user(2L, "João", "joao", "joao@email.com");

        when(userRepository.findByUsernameIgnoreCase("joao")).thenReturn(Optional.empty());
        when(userRepository.findByEmailIgnoreCase("joao@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(UserService.DEFAULT_PASSWORD)).thenReturn("hash");
        when(userRepository.save(any(AppUser.class))).thenReturn(savedUser);
        when(authService.toResponse(savedUser)).thenReturn(new UserResponseDTO(2L, "João", "joao", "joao@email.com", "ADMIN"));

        UserResponseDTO response = userService.create(new UserCreateRequestDTO(" João ", " joao ", " joao@email.com "));

        assertEquals(2L, response.id());
        assertEquals("joao", response.username());
    }

    @Test
    void shouldRejectDuplicatedUsernameOnCreate() {
        AppUser existingUser = user(3L, "Ana", "ana", null);
        when(userRepository.findByUsernameIgnoreCase("ana")).thenReturn(Optional.of(existingUser));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.create(new UserCreateRequestDTO("Ana", "ana", ""))
        );

        assertEquals("Usuário já cadastrado", exception.getMessage());
        verify(userRepository, never()).save(any(AppUser.class));
    }

    @Test
    void shouldUpdateUserAndResetPassword() {
        AppUser existingUser = user(4L, "Nome antigo", "antigo", "old@email.com");
        existingUser.setAuthToken("token");

        when(userRepository.findById(4L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsernameIgnoreCase("novo")).thenReturn(Optional.empty());
        when(userRepository.findByEmailIgnoreCase("novo@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(UserService.DEFAULT_PASSWORD)).thenReturn("new-hash");
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(authService.toResponse(existingUser)).thenReturn(new UserResponseDTO(4L, "Nome novo", "novo", "novo@email.com", "ADMIN"));

        UserResponseDTO response = userService.update(
                4L,
                new UserUpdateRequestDTO("Nome novo", "novo", "novo@email.com", true)
        );

        assertEquals("novo", response.username());
        assertNull(existingUser.getAuthToken());
        assertEquals("new-hash", existingUser.getPasswordHash());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingUnknownUser() {
        when(userRepository.findById(404L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.update(404L, new UserUpdateRequestDTO("Nome", "user", "", false))
        );

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(userRepository, never()).save(any(AppUser.class));
    }

    @Test
    void shouldRejectDeletingTheOnlyUser() {
        AppUser user = user(5L, "Solo", "solo", null);

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(userRepository.count()).thenReturn(1L);

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.delete(5L));

        assertEquals("Não é possível excluir o único usuário do sistema", exception.getMessage());
        verify(userRepository, never()).delete(user);
    }

    @Test
    void shouldDeleteUserWhenThereIsMoreThanOneUser() {
        AppUser user = user(6L, "Remover", "remover", null);

        when(userRepository.findById(6L)).thenReturn(Optional.of(user));
        when(userRepository.count()).thenReturn(2L);

        userService.delete(6L);

        verify(userRepository).delete(user);
    }

    @Test
    void shouldRejectDuplicatedEmailOnUpdate() {
        AppUser currentUser = user(7L, "Atual", "atual", "atual@email.com");
        AppUser otherUser = user(8L, "Outro", "outro", "outro@email.com");

        when(userRepository.findById(7L)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUsernameIgnoreCase("atual")).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmailIgnoreCase("outro@email.com")).thenReturn(Optional.of(otherUser));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.update(7L, new UserUpdateRequestDTO("Atual", "atual", "outro@email.com", false))
        );

        assertEquals("E-mail já cadastrado", exception.getMessage());
        verify(passwordEncoder, never()).encode(eq(UserService.DEFAULT_PASSWORD));
        verify(userRepository, never()).save(any(AppUser.class));
    }

    private AppUser user(Long id, String name, String username, String email) {
        AppUser user = new AppUser(name, username, email, "hash", "ADMIN");
        user.setId(id);
        return user;
    }
}
