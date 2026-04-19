package com.aztech.productcms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aztech.productcms.dto.ProductAttributeDTO;
import com.aztech.productcms.dto.ProductRequestDTO;
import com.aztech.productcms.dto.ProductResponseDTO;
import com.aztech.productcms.exception.BusinessException;
import com.aztech.productcms.exception.ResourceNotFoundException;
import com.aztech.productcms.model.Category;
import com.aztech.productcms.model.CategoryAttribute;
import com.aztech.productcms.model.Product;
import com.aztech.productcms.repository.CategoryAttributeRepository;
import com.aztech.productcms.repository.CategoryRepository;
import com.aztech.productcms.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryAttributeRepository attributeRepository;

    @Mock
    private ProductEventPublisher eventPublisher;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, categoryRepository, attributeRepository, eventPublisher);
    }

    @Test
    void shouldCreateProductWithValidCategoryAndAttributes() {
        Category category = new Category("Eletrônico");
        category.setId(1L);

        ProductRequestDTO request = new ProductRequestDTO(
                "Notebook X",
                "Notebook para trabalho",
                BigDecimal.valueOf(4500),
                1L,
                List.of(
                        new ProductAttributeDTO("marca", "Dell"),
                        new ProductAttributeDTO("voltagem", "220V")
                )
        );

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(attributeRepository.findByCategoryId(1L)).thenReturn(List.of(
                new CategoryAttribute("marca", category),
                new CategoryAttribute("voltagem", category),
                new CategoryAttribute("garantia", category)
        ));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(10L);
            return product;
        });

        ProductResponseDTO response = productService.create(request);

        assertEquals(10L, response.id());
        assertEquals("Notebook X", response.name());
        assertEquals("Eletrônico", response.categoryName());
        assertEquals(2, response.attributes().size());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenCategoryDoesNotExist() {
        ProductRequestDTO request = new ProductRequestDTO(
                "Camiseta",
                "Camiseta basica",
                BigDecimal.valueOf(79.9),
                99L,
                List.of()
        );

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.create(request)
        );

        assertEquals("Categoria não encontrada", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldRejectAttributeThatDoesNotBelongToCategory() {
        Category category = new Category("Livro");
        category.setId(3L);

        ProductRequestDTO request = new ProductRequestDTO(
                "Clean Code",
                "Livro de engenharia de software",
                BigDecimal.valueOf(120),
                3L,
                List.of(new ProductAttributeDTO("voltagem", "220V"))
        );

        when(categoryRepository.findById(3L)).thenReturn(Optional.of(category));
        when(attributeRepository.findByCategoryId(3L)).thenReturn(List.of(
                new CategoryAttribute("autor", category),
                new CategoryAttribute("editora", category),
                new CategoryAttribute("páginas", category)
        ));

        BusinessException exception = assertThrows(BusinessException.class, () -> productService.create(request));

        assertTrue(exception.getMessage().contains("Atributo inválido"));
        verify(productRepository, never()).save(any(Product.class));
    }
}
