package com.finexchange.finexchange.repository;

import com.finexchange.finexchange.model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    Optional<Transaction> findById(String id);
    @Query("SELECT t FROM Transaction t WHERE t.customer.user.id = :userId ")
    List<Transaction> findByUserId(@Param("userId")String userId);
    List<Transaction> findByCustomerId(String customerId);
    List<Transaction> findByStatus(String status);
    @Query("SELECT t FROM Transaction t WHERE t.customer.user.id = :userId AND t.status = :status")
    List<Transaction> findByUserIdAndStatus(@Param("userId") String userId, String status);
    List<Transaction> findByCustomerIdAndStatus(String customerId, String status);

}

