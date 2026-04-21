package com.aztech.productcms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aztech.productcms.dto.CategoryAttributeDefinitionDTO;
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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryAttributeRepository attributeRepository;

    @Mock
    private ProductRepository productRepository;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryRepository, attributeRepository, productRepository);
    }

    @Test
    void shouldCreateCategoryWhenNameIsAvailable() {
        CategoryRequestDTO request = new CategoryRequestDTO(" Eletrônicos ");
        Category savedCategory = new Category("Eletrônicos");
        savedCategory.setId(1L);

        when(categoryRepository.existsByNameIgnoreCase("Eletrônicos")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryResponseDTO response = categoryService.create(request);

        assertEquals(1L, response.id());
        assertEquals("Eletrônicos", response.name());
    }

    @Test
    void shouldRejectDuplicatedCategoryName() {
        when(categoryRepository.existsByNameIgnoreCase("Livros")).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> categoryService.create(new CategoryRequestDTO(" Livros "))
        );

        assertEquals("Categoria já cadastrada", exception.getMessage());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void shouldListCategoriesWithPagination() {
        Category category = new Category("Livros");
        category.setId(2L);
        PageRequest pageable = PageRequest.of(0, 10);

        when(categoryRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(category), pageable, 1));

        Page<CategoryResponseDTO> response = categoryService.list(pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals("Livros", response.getContent().getFirst().name());
    }

    @Test
    void shouldDeleteCategoryWithoutProducts() {
        Category category = new Category("Acessórios");
        category.setId(3L);

        when(categoryRepository.findById(3L)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryId(3L)).thenReturn(false);

        categoryService.delete(3L);

        verify(categoryRepository).delete(category);
    }

    @Test
    void shouldRejectDeletingCategoryWithProducts() {
        Category category = new Category("Acessórios");
        category.setId(3L);

        when(categoryRepository.findById(3L)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryId(3L)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> categoryService.delete(3L));

        assertEquals("Categoria possui produtos cadastrados", exception.getMessage());
        verify(categoryRepository, never()).delete(category);
    }

    @Test
    void shouldListCategoryAttributesWithPagination() {
        Category category = new Category("Eletrônicos");
        category.setId(4L);
        CategoryAttribute attribute = new CategoryAttribute("voltagem", "string", category);
        attribute.setId(9L);
        PageRequest pageable = PageRequest.of(0, 5);

        when(categoryRepository.findById(4L)).thenReturn(Optional.of(category));
        when(attributeRepository.findByCategoryId(4L, pageable)).thenReturn(new PageImpl<>(List.of(attribute), pageable, 1));

        Page<CategoryAttributeResponseDTO> response = categoryService.listAttributes(4L, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals("voltagem", response.getContent().getFirst().name());
    }

    @Test
    void shouldThrowExceptionWhenListingAttributesFromUnknownCategory() {
        PageRequest pageable = PageRequest.of(0, 5);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.listAttributes(99L, pageable)
        );

        assertEquals("Categoria não encontrada", exception.getMessage());
        verify(attributeRepository, never()).findByCategoryId(99L, pageable);
    }

    @Test
    void shouldAddOnlyNewAttributes() {
        Category category = new Category("Eletrônicos");
        category.setId(5L);
        CategoryAttribute savedAttribute = new CategoryAttribute("marca", "string", category);
        savedAttribute.setId(10L);

        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));
        when(attributeRepository.existsByCategoryIdAndNameIgnoreCase(5L, "marca")).thenReturn(false);
        when(attributeRepository.save(any(CategoryAttribute.class))).thenReturn(savedAttribute);
        when(attributeRepository.findByCategoryId(5L)).thenReturn(List.of(savedAttribute));

        List<CategoryAttributeResponseDTO> response = categoryService.addAttributes(
                5L,
                new CategoryAttributeRequestDTO(List.of(new CategoryAttributeDefinitionDTO(" marca ", "string")))
        );

        assertEquals(1, response.size());
        assertEquals("marca", response.getFirst().name());
    }
}
