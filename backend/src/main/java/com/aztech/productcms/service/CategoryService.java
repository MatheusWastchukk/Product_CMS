package com.aztech.productcms.service;

import com.aztech.productcms.dto.CategoryAttributeRequestDTO;
import com.aztech.productcms.dto.CategoryAttributeResponseDTO;
import com.aztech.productcms.dto.CategoryRequestDTO;
import com.aztech.productcms.dto.CategoryResponseDTO;
import com.aztech.productcms.exception.BusinessException;
import com.aztech.productcms.exception.ResourceNotFoundException;
import com.aztech.productcms.model.Category;
import com.aztech.productcms.model.CategoryAttribute;
import com.aztech.productcms.repository.CategoryAttributeRepository;
import com.aztech.productcms.repository.CategoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryAttributeRepository attributeRepository;

    public CategoryService(CategoryRepository categoryRepository, CategoryAttributeRepository attributeRepository) {
        this.categoryRepository = categoryRepository;
        this.attributeRepository = attributeRepository;
    }

    @Transactional
    public CategoryResponseDTO create(CategoryRequestDTO request) {
        String name = request.name().trim();
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new BusinessException("Categoria ja cadastrada");
        }

        Category category = categoryRepository.save(new Category(name));
        return toResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> list() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<CategoryAttributeResponseDTO> addAttributes(Long categoryId, CategoryAttributeRequestDTO request) {
        Category category = findCategory(categoryId);

        request.attributes().stream()
                .map(String::trim)
                .filter(attribute -> !attribute.isBlank())
                .distinct()
                .filter(attribute -> !attributeRepository.existsByCategoryIdAndNameIgnoreCase(categoryId, attribute))
                .map(attribute -> new CategoryAttribute(attribute, category))
                .forEach(attributeRepository::save);

        return listAttributes(categoryId);
    }

    @Transactional(readOnly = true)
    public List<CategoryAttributeResponseDTO> listAttributes(Long categoryId) {
        findCategory(categoryId);
        return attributeRepository.findByCategoryId(categoryId)
                .stream()
                .map(attribute -> new CategoryAttributeResponseDTO(attribute.getId(), attribute.getName()))
                .toList();
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria nao encontrada"));
    }

    private CategoryResponseDTO toResponse(Category category) {
        return new CategoryResponseDTO(category.getId(), category.getName());
    }
}
