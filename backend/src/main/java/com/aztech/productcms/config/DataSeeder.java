package com.aztech.productcms.config;

import com.aztech.productcms.model.AppUser;
import com.aztech.productcms.model.Category;
import com.aztech.productcms.model.CategoryAttribute;
import com.aztech.productcms.repository.AppUserRepository;
import com.aztech.productcms.repository.CategoryAttributeRepository;
import com.aztech.productcms.repository.CategoryRepository;
import com.aztech.productcms.service.UserService;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final CategoryAttributeRepository attributeRepository;
    private final AppUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataSeeder(
            CategoryRepository categoryRepository,
            CategoryAttributeRepository attributeRepository,
            AppUserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.categoryRepository = categoryRepository;
        this.attributeRepository = attributeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedCategory("Eletrônico", List.of("marca", "voltagem", "garantia"));
        seedCategory("Roupa", List.of("tamanho", "cor", "material"));
        seedCategory("Livro", List.of("autor", "editora", "páginas"));
        seedAdminUser();
    }

    private void seedCategory(String categoryName, List<String> attributes) {
        Category category = categoryRepository.findByNameIgnoreCase(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));

        attributes.stream()
                .filter(attribute -> !attributeRepository.existsByCategoryIdAndNameIgnoreCase(category.getId(), attribute))
                .map(attribute -> new CategoryAttribute(attribute, "string", category))
                .forEach(attributeRepository::save);
    }

    private void seedAdminUser() {
        if (userRepository.existsByUsernameIgnoreCase("admin")) {
            return;
        }

        userRepository.save(new AppUser(
                "Administrador",
                "admin",
                "admin@aztech.com",
                passwordEncoder.encode(UserService.DEFAULT_PASSWORD),
                "ADMIN"
        ));
    }
}
