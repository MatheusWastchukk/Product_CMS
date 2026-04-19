package com.aztech.productcms.service;

import com.aztech.productcms.dto.UserCreateRequestDTO;
import com.aztech.productcms.dto.UserResponseDTO;
import com.aztech.productcms.dto.UserUpdateRequestDTO;
import com.aztech.productcms.exception.BusinessException;
import com.aztech.productcms.exception.ResourceNotFoundException;
import com.aztech.productcms.model.AppUser;
import com.aztech.productcms.repository.AppUserRepository;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    public static final String DEFAULT_PASSWORD = "senha";

    private final AppUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthService authService;

    public UserService(
            AppUserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder,
            AuthService authService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> list() {
        return userRepository.findAll()
                .stream()
                .map(authService::toResponse)
                .toList();
    }

    @Transactional
    public UserResponseDTO create(UserCreateRequestDTO request) {
        validateUsername(request.username(), null);
        validateEmail(request.email(), null);

        AppUser user = new AppUser(
                request.name().trim(),
                request.username().trim(),
                normalizeEmail(request.email()),
                passwordEncoder.encode(DEFAULT_PASSWORD),
                "ADMIN"
        );

        return authService.toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponseDTO update(Long id, UserUpdateRequestDTO request) {
        AppUser user = findUser(id);
        validateUsername(request.username(), id);
        validateEmail(request.email(), id);

        user.setName(request.name().trim());
        user.setUsername(request.username().trim());
        user.setEmail(normalizeEmail(request.email()));

        if (request.resetPassword()) {
            user.setPasswordHash(passwordEncoder.encode(DEFAULT_PASSWORD));
            user.setAuthToken(null);
            user.setTokenExpiresAt(null);
        }

        return authService.toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        AppUser user = findUser(id);

        if (userRepository.count() <= 1) {
            throw new BusinessException("Não é possível excluir o único usuário do sistema");
        }

        userRepository.delete(user);
    }

    private AppUser findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    private void validateUsername(String username, Long currentId) {
        String normalized = username == null ? "" : username.trim();
        if (normalized.isBlank()) {
            throw new BusinessException("Usuário é obrigatório");
        }

        userRepository.findByUsernameIgnoreCase(normalized)
                .filter(user -> !user.getId().equals(currentId))
                .ifPresent(user -> {
                    throw new BusinessException("Usuário já cadastrado");
                });
    }

    private void validateEmail(String email, Long currentId) {
        String normalized = normalizeEmail(email);
        if (normalized == null) {
            return;
        }

        userRepository.findByEmailIgnoreCase(normalized)
                .filter(user -> !user.getId().equals(currentId))
                .ifPresent(user -> {
                    throw new BusinessException("E-mail já cadastrado");
                });
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim();
    }
}
