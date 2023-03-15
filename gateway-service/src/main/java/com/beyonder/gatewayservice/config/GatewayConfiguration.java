package com.beyonder.gatewayservice.config;

import com.beyonder.gatewayservice.filter.AdminFilter;
import com.beyonder.gatewayservice.filter.AppFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
@RequiredArgsConstructor
public class GatewayConfiguration {
    private final AppFilter filter;
    private final AdminFilter adminFilter;

    private static final String UserServiceUrl = "http://user-service:8082/";
    private static final String AuthServiceUrl = "http://auth-service:8086/";
    private static final String ProductServiceUrl = "http://product-service:8084/";
    @Bean
    RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/api/v1/user")
                        .filters(f -> f.filter(filter).filter(adminFilter))
                        .uri(UserServiceUrl))
                .route(r -> r.method(HttpMethod.POST)
                        .and()
                        .path("/api/v1/user")
                        .filters(f -> f.filter(filter).filter(adminFilter))
                        .uri(ProductServiceUrl))
                .route(r -> r.path("/api/v1/auth/**")
                        .uri(AuthServiceUrl))
                .build();
    }
}
