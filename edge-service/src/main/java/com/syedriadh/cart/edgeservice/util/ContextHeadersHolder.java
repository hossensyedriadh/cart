package com.syedriadh.cart.edgeservice.util;

import java.util.HashMap;

public class ContextHeadersHolder {
    private static ContextHeadersHolder instance;
    private static final HashMap<String, String> headers = new HashMap<>();

    private ContextHeadersHolder() {
    }

    public static ContextHeadersHolder getInstance() {
        if (instance == null) {
            instance = new ContextHeadersHolder();
        }

        return instance;
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    public void putHeader(String header, String value) {
        headers.put(header, value);
    }
}
