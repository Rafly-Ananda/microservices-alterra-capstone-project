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

    private static final String USER_SERVICE_URL = "http://user-service:8082/";
    private static final String AUTH_SERVICE_URL = "http://auth-service:8086/";
    private static final String CATEGORY_SERVICE_URL = "http://category-service:8083/";
    private static final String PRODUCT_SERVICE_URL = "http://product-service:8084/";
    private static final String ORDER_SERVICE_URL = "http://order-service:8085/";

    @Bean
    RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                // AUTH_SERVICE_URL
                .route(r -> r.path("/api/v1/auth/**")
                        .uri(AUTH_SERVICE_URL))
                // USER_SERVICE_URL
                .route(r -> r.path("/api/v1/user")
                        .and().method( "GET")
                        .filters(f -> f.filter(filter).filter(adminFilter))
                        .uri(USER_SERVICE_URL))
                .route(r -> r.path("/api/v1/user/{id}")
                        .and().method( "GET")
                        .filters(f -> f.filter(filter))
                        .uri(USER_SERVICE_URL))
                .route(r -> r.path("/api/v1/user/**")
                        .and().method( "POST")
                        .filters(f -> f.filter(filter))
                        .uri(USER_SERVICE_URL))
                .route(r -> r.path("/api/v1/user/**")
                        .and().method( "PUT","DELETE")
                        .filters(f -> f.filter(filter).filter(adminFilter))
                        .uri(USER_SERVICE_URL))
                // CATEGORY_SERVICE_URL
                .route(r -> r.path("/api/v1/categories/**")
                        .and().method("GET")
                        .filters(f -> f.filter(filter))
                        .uri(CATEGORY_SERVICE_URL))
                .route(r -> r.path("/api/v1/categories/**")
                        .and().method("POST", "PUT", "DELETE")
                        .filters(f -> f.filter(filter).filter(adminFilter))
                        .uri(CATEGORY_SERVICE_URL))
                // PRODUCT_SERVICE_URL
                .route(r -> r.path("/api/v1/products/**")
                        .and().method("GET")
                        .uri(PRODUCT_SERVICE_URL))
                .route(r -> r.path("/api/v1/products/**")
                        .and().method("POST", "PUT", "DELETE")
                        .filters(f -> f.filter(filter).filter(adminFilter))
                        .uri(PRODUCT_SERVICE_URL))
                // ORDER_SERVICE_URL
                .route(r -> r.path("/api/v1/orders/**")
                        .and().method( "GET","POST","PUT")
                        .filters(f -> f.filter(filter))
                        .uri(ORDER_SERVICE_URL))
                .route(r -> r.path("/api/v1/orders/**")
                        .and().method( "DELETE")
                        .filters(f -> f.filter(filter).filter(adminFilter))
                        .uri(ORDER_SERVICE_URL))
                .build();
    }
}
