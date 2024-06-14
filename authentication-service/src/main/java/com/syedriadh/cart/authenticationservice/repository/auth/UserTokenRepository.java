package com.syedriadh.cart.authenticationservice.repository.auth;

import com.syedriadh.cart.authenticationservice.entity.auth.UserToken;
import com.syedriadh.cart.authenticationservice.enumerator.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, String> {
    @Modifying
    @Query(value = "delete from user_token ut where ut.expires_on < (unix_timestamp(now()) * 1000);", nativeQuery = true)
    void deleteExpiredTokens();

    Optional<UserToken> findByForUserAndIpAddressAndUserAgentAndTokenType(String forUser, String ipAddress, String userAgent, TokenType tokenType);

    Optional<UserToken> findByTokenTypeAndToken(TokenType tokenType, String token);
}