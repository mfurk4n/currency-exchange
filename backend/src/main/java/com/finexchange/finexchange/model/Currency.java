package com.finexchange.finexchange.model;

import com.finexchange.finexchange.constant.CurrencyConstants;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "currencies")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Currency {
    @Id
    @GeneratedValue
    @UuidGenerator
    @EqualsAndHashCode.Include
    String id;

    @Column(nullable = false, unique = true)
    String code;

    @Column(nullable = false, unique = true)
    String name;

    String symbol;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    LocalDateTime createdAt;

    public String getSymbol() {
        int index = CurrencyConstants.currencyCodes.indexOf(this.code);
        if (index != -1) {
            return CurrencyConstants.currencySymbols.get(index);
        } else {
            return this.symbol;
        }
    }
}
