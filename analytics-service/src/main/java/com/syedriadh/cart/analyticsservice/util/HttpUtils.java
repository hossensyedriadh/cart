package com.syedriadh.cart.analyticsservice.util;

import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.http.HttpHeaders;

@SuppressWarnings("unused")
public class HttpUtils {
    private final HttpServletRequest httpServletRequest;
    private final UserAgent userAgent;

    public HttpUtils(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
        this.userAgent = UserAgent.parseUserAgentString(httpServletRequest.getHeader(HttpHeaders.USER_AGENT));
    }

    public String parseClientAddress() {
        String[] headers = {
                "X-FORWARDED-FOR",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : headers) {
            String value = httpServletRequest.getHeader(header);

            if (value == null || value.isEmpty()) {
                continue;
            }

            String[] parts = value.split("\\s*,\\s*");
            return parts[0];
        }

        return httpServletRequest.getRemoteAddr();
    }

    public OperatingSystem getOperatingSystemInfo() {
        if (this.userAgent.getOperatingSystem() != null) {
            return this.userAgent.getOperatingSystem();
        }
        return null;
    }

    public BrowserInfo getBrowserInfo() {
        return new BrowserInfo();
    }

    @Getter
    public class BrowserInfo {
        private final String name;
        private final String type;
        private final String version;
        private final String manufacturer;
        private final String renderingEngine;

        public BrowserInfo() {
            this.name = userAgent.getBrowser() != null ? userAgent.getBrowser().getName() : null;
            this.type = userAgent.getBrowser() != null ? userAgent.getBrowser().getBrowserType().getName() : null;
            this.version = userAgent.getBrowserVersion() != null ? userAgent.getBrowserVersion().getVersion() : null;
            this.manufacturer = userAgent.getBrowser() != null ? userAgent.getBrowser().getManufacturer().getName() : null;
            this.renderingEngine = userAgent.getBrowser() != null ? userAgent.getBrowser().getRenderingEngine().getName() : null;
        }
    }
}
