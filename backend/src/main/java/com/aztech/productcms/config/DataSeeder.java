package com.aztech.productcms.config;

import com.aztech.productcms.model.Category;
import com.aztech.productcms.model.CategoryAttribute;
import com.aztech.productcms.repository.CategoryAttributeRepository;
import com.aztech.productcms.repository.CategoryRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final CategoryAttributeRepository attributeRepository;

    public DataSeeder(CategoryRepository categoryRepository, CategoryAttributeRepository attributeRepository) {
        this.categoryRepository = categoryRepository;
        this.attributeRepository = attributeRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedCategory("Eletronico", List.of("marca", "voltagem", "garantia"));
        seedCategory("Roupa", List.of("tamanho", "cor", "material"));
        seedCategory("Livro", List.of("autor", "editora", "paginas"));
    }

    private void seedCategory(String categoryName, List<String> attributes) {
        Category category = categoryRepository.findByNameIgnoreCase(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));

        attributes.stream()
                .filter(attribute -> !attributeRepository.existsByCategoryIdAndNameIgnoreCase(category.getId(), attribute))
                .map(attribute -> new CategoryAttribute(attribute, "string", category))
                .forEach(attributeRepository::save);
    }
}
