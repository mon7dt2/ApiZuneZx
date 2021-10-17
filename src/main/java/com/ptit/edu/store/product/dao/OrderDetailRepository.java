package com.ptit.edu.store.product.dao;

import com.ptit.edu.store.product.models.data.OrderDetail;
import com.ptit.edu.store.product.models.view.OrderDetailView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {

    @Query("select new com.ptit.edu.store.product.models.view.OrderDetailView(o) from OrderDetail o where o.order.id = ?1")
    Page<OrderDetailView> getDetail(String id, Pageable pageable);

    @Query("select o.product.id, o.quantity from OrderDetail o where o.order.id = ?1")
    List<Object[]> getCancelDetail(String id);
}
