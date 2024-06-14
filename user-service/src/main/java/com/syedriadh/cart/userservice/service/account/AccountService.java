package com.syedriadh.cart.userservice.service.account;

import com.syedriadh.cart.userservice.entity.User;
import com.syedriadh.cart.userservice.entity.UserProfile;

public interface AccountService {
    User fetch();

    User update(UserProfile UserProfile);
}
