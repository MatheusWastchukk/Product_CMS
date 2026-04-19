package com.aztech.productcms.service;

import com.aztech.productcms.dto.AuthRequestDTO;
import com.aztech.productcms.dto.AuthResponseDTO;
import com.aztech.productcms.dto.ProfileUpdateRequestDTO;
import com.aztech.productcms.dto.UserResponseDTO;
import com.aztech.productcms.exception.BusinessException;
import com.aztech.productcms.exception.ResourceNotFoundException;
import com.aztech.productcms.model.AppUser;
import com.aztech.productcms.repository.AppUserRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final int TOKEN_HOURS = 8;

    private final AppUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(AppUserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponseDTO login(AuthRequestDTO request) {
        AppUser user = userRepository.findByUsernameIgnoreCase(request.username().trim())
                .orElseThrow(() -> new BusinessException("Usuário ou senha inválidos"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException("Usuário ou senha inválidos");
        }

        user.setAuthToken(UUID.randomUUID().toString());
        user.setTokenExpiresAt(LocalDateTime.now().plusHours(TOKEN_HOURS));
        return new AuthResponseDTO(user.getAuthToken(), toResponse(userRepository.save(user)));
    }

    @Transactional(readOnly = true)
    public AppUser authenticateToken(String rawToken) {
        String token = rawToken == null ? "" : rawToken.replace("Bearer ", "").trim();
        if (token.isBlank()) {
            throw new BusinessException("Token de autenticação não informado");
        }

        AppUser user = userRepository.findByAuthToken(token)
                .orElseThrow(() -> new BusinessException("Token de autenticação inválido"));

        if (user.getTokenExpiresAt() == null || user.getTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Sessão expirada");
        }

        return user;
    }

    @Transactional
    public void logout(String rawToken) {
        AppUser user = authenticateToken(rawToken);
        user.setAuthToken(null);
        user.setTokenExpiresAt(null);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO me(String rawToken) {
        return toResponse(authenticateToken(rawToken));
    }

    @Transactional
    public UserResponseDTO updateProfile(String rawToken, ProfileUpdateRequestDTO request) {
        AppUser user = authenticateToken(rawToken);
        user.setName(request.name().trim());

        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        return toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public AppUser findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    public UserResponseDTO toResponse(AppUser user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}
