package com.aztech.productcms.service;

import com.aztech.productcms.dto.ProductAttributeDTO;
import com.aztech.productcms.dto.ProductRequestDTO;
import com.aztech.productcms.dto.ProductResponseDTO;
import com.aztech.productcms.exception.BusinessException;
import com.aztech.productcms.exception.ResourceNotFoundException;
import com.aztech.productcms.model.Category;
import com.aztech.productcms.model.CategoryAttribute;
import com.aztech.productcms.model.Product;
import com.aztech.productcms.model.ProductAttributeValue;
import com.aztech.productcms.repository.CategoryAttributeRepository;
import com.aztech.productcms.repository.CategoryRepository;
import com.aztech.productcms.repository.ProductRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryAttributeRepository attributeRepository;
    private final ProductEventPublisher eventPublisher;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            CategoryAttributeRepository attributeRepository,
            ProductEventPublisher eventPublisher
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.attributeRepository = attributeRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        validateAttributes(request, category.getId());

        Product product = new Product(
                request.name().trim(),
                request.description(),
                request.price(),
                category
        );

        if (request.attributes() != null) {
            request.attributes().forEach(attribute -> product.addAttribute(
                    new ProductAttributeValue(attribute.name().trim(), attribute.value().trim())
            ));
        }

        Product savedProduct = productRepository.save(product);
        eventPublisher.publish("CREATED", savedProduct);
        return toResponse(savedProduct);
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        validateAttributes(request, category.getId());

        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(category);
        product.getAttributes().clear();

        if (request.attributes() != null) {
            request.attributes().forEach(attribute -> product.addAttribute(
                    new ProductAttributeValue(attribute.name().trim(), attribute.value().trim())
            ));
        }

        Product savedProduct = productRepository.save(product);
        eventPublisher.publish("UPDATED", savedProduct);
        return toResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> list(Long categoryId, String name, Pageable pageable) {
        String normalizedName = name == null ? "" : name.trim();

        Page<Product> products;
        if (categoryId != null) {
            if (!categoryRepository.existsById(categoryId)) {
                throw new ResourceNotFoundException("Categoria não encontrada");
            }
            products = normalizedName.isBlank()
                    ? productRepository.findByCategoryId(categoryId, pageable)
                    : productRepository.findByCategoryIdAndNameContainingIgnoreCase(categoryId, normalizedName, pageable);
        } else if (normalizedName.isBlank()) {
            products = productRepository.findAll(pageable);
        } else {
            products = productRepository.findByNameContainingIgnoreCase(normalizedName, pageable);
        }

        return products.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        eventPublisher.publish("DELETED", product);
        productRepository.delete(product);
    }

    private void validateAttributes(ProductRequestDTO request, Long categoryId) {
        if (request.attributes() == null || request.attributes().isEmpty()) {
            return;
        }

        Set<String> allowedAttributes = attributeRepository.findByCategoryId(categoryId)
                .stream()
                .map(CategoryAttribute::getName)
                .map(this::normalize)
                .collect(Collectors.toCollection(HashSet::new));

        request.attributes().stream()
                .map(ProductAttributeDTO::name)
                .map(this::normalize)
                .filter(attribute -> !allowedAttributes.contains(attribute))
                .findFirst()
                .ifPresent(attribute -> {
                    throw new BusinessException("Atributo inválido para a categoria selecionada: " + attribute);
                });
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private ProductResponseDTO toResponse(Product product) {
        List<ProductAttributeDTO> attributes = product.getAttributes()
                .stream()
                .map(attribute -> new ProductAttributeDTO(attribute.getAttributeName(), attribute.getAttributeValue()))
                .toList();

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                attributes
        );
    }
}
