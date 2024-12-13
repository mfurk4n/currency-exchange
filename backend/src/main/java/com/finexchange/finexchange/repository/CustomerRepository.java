package com.finexchange.finexchange.repository;

import com.finexchange.finexchange.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    List<Customer> findByUserId(String userId);
    boolean existsByNationalIdOrTaxId(String nationalId, String taxId);
}
