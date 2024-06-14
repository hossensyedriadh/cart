package com.syedriadh.cart.edgeservice.filter;

import com.syedriadh.cart.edgeservice.util.ContextHeadersHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

public class UserAgentExchangeFilter implements ExchangeFilterFunction {
    @Override
    @NonNull
    public Mono<ClientResponse> filter(@NonNull ClientRequest request, @NonNull ExchangeFunction next) {
        ContextHeadersHolder contextHeadersHolder = ContextHeadersHolder.getInstance();

        ClientRequest clientRequest = ClientRequest.from(request)
                .header(HttpHeaders.USER_AGENT, contextHeadersHolder.getHeader(HttpHeaders.USER_AGENT))
                .build();

        return next.exchange(clientRequest);
    }
}
