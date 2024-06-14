package com.syedriadh.cart.userservice.repository;

import com.syedriadh.cart.userservice.entity.Address;
import com.syedriadh.cart.userservice.entity.UserAddress;
import com.syedriadh.cart.userservice.entity.UserAddressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, UserAddressId> {
    @Query(value = "select ua.address_ref from user_address ua where user_ref = ?;", nativeQuery = true)
    List<Address> findAllByUser(String username);

    @Query(value = "select ua.address_ref from user_address ua where ua.address_ref = :addressId and ua.user_ref = :username;", nativeQuery = true)
    Optional<Address> findAddress(Long addressId, String username);
}
