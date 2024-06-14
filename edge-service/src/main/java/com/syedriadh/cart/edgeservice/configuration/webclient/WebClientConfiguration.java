package com.syedriadh.cart.edgeservice.configuration.webclient;

import com.syedriadh.cart.edgeservice.filter.UserAgentExchangeFilter;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@LoadBalancerClient(name = "authentication-service", configuration = LoadBalancerClientConfiguration.class)
public class WebClientConfiguration {
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder().filters(filterFunctions -> {
            filterFunctions.add(new UserAgentExchangeFilter());
        });
    }
}
