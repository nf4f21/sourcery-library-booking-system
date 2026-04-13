package com.example.demo.formatter;

import com.example.demo.model.AddressEntity;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class AddressFormatter {
    @Named("formatAddress")
    public String format(AddressEntity address) {
        return String.format("%s %s %s, %s %s, %s",
                address.getStreet(),
                address.getBuildingNumber(),
                address.getApartmentNumber() != null ? address.getApartmentNumber() : "",
                address.getZipCode(),
                address.getCity(),
                address.getCountry()
        ).replace(" ,", ",");
    }
}
