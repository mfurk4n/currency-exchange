package com.finexchange.finexchange.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreateWalletRequest {
    @NotBlank
    String customerId;
    @NotBlank
    String currencyId;
}
