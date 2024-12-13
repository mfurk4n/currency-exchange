package com.finexchange.finexchange.repository;

import com.finexchange.finexchange.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, String> {
    List<Wallet> findByCustomerIdOrderByCreatedAtAsc(String customerId);


    Optional<Wallet> findByCustomerIdAndCurrencyId(String customerId, String currencyId);

    @Query("SELECT w FROM Wallet w WHERE w.customer.id = :customerId ORDER BY w.createdAt ASC")
    List<Wallet> findAllByCustomerId(@Param("customerId") String customerId);

}
