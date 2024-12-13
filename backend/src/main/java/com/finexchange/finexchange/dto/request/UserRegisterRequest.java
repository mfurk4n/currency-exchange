package com.finexchange.finexchange.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRegisterRequest {
    @NotBlank(message = "Boş bırakılamaz")
    @Email(message = "Geçerli bir email adresi olmalıdır")
    String email;
    @NotBlank(message = "Boş bırakılamaz")
    @Size(min = 2, message = "Minimum 2 karakterli olmalıdır")
    String name;
}
