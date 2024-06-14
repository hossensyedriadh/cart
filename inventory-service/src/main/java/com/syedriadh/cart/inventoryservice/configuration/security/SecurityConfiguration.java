package com.syedriadh.cart.inventoryservice.configuration.security;

import com.syedriadh.cart.inventoryservice.configuration.handler.ResourceAccessDeniedHandler;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.header.writers.*;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final ResourceAccessDeniedHandler accessDeniedHandler;

    @Autowired
    public SecurityConfiguration(ResourceAccessDeniedHandler accessDeniedHandler) {
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Profile("dev")
    @Bean
    public SecurityFilterChain devSecurityFilterChain(HttpSecurity httpSecurity, HandlerMappingIntrospector mappingIntrospector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(mappingIntrospector);

        httpSecurity.cors(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling.accessDeniedHandler(this.accessDeniedHandler))
                .authorizeHttpRequests(httpRequests -> httpRequests.requestMatchers(mvcMatcherBuilder.pattern("/actuator/**")).permitAll().anyRequest().authenticated());

        return httpSecurity.build();
    }

    @Profile("prod")
    @Bean
    public SecurityFilterChain prodSecurityFilterChain(HttpSecurity httpSecurity, HandlerMappingIntrospector mappingIntrospector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(mappingIntrospector);

        httpSecurity.cors(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling.accessDeniedHandler(this.accessDeniedHandler))
                .authorizeHttpRequests(httpRequests -> httpRequests.requestMatchers(mvcMatcherBuilder.pattern("/actuator/**")).permitAll().anyRequest().authenticated());

        httpSecurity.headers(headersConfigurer -> {
            headersConfigurer.referrerPolicy(referrerPolicyConfig -> referrerPolicyConfig.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN));
            headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny);
            headersConfigurer.crossOriginOpenerPolicy(crossOriginOpenerPolicyConfig -> crossOriginOpenerPolicyConfig.policy(CrossOriginOpenerPolicyHeaderWriter.CrossOriginOpenerPolicy.SAME_ORIGIN));
            headersConfigurer.crossOriginEmbedderPolicy(crossOriginEmbedderPolicyConfig -> crossOriginEmbedderPolicyConfig.policy(CrossOriginEmbedderPolicyHeaderWriter.CrossOriginEmbedderPolicy.REQUIRE_CORP));
            headersConfigurer.crossOriginResourcePolicy(crossOriginResourcePolicyConfig -> crossOriginResourcePolicyConfig.policy(CrossOriginResourcePolicyHeaderWriter.CrossOriginResourcePolicy.SAME_ORIGIN));
            headersConfigurer.xssProtection(xXssConfig -> xXssConfig.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK));
//            headersConfigurer.httpStrictTransportSecurity(hstsConfig -> hstsConfig.includeSubDomains(true).maxAgeInSeconds(31536000));
        });

        return httpSecurity.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.httpFirewall(this.httpFirewall());
    }

    @Bean
    public HttpFirewall httpFirewall() {
        return new StrictHttpFirewall();
    }

    @Bean
    public Filter eTagFilter() {
        return new ShallowEtagHeaderFilter();
    }
}
