package com.syedriadh.cart.userservice.service.address;

import com.syedriadh.cart.userservice.entity.Address;

import java.util.List;
import java.util.Optional;

public interface AddressService {
    List<Address> fetchAddresses();

    Optional<Address> fetchAddress(Long id);

    Address addAddress(Address address);

    Address updateAddress(Address address);
}
