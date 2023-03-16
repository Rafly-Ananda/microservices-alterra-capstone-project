package com.beyonder.gatewayservice.filter;

import com.beyonder.gatewayservice.dto.GlobalResponse;
import com.beyonder.gatewayservice.dto.UserDTO;
import com.beyonder.gatewayservice.exception.GlobalExceptionHandler;
import com.beyonder.gatewayservice.exception.UnauthorizedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppFilter implements GatewayFilter {
    private final RestTemplate restTemplate;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        final List<String> unsecuredEndpoints = List.of("/api/v1/auth/register", "/api/v1/auth/login");

        if (!unsecuredEndpoints.contains(request.getURI().getPath()) && !request.getHeaders().containsKey("Authorization")) {
            log.info(String.valueOf(exchange));
            throw new UnauthorizedException();
        }

        if (!unsecuredEndpoints.contains(request.getURI().getPath())) {
            final String token = request.getHeaders().getOrEmpty("Authorization").get(0);
            String newToken = token.split(" ")[1];
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(newToken);

            try {
                HttpEntity<String> entity = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.exchange("http://auth-service:8086/api/v1/auth/detail-user", HttpMethod.GET, entity, String.class);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String role = jsonNode.get("data").get(0).get("role").asText();
                Integer userId = jsonNode.get("data").get(0).get("user_id").asInt();
                exchange.getRequest().mutate().header("role", String.valueOf(role)).build();
                exchange.getRequest().mutate().header("id", String.valueOf(userId)).build();
            } catch (Exception e) {
                throw new UnauthorizedException();
            }
        }

        return chain.filter(exchange);
    }
}
