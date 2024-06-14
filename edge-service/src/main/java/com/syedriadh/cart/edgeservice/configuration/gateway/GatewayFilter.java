package com.syedriadh.cart.edgeservice.configuration.gateway;

import com.syedriadh.cart.edgeservice.util.ContextHeadersHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@Component
public class GatewayFilter implements WebFilter {
    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        response.getHeaders().addIfAbsent(HttpHeaders.CONTENT_LANGUAGE, Locale.ENGLISH.getLanguage());
        response.getHeaders().addIfAbsent(HttpHeaders.DATE, String.valueOf(Date.from(Instant.now())));

        String id = UUID.randomUUID().toString();

        ContextHeadersHolder contextHeadersHolder = ContextHeadersHolder.getInstance();
        contextHeadersHolder.putHeader(HttpHeaders.USER_AGENT, request.getHeaders().getFirst(HttpHeaders.USER_AGENT));

        exchange.getAttributes().put("X-Ray-ID", id);
        request.mutate().header("X-Ray-ID", id).build();
        response.getHeaders().addIfAbsent("X-Ray-ID", id);

        return chain.filter(exchange);
    }
}
