package com.aztech.productcms.controller;

import com.aztech.productcms.dto.CategoryAttributeRequestDTO;
import com.aztech.productcms.dto.CategoryAttributeResponseDTO;
import com.aztech.productcms.dto.CategoryRequestDTO;
import com.aztech.productcms.dto.CategoryResponseDTO;
import com.aztech.productcms.dto.PageResponseDTO;
import com.aztech.productcms.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public PageResponseDTO<CategoryResponseDTO> list(@PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return PageResponseDTO.from(categoryService.list(pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDTO create(@Valid @RequestBody CategoryRequestDTO request) {
        return categoryService.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        categoryService.delete(id);
    }

    @GetMapping("/{id}/attributes")
    public PageResponseDTO<CategoryAttributeResponseDTO> listAttributes(
            @PathVariable("id") Long id,
            @PageableDefault(size = 10, sort = "name") Pageable pageable
    ) {
        return PageResponseDTO.from(categoryService.listAttributes(id, pageable));
    }

    @PostMapping("/{id}/attributes")
    @ResponseStatus(HttpStatus.CREATED)
    public List<CategoryAttributeResponseDTO> addAttributes(
            @PathVariable("id") Long id,
            @Valid @RequestBody CategoryAttributeRequestDTO request
    ) {
        return categoryService.addAttributes(id, request);
    }
}
