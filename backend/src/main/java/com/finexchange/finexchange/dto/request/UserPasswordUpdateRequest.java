package com.finexchange.finexchange.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserPasswordUpdateRequest {

    @NotBlank(message = "Boş bırakılamaz")
    @Size(min = 6, message = "Minimum 6 karakterli olmalıdır")
    private String currentPassword;

    @NotBlank(message = "Boş bırakılamaz")
    @Size(min = 6, message = "Minimum 6 karakterli olmalıdır")
    private String newPassword;
}
