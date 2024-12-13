package com.finexchange.finexchange.repository;

import com.finexchange.finexchange.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, String> {
    Optional<ExchangeRate> findTopByBaseCurrencyIdAndTargetCurrencyIdOrderByCreatedAtDesc(String baseCurrencyId, String targetCurrencyId);

    @Query("SELECT er FROM ExchangeRate er WHERE (er.baseCurrency.id, er.targetCurrency.id, er.createdAt) IN (" +
            "SELECT er2.baseCurrency.id, er2.targetCurrency.id, MAX(er2.createdAt) " +
            "FROM ExchangeRate er2 GROUP BY er2.baseCurrency.id, er2.targetCurrency.id) " +
            "ORDER BY er.createdAt ASC")
    List<ExchangeRate> findLatestExchangeRates();

    @Query("SELECT er FROM ExchangeRate er WHERE er.dataDate = :dataDate AND (er.baseCurrency.id, er.targetCurrency.id, er.createdAt) IN (" +
            "SELECT er2.baseCurrency.id, er2.targetCurrency.id, MAX(er2.createdAt) " +
            "FROM ExchangeRate er2 WHERE er2.dataDate = :dataDate GROUP BY er2.baseCurrency.id, er2.targetCurrency.id) " +
            "ORDER BY er.createdAt ASC")
    List<ExchangeRate> findLatestExchangeRatesByDataDate(@Param("dataDate") String dataDate);


    @Query("SELECT e FROM ExchangeRate e WHERE e.dataDate IN :dataDates AND e.targetCurrency.id = :targetCurrencyId ORDER BY e.baseCurrency.code")
    List<ExchangeRate> getWeeklyExchangeRatesAsPartBaseTargetCurrency(@Param("dataDates") List<String> dataDates,
                                                                      @Param("targetCurrencyId") String targetCurrencyId);

    @Query("SELECT e FROM ExchangeRate e WHERE e.dataDate IN :dataDates AND e.baseCurrency.id = :baseCurrencyId ORDER BY e.targetCurrency.code")
    List<ExchangeRate> getWeeklyExchangeRatesAsPartBaseBaseCurrency(@Param("dataDates") List<String> dataDates,
                                                                    @Param("baseCurrencyId") String baseCurrencyId);

    boolean existsByBaseCurrencyIdAndTargetCurrencyIdAndDataDate(
            String baseCurrencyId, String targetCurrencyId, String dataDate);


}
