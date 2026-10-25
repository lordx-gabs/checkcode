package com.edu.checkcode.dto;

import com.edu.checkcode.model.Address;

public record AddressResponse(
        String street,
        String number,
        String complement,
        String zipCode
) {
    public static AddressResponse from(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressResponse(
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getZipCode()
        );
    }
}
