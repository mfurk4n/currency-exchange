package com.finexchange.finexchange.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreateCustomerRequest {

    @NotBlank(message = "Boş bırakılamaz")
    @Size(min = 2, message = "Minimum 2 karakterli olmalıdır")
    String name;

    private String nationalId;

    private String taxId;

    @JsonProperty("legal")
    boolean isLegal;

}
