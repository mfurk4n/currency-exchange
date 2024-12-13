package com.finexchange.finexchange.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyDto {

    String id;
    String code;
    String name;
    String symbol;

}
