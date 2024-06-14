package com.syedriadh.cart.userservice.service.account;

import com.syedriadh.cart.userservice.entity.User;
import com.syedriadh.cart.userservice.entity.UserProfile;
import com.syedriadh.cart.userservice.exception.ResourceException;
import com.syedriadh.cart.userservice.repository.UserAddressRepository;
import com.syedriadh.cart.userservice.repository.UserProfileRepository;
import com.syedriadh.cart.userservice.util.CurrentAuthenticatedPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AccountServiceImpl implements AccountService {
    private final UserProfileRepository userProfileRepository;
    private final CurrentAuthenticatedPrincipal authenticatedPrincipal;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public AccountServiceImpl(UserProfileRepository userProfileRepository, UserAddressRepository userAddressRepository,
                              CurrentAuthenticatedPrincipal authenticatedPrincipal, HttpServletRequest httpServletRequest) {
        this.userProfileRepository = userProfileRepository;
        this.authenticatedPrincipal = authenticatedPrincipal;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public User fetch() {
        return this.authenticatedPrincipal.getPrincipal();
    }

    @Override
    public User update(UserProfile userProfile) {
        User authenticatedPrincipal = this.authenticatedPrincipal.getPrincipal();

        if (authenticatedPrincipal.getProfile().getId().equals(userProfile.getId())) {
            this.userProfileRepository.saveAndFlush(userProfile);
        }

        throw new ResourceException("Failed to update profile because of conflicting profile IDs", HttpStatus.CONFLICT, this.httpServletRequest);
    }
}
