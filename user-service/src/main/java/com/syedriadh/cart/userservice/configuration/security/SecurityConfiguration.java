package com.syedriadh.cart.userservice.configuration.security;

import com.syedriadh.cart.userservice.configuration.authentication.entrypoint.BearerAuthenticationEntryPoint;
import com.syedriadh.cart.userservice.configuration.authentication.filter.BearerAuthenticationExceptionFilter;
import com.syedriadh.cart.userservice.configuration.authentication.filter.BearerAuthenticationFilter;
import com.syedriadh.cart.userservice.configuration.authentication.handler.ResourceAccessDeniedHandler;
import com.syedriadh.cart.userservice.configuration.authentication.service.BearerAuthenticationUserDetailsService;
import com.syedriadh.cart.userservice.filter.RequestInterceptFilter;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final BearerAuthenticationUserDetailsService bearerAuthenticationUserDetailsService;
    private final BearerAuthenticationFilter bearerAuthenticationFilter;
    private final BearerAuthenticationExceptionFilter bearerAuthenticationExceptionFilter;
    private final BearerAuthenticationEntryPoint bearerAuthenticationEntryPoint;
    private final ResourceAccessDeniedHandler resourceAccessDeniedHandler;
    private final RequestInterceptFilter requestInterceptFilter;

    @Autowired
    public SecurityConfiguration(BearerAuthenticationUserDetailsService bearerAuthenticationUserDetailsService, BearerAuthenticationFilter bearerAuthenticationFilter,
                                 BearerAuthenticationExceptionFilter bearerAuthenticationExceptionFilter, BearerAuthenticationEntryPoint bearerAuthenticationEntryPoint,
                                 ResourceAccessDeniedHandler resourceAccessDeniedHandler, RequestInterceptFilter requestInterceptFilter) {
        this.bearerAuthenticationUserDetailsService = bearerAuthenticationUserDetailsService;
        this.bearerAuthenticationFilter = bearerAuthenticationFilter;
        this.bearerAuthenticationExceptionFilter = bearerAuthenticationExceptionFilter;
        this.bearerAuthenticationEntryPoint = bearerAuthenticationEntryPoint;
        this.resourceAccessDeniedHandler = resourceAccessDeniedHandler;
        this.requestInterceptFilter = requestInterceptFilter;
    }

    @Bean
    @Profile("dev")
    public SecurityFilterChain securityFilterChain(@NonNull HttpSecurity httpSecurity, @NonNull HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

        httpSecurity.cors(AbstractHttpConfigurer::disable).csrf(CsrfConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(httpRequests -> httpRequests.requestMatchers(mvcMatcherBuilder.pattern("/actuator/**")).permitAll().anyRequest().authenticated())
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(this.bearerAuthenticationEntryPoint)
                        .accessDeniedHandler(this.resourceAccessDeniedHandler)).userDetailsService(this.bearerAuthenticationUserDetailsService);

        httpSecurity.addFilterBefore(this.bearerAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterBefore(this.bearerAuthenticationExceptionFilter, BearerAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public FilterRegistrationBean<RequestInterceptFilter> interceptFilterFilterRegistrationBean() {
        FilterRegistrationBean<RequestInterceptFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(this.requestInterceptFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setEnabled(true);

        return filterRegistrationBean;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.httpFirewall(this.firewall());
    }

    @Bean
    public Filter eTagFilter() {
        return new ShallowEtagHeaderFilter();
    }

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public HttpFirewall firewall() {
        return new StrictHttpFirewall();
    }
}
