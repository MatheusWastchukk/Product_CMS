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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

    @Test
    void shouldListProductsWithPaginationAndNameFilter() {
        Category category = new Category("Eletrônico");
        category.setId(1L);
        Product product = new Product("Notebook Pro", "Ultrafino", BigDecimal.valueOf(7000), category);
        product.setId(20L);

        PageRequest pageable = PageRequest.of(0, 10);
        when(productRepository.findByNameContainingIgnoreCase("Notebook", pageable))
                .thenReturn(new PageImpl<>(List.of(product), pageable, 1));

        Page<ProductResponseDTO> response = productService.list(null, " Notebook ", pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals("Notebook Pro", response.getContent().getFirst().name());
    }

    @Test
    void shouldListProductsByCategoryWithPagination() {
        Category category = new Category("Livros");
        category.setId(3L);
        Product product = new Product("Clean Code", "Livro", BigDecimal.valueOf(120), category);
        product.setId(30L);

        PageRequest pageable = PageRequest.of(0, 10);
        when(categoryRepository.existsById(3L)).thenReturn(true);
        when(productRepository.findByCategoryId(3L, pageable)).thenReturn(new PageImpl<>(List.of(product), pageable, 1));

        Page<ProductResponseDTO> response = productService.list(3L, "", pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals("Livros", response.getContent().getFirst().categoryName());
    }

    @Test
    void shouldThrowExceptionWhenListingProductsFromUnknownCategory() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(categoryRepository.existsById(99L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.list(99L, null, pageable)
        );

        assertEquals("Categoria não encontrada", exception.getMessage());
        verify(productRepository, never()).findByCategoryId(99L, pageable);
    }

    @Test
    void shouldUpdateProductAndPublishEvent() {
        Category oldCategory = new Category("Antiga");
        oldCategory.setId(1L);
        Category newCategory = new Category("Nova");
        newCategory.setId(2L);
        Product product = new Product("Nome antigo", "Descrição antiga", BigDecimal.TEN, oldCategory);
        product.setId(40L);

        ProductRequestDTO request = new ProductRequestDTO(
                "Nome novo",
                "Descrição nova",
                BigDecimal.valueOf(99),
                2L,
                List.of(new ProductAttributeDTO("cor", "preto"))
        );

        when(productRepository.findById(40L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
        when(attributeRepository.findByCategoryId(2L)).thenReturn(List.of(new CategoryAttribute("cor", newCategory)));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDTO response = productService.update(40L, request);

        assertEquals("Nome novo", response.name());
        assertEquals("Nova", response.categoryName());
        assertEquals(1, response.attributes().size());
        verify(eventPublisher).publish("UPDATED", product);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingUnknownProduct() {
        ProductRequestDTO request = new ProductRequestDTO("Produto", "", BigDecimal.ONE, 1L, List.of());
        when(productRepository.findById(404L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.update(404L, request)
        );

        assertEquals("Produto não encontrado", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }
}
