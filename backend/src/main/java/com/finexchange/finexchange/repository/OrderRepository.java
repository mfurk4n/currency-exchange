package com.finexchange.finexchange.repository;

import com.finexchange.finexchange.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByStatusAndOrderTypeOrderByCreatedAtAsc(String status, boolean orderType);

    List<Order> findByStatusAndCustomerIdOrderByCreatedAtAsc(
            String status,
            String customerId
    );
}
