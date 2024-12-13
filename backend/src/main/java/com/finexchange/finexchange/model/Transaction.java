package com.finexchange.finexchange.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue
    @UuidGenerator
    @EqualsAndHashCode.Include
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_from_id", nullable = false)
    Currency currencyFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_to_id", nullable = false)
    Currency currencyTo;

    @Column(nullable = false, precision = 14, scale = 2)
    BigDecimal amountFrom;

    @Column(nullable = false, precision = 14, scale = 2)
    BigDecimal amountTo;

    @Column(nullable = false, precision = 14, scale = 5)
    BigDecimal currencyPrice;

    @Column(nullable = false)
    String transactionType;

    //WAIT, SUCCESS, CANCEL
    @Column(nullable = false)
    String status;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    LocalDateTime createdAt;
}
