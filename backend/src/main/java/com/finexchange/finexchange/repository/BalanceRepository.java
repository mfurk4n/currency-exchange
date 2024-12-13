package com.finexchange.finexchange.repository;

import com.finexchange.finexchange.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceRepository  extends JpaRepository<Balance, String> {
}
