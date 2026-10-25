package com.edu.checkcode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank(message = "Street is required")
        @Size(max = 300, message = "Street must have at most 300 characters")
        String street,

        @NotBlank(message = "Number is required")
        @Size(max = 20, message = "Number must have at most 20 characters")
        String number,

        @Size(max = 100, message = "Complement must have at most 100 characters")
        String complement,

        @NotNull(message = "Zip code is required")
        @Size(max = 8, message = "Zip code must have at most 8 characters")
        String zipCode
) {
}
