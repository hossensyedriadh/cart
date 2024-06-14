package com.syedriadh.cart.userservice.service.address;

import com.syedriadh.cart.userservice.entity.Address;
import com.syedriadh.cart.userservice.entity.UserAddress;
import com.syedriadh.cart.userservice.exception.ResourceException;
import com.syedriadh.cart.userservice.repository.AddressRepository;
import com.syedriadh.cart.userservice.repository.UserAddressRepository;
import com.syedriadh.cart.userservice.util.CurrentAuthenticatedPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserAddressRepository userAddressRepository;
    private final CurrentAuthenticatedPrincipal authenticatedPrincipal;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository, UserAddressRepository userAddressRepository,
                              CurrentAuthenticatedPrincipal authenticatedPrincipal, HttpServletRequest httpServletRequest) {
        this.addressRepository = addressRepository;
        this.userAddressRepository = userAddressRepository;
        this.authenticatedPrincipal = authenticatedPrincipal;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public List<Address> fetchAddresses() {
        return this.userAddressRepository.findAllByUser(this.authenticatedPrincipal.getPrincipal().getUsername());
    }

    @Override
    public Optional<Address> fetchAddress(Long id) {
        return this.userAddressRepository.findAddress(id,
                this.authenticatedPrincipal.getPrincipal().getUsername());
    }

    @Override
    public Address addAddress(Address address) {
        UserAddress userAddress = new UserAddress();
        userAddress.setAddress(address);
        userAddress.setUser(this.authenticatedPrincipal.getPrincipal());

        return this.userAddressRepository.saveAndFlush(userAddress).getAddress();
    }

    @Override
    public Address updateAddress(Address address) {
        Optional<Address> addressOptional = this.userAddressRepository.findAddress(address.getId(),
                this.authenticatedPrincipal.getPrincipal().getUsername());

        if (addressOptional.isEmpty()) {
            throw new ResourceException("Address with ID: " + address.getId() + " does not exist for user",
                    HttpStatus.BAD_REQUEST, this.httpServletRequest);
        }

        return this.addressRepository.saveAndFlush(address);
    }
}
