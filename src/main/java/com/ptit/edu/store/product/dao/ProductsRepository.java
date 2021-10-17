package com.ptit.edu.store.product.dao;

import com.ptit.edu.store.product.models.data.NumProductBySubCateData;
import com.ptit.edu.store.product.models.data.Product;
import com.ptit.edu.store.product.models.data.SubCategoryData;
import com.ptit.edu.store.product.models.view.ProductPreview;
import com.ptit.edu.store.product.models.view.ProductViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ProductsRepository extends JpaRepository<Product, String>{
    Product findById(String clothesID);

    @Query("select new com.ptit.edu.store.product.models.view.ProductPreview(c) " +
            " from Product c ")
    Page<ProductPreview> getAllClothesPreviews(Pageable pageable);

    @Query("select new com.ptit.edu.store.product.models.view.ProductPreview(c) from Product c where c.category.id = ?1")
    Page<ProductPreview> getSimilarClothesPreviews(Pageable pageable, String categoryID);

    @Query("select new com.ptit.edu.store.product.models.view.ProductViewModel(c) from Product c where c.id = ?1")
    ProductViewModel getClothesViewModel(String clothesID);

    @Query("select p.id from Product p where p.name = ?1")
    String getProductByName(String name);

    @Query("select new com.ptit.edu.store.product.models.view.ProductPreview(p) " +
        "from Product p where p.name like ?1")
    Page<ProductPreview> searchByName(Pageable pageable,String name);

    @Query("select new com.ptit.edu.store.product.models.view.ProductPreview(c) " +
            " from Product c where c.category.id = ?1")
    Page<ProductPreview> getProductByCategory(Pageable pageable, String id);

    @Query("select sum(p.quantity) from Product p")
    long totalProduct();

    long countByDescription(String description);

    @Query(value = "select product.description, SUM(product.quantity) " +
            " from product  GROUP BY product.description", nativeQuery = true)
    List<Object[]> countBySubCate();
}
