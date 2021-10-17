package com.ptit.edu.store.product.dao;

import com.ptit.edu.store.customer.models.data.CustomerOrder;
import com.ptit.edu.store.customer.models.view.CustomerOrderPreview;
import com.ptit.edu.store.product.models.view.OrderPreview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder,String> {
    @Query("select new com.ptit.edu.store.customer.models.view.CustomerOrderPreview(o)" +
            " from CustomerOrder o " +
            "where o.customer.id = ?1")
    Page<CustomerOrderPreview> getAllOrderPreview(String customerID, Pageable pageable);



}
