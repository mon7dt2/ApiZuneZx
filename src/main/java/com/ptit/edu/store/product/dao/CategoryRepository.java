package com.ptit.edu.store.product.dao;

import com.ptit.edu.store.product.models.data.Category;
import com.ptit.edu.store.product.models.view.CategoryView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, String> {

    @Query("select new com.ptit.edu.store.product.models.view.CategoryView(c) " +
            " from Category c ")
    Page<CategoryView> getAllCategory(Pageable pageable);

}
