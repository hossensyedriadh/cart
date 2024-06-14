package com.syedriadh.cart.authenticationservice.configuration.security;

import com.syedriadh.cart.authenticationservice.filter.RequestInterceptFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final RequestInterceptFilter requestInterceptFilter;

    @Autowired
    public SecurityConfiguration(RequestInterceptFilter requestInterceptFilter) {
        this.requestInterceptFilter = requestInterceptFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(@NonNull HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(httpRequests -> httpRequests.requestMatchers("/v1/authentication/**", "/v1/introspection/**",
                        "/actuator/info", "/actuator/health").permitAll().anyRequest().authenticated());

        return httpSecurity.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.httpFirewall(this.firewall());
    }

    @Bean
    public FilterRegistrationBean<RequestInterceptFilter> requestInterceptFilterRegistrationBean() {
        FilterRegistrationBean<RequestInterceptFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(this.requestInterceptFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setEnabled(true);

        return filterRegistrationBean;
    }

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public HttpFirewall firewall() {
        return new StrictHttpFirewall();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }
}
