package com.finexchange.finexchange.repository;

import com.finexchange.finexchange.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, String> {
    Optional<Currency> findByCode(String code);

    @Query("SELECT c FROM Currency c ORDER BY c.createdAt ASC")
    List<Currency> findAllOrderByCreatedAtAsc();
}
