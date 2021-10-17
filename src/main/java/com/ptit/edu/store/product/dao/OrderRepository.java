package com.ptit.edu.store.product.dao;

import com.ptit.edu.store.product.models.data.Order;
import com.ptit.edu.store.product.models.view.OrderPreview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("select new com.ptit.edu.store.product.models.view.OrderPreview(o)" +
            " from Order o ")
    Page<OrderPreview> getAllOrderPreview(Pageable pageable);

    @Query("select new com.ptit.edu.store.product.models.view.OrderPreview(o)" +
            " from Order o where o.customer.id = ?1")
    Page<OrderPreview> getCustomerOrder(String customerid, Pageable pageable);

    long countByIsCheck(int isCheck);

    @Query(value = "select sum(o.total) from orders as o where o.isCheck = 0",nativeQuery = true)
    Double totalIncome();

    @Query("select new com.ptit.edu.store.product.models.view.OrderPreview(o)" +
            " from Order o where o.isCheck = 1")
    Page<OrderPreview> getOrderChecked(Pageable pageable);

    @Query("select new com.ptit.edu.store.product.models.view.OrderPreview(o)" +
            " from Order o where o.isCheck = 0")
    Page<OrderPreview> getOrderUnchecked(Pageable pageable);

    @Query("select new com.ptit.edu.store.product.models.view.OrderPreview(o)" +
            " from Order o where o.isCheck = -1")
    Page<OrderPreview> getOrderCanceled(Pageable pageable);
}
