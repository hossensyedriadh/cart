package com.syedriadh.cart.userservice.enumerator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    OTHERS("Others"),
    UNDISCLOSED("Rather not say");

    private final String value;
}
