package com.ptit.edu.store.customer.dao;

import com.ptit.edu.store.customer.models.data.Customer;
import com.ptit.edu.store.customer.models.view.CustomerView;
import com.ptit.edu.store.customer.models.view.HeaderProfile;
import com.ptit.edu.store.customer.models.view.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface CustomerRespository extends JpaRepository<Customer,String> {
    Customer findByUser_Id(String userID);
    @Query("select new com.ptit.edu.store.customer.models.view.HeaderProfile(" +
            "c.fullName," +
            "c.avatarUrl" +
            ") from Customer c where c.id = ?1")
    HeaderProfile getHeaderProfile(String id);

    @Query("select new com.ptit.edu.store.customer.models.view.Profile(c)" +
            "from Customer c where c.id = ?1")
    Profile getProfile(String customerID);

    @Query("select new com.ptit.edu.store.customer.models.view.CustomerView(c) " +
            " from Customer c ")
    Page<CustomerView> getListCustomer(Pageable pageable);
}
