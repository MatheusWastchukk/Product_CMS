package com.aztech.productcms.repository;

import com.aztech.productcms.model.CategoryAttribute;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, Long> {

    List<CategoryAttribute> findByCategoryId(Long categoryId);

    boolean existsByCategoryIdAndNameIgnoreCase(Long categoryId, String name);
}
