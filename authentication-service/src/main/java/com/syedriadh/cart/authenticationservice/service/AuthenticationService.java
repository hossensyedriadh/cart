package com.syedriadh.cart.authenticationservice.service;

import com.syedriadh.cart.authenticationservice.model.AccessTokenRenewalRequest;
import com.syedriadh.cart.authenticationservice.model.BearerTokenRequest;
import com.syedriadh.cart.authenticationservice.model.BearerTokenResponse;
import com.syedriadh.cart.authenticationservice.model.LogoutRequest;

public interface AuthenticationService {
    BearerTokenResponse authenticate(BearerTokenRequest bearerTokenRequest);

    BearerTokenResponse renewAuthentication(AccessTokenRenewalRequest tokenRenewalRequest);

    void logout(LogoutRequest logoutRequest);
}
