package com.finexchange.finexchange.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "balances")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Balance {

    @Id
    @GeneratedValue
    @UuidGenerator
    @EqualsAndHashCode.Include
    String id;

    @Column(name = "updated_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    LocalDateTime updatedAt;

    @Column(nullable = false, precision = 14, scale = 2)
    BigDecimal amount;

    @Column(nullable = false, precision = 14, scale = 2)
    BigDecimal loadedAmount;

    @Column(nullable = false, precision = 14, scale = 2)
    BigDecimal previousDayAmount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    Wallet wallet;


}
