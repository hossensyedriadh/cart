package com.syedriadh.cart.edgeservice.configuration.security;

import com.syedriadh.cart.edgeservice.configuration.gateway.GatewayFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.cloud.gateway.filter.headers.ForwardedHeadersFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;
import org.springframework.security.web.server.header.XXssProtectionServerHttpHeadersWriter;
import org.springframework.web.server.WebFilter;

import java.time.format.DateTimeFormatter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {
    private final GatewayFilter gatewayFilter;

    @Autowired
    public SecurityConfiguration(GatewayFilter gatewayFilter) {
        this.gatewayFilter = gatewayFilter;
    }

    @Bean
    @Profile("dev")
    public SecurityWebFilterChain devSecurityWebFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity.cors(ServerHttpSecurity.CorsSpec::disable).csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll());

        httpSecurity.headers(headerSpec -> headerSpec.referrerPolicy(referrerPolicySpec -> referrerPolicySpec
                        .policy(ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                .xssProtection(xssProtectionSpec -> xssProtectionSpec.headerValue(XXssProtectionServerHttpHeadersWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .frameOptions(frameOptionsSpec -> frameOptionsSpec.mode(XFrameOptionsServerHttpHeadersWriter.Mode.DENY)));

        return httpSecurity.build();
    }

    @Bean
    public WebFilter gatewayFilterBean() {
        return this.gatewayFilter;
    }

    @Bean
    public ForwardedHeadersFilter forwardedHeaderFilter() {
        return new ForwardedHeadersFilter();
    }

    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }

    @Bean
    public FormattingConversionService formattingConversionService() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService(false);

        DateTimeFormatterRegistrar dateTimeFormatterRegistrar = new DateTimeFormatterRegistrar();
        dateTimeFormatterRegistrar.setDateFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        dateTimeFormatterRegistrar.registerFormatters(conversionService);

        DateFormatterRegistrar dateFormatterRegistrar = new DateFormatterRegistrar();
        dateFormatterRegistrar.setFormatter(new DateFormatter("yyyy-MM-dd"));
        dateFormatterRegistrar.registerFormatters(conversionService);

        return conversionService;
    }
}

