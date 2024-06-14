package com.syedriadh.cart.authenticationservice.repository.user;

import com.syedriadh.cart.authenticationservice.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}