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
import com.aztech.productcms.repository.ProductRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final CategoryAttributeRepository attributeRepository;
    private final ProductRepository productRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            CategoryAttributeRepository attributeRepository,
            ProductRepository productRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.attributeRepository = attributeRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CategoryResponseDTO create(CategoryRequestDTO request) {
        String name = request.name().trim();
        logger.info("Solicitada criação de categoria: {}", name);

        if (categoryRepository.existsByNameIgnoreCase(name)) {
            logger.warn("Categoria já cadastrada: {}", name);
            throw new BusinessException("Categoria já cadastrada");
        }

        Category category = categoryRepository.save(new Category(name));
        logger.info("Categoria criada com id {} e nome {}", category.getId(), category.getName());
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
    public void delete(Long categoryId) {
        logger.info("Solicitada exclusão da categoria {}", categoryId);
        Category category = findCategory(categoryId);

        if (productRepository.existsByCategoryId(categoryId)) {
            logger.warn("Categoria {} não pode ser excluída porque possui produtos", categoryId);
            throw new BusinessException("Categoria possui produtos cadastrados");
        }

        categoryRepository.delete(category);
        logger.info("Categoria {} excluída", categoryId);
    }

    @Transactional
    public List<CategoryAttributeResponseDTO> addAttributes(Long categoryId, CategoryAttributeRequestDTO request) {
        logger.info("Solicitada adição de atributos para categoria {}: {}", categoryId, request.attributes());
        Category category = findCategory(categoryId);

        request.attributes().stream()
                .map(attribute -> new AttributeInput(attribute.name().trim(), attribute.type().trim().toLowerCase()))
                .filter(attribute -> !attribute.name().isBlank() && !attribute.type().isBlank())
                .distinct()
                .filter(attribute -> !attributeRepository.existsByCategoryIdAndNameIgnoreCase(categoryId, attribute.name()))
                .map(attribute -> new CategoryAttribute(attribute.name(), attribute.type(), category))
                .forEach(attribute -> {
                    CategoryAttribute savedAttribute = attributeRepository.save(attribute);
                    logger.info(
                            "Atributo criado com id {} para categoria {}: {}",
                            savedAttribute.getId(),
                            categoryId,
                            savedAttribute.getName()
                    );
                });

        return listAttributes(categoryId);
    }

    @Transactional(readOnly = true)
    public List<CategoryAttributeResponseDTO> listAttributes(Long categoryId) {
        findCategory(categoryId);
        return attributeRepository.findByCategoryId(categoryId)
                .stream()
                .map(attribute -> new CategoryAttributeResponseDTO(
                        attribute.getId(),
                        attribute.getName(),
                        attribute.getType()
                ))
                .toList();
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
    }

    private CategoryResponseDTO toResponse(Category category) {
        return new CategoryResponseDTO(category.getId(), category.getName());
    }

    private record AttributeInput(String name, String type) {
    }
}
