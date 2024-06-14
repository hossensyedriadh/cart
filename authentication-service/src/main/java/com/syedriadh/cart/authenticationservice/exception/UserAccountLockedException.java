package com.syedriadh.cart.authenticationservice.exception;

import org.springframework.security.core.AuthenticationException;

import java.io.Serial;

@SuppressWarnings("unused")
public class UserAccountLockedException extends AuthenticationException {
    @Serial
    private static final long serialVersionUID = -3280911998545195131L;

    public UserAccountLockedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UserAccountLockedException(String msg) {
        super(msg);
    }
}
