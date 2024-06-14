package com.syedriadh.cart.edgeservice.configuration.gateway;

import com.syedriadh.cart.edgeservice.exception.ResourceException;
import com.syedriadh.cart.edgeservice.model.TokenValidationRequest;
import com.syedriadh.cart.edgeservice.model.TokenValidationResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class BearerAuthenticationFilter extends AbstractGatewayFilterFactory<BearerAuthenticationFilter.Config> {
    private final String accessTokenPrefix = "Bearer ";

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public BearerAuthenticationFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest httpRequest = exchange.getRequest();

            if (httpRequest.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                String authorization = Objects.requireNonNull(httpRequest.getHeaders().get(HttpHeaders.AUTHORIZATION)).getFirst();

                if (authorization.startsWith(accessTokenPrefix)) {
                    String accessToken = authorization.substring(accessTokenPrefix.length());

                    return this.webClientBuilder.build().post().uri("http://authentication-service/authentication-api/v1/introspection/introspect")
                            .body(Mono.just(new TokenValidationRequest(accessToken)), TokenValidationRequest.class).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(TokenValidationResponse.class)
                            .onErrorResume(WebClientResponseException.BadRequest.class, e -> Mono.error(new ResourceException(e.getMessage(), HttpStatus.BAD_REQUEST)))
                            .onErrorResume(WebClientResponseException.BadGateway.class, e -> Mono.error(new ResourceException(e.getMessage(), HttpStatus.BAD_GATEWAY)))
                            .onErrorResume(WebClientResponseException.GatewayTimeout.class, e -> Mono.error(new ResourceException(e.getMessage(), HttpStatus.GATEWAY_TIMEOUT)))
                            .onErrorResume(WebClientResponseException.ServiceUnavailable.class, e -> Mono.error(new ResourceException("Unable to find instance for authentication-service", HttpStatus.SERVICE_UNAVAILABLE)))
                            .onErrorResume(WebClientResponseException.InternalServerError.class, e -> Mono.error(new ResourceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR)))
                            .onErrorResume(WebClientResponseException.Unauthorized.class, e -> Mono.error(new ResourceException(e.getMessage(), HttpStatus.UNAUTHORIZED)))
                            .flatMap(response -> {
                                if (response.getIsValid()) {
                                    return chain.filter(exchange);
                                } else {
                                    return Mono.error(new ResourceException("Invalid/Expired access token", HttpStatus.UNAUTHORIZED, httpRequest));
                                }
                            });
                } else {
                    return Mono.error(new ResourceException("Access token must be prepended with access token type, i.e.: 'Bearer '", HttpStatus.BAD_REQUEST, httpRequest));
                }
            } else {
                return Mono.error(new ResourceException("Missing access token", HttpStatus.UNAUTHORIZED, httpRequest));
            }
        };
    }

    @Getter
    @Setter
    public static class Config {
        private String name;
    }
}
