package com.finexchange.finexchange.dto.request;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExchangeOrderRequest {
    @NotBlank
    String customerId;

    @NotBlank
    String baseCurrency;

    @NotBlank
    String targetCurrency;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Miktar 0'dan büyük olmalıdır")
    @Digits(integer = 9, fraction = 2, message = "Miktar en fazla 2 ondalık basamak içerebilir")
    BigDecimal amount;

    //emir tipi limit ise yani orderType false ise burası beklenen fiyat olarak kullanılır
    //alınan fiyat
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Miktar 0'dan büyük olmalıdır")
    BigDecimal expectedPrice;

    //true -> piyase false -> limit
    @NotNull
    @Min(value = 0, message = "Transaction type en az 0 olmalıdır")
    @Max(value = 2, message = "Transaction type en fazla 3 olabilir")
    int transactionType;

    //true -> sat -> false al
    @NotNull
    boolean orderType;
}
