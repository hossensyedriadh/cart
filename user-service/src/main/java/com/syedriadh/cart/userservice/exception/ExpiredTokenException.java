package com.syedriadh.cart.userservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

import java.io.Serial;

@SuppressWarnings("unused")
@Getter
public class ExpiredTokenException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4693181048020810173L;

    private HttpServletRequest httpServletRequest;

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public ExpiredTokenException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @since 1.4
     */
    public ExpiredTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message            the detail message. The detail message is saved for
     *                           later retrieval by the {@link #getMessage()} method.
     * @param httpServletRequest request that caused the exception
     */
    public ExpiredTokenException(String message, HttpServletRequest httpServletRequest) {
        super(message);
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message            the detail message (which is saved for later retrieval
     *                           by the {@link #getMessage()} method).
     * @param cause              the cause (which is saved for later retrieval by the
     *                           {@link #getCause()} method).  (A {@code null} value is
     *                           permitted, and indicates that the cause is nonexistent or
     *                           unknown.)
     * @param httpServletRequest request that caused the exception
     * @since 1.4
     */
    public ExpiredTokenException(String message, Throwable cause, HttpServletRequest httpServletRequest) {
        super(message, cause);
        this.httpServletRequest = httpServletRequest;
    }
}
