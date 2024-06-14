package com.syedriadh.cart.userservice.controller.v1;

import com.syedriadh.cart.userservice.entity.Address;
import com.syedriadh.cart.userservice.service.address.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/v1/user-addresses", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AddressController {
    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/")
    public ResponseEntity<?> fetchAddresses() {
        List<Address> addresses = this.addressService.fetchAddresses();

        return addresses.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> fetchAddress(@PathVariable("id") Long id) {
        Optional<Address> address = this.addressService.fetchAddress(id);

        return address.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(address, HttpStatus.OK);
    }

    @PostMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> saveAddress(@Validated @RequestBody Address address) {
        Address savedAddress = this.addressService.addAddress(address);

        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }

    @PutMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateAddress(@Validated @RequestBody Address address) {
        Address updatedAddress = this.addressService.updateAddress(address);

        return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
    }
}
