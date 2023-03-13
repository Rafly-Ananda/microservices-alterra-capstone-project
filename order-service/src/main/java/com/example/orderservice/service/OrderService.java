package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequestDTO;
import com.example.orderservice.dto.GlobalResponse;
import com.example.orderservice.dto.OrderDetailDTO;
import com.example.orderservice.dto.ProductDTO;
import com.example.orderservice.entity.OrderDetailEntity;
import com.example.orderservice.entity.OrderStateEntity;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.StatusEntity;
import com.example.orderservice.exception.*;
import com.example.orderservice.repository.OrderDetailRepository;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.OrderStateRepository;
import com.example.orderservice.repository.StatusRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final StatusRepository statusRepository;
    private final OrderStateRepository orderStateRepository;

    private final OrderStateService orderStateService;
    public ResponseEntity<GlobalResponse> getAll() {
        List<OrderEntity> categories = orderRepository.findAll();
        log.info("Get all orders :");
        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Orders found.")
                .status(200)
                .data(categories)
                .build(), HttpStatus.OK);
    }
    public ResponseEntity<GlobalResponse> getAllByUserId(Integer user_id) {
        List<OrderEntity> order_byuser = orderRepository.findByUserId(user_id);
        if(order_byuser.isEmpty()){
            throw new UserNotFoundException(user_id);
        }else{
            log.info("Get all orders by user_id :"+user_id);
            return new ResponseEntity<>(GlobalResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .message("Orders Found.")
                    .status(200)
                    .data(order_byuser)
                    .build(), HttpStatus.OK);
        }
    }
    public ResponseEntity<GlobalResponse> getById(Long id) {
        OrderEntity order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        log.info("Get all orders by order_id :"+order);
        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Order Found.")
                .status(200)
                .data(List.of(order))
                .build(), HttpStatus.OK);
    }
    public ResponseEntity<GlobalResponse> create(CreateOrderRequestDTO request) {
        List<OrderStateEntity> orderState = orderStateRepository.findAll();

        if (orderState.isEmpty()) {
            orderStateService.seedOrderState();
        }

        log.info("Check availabilty of each Product :");
        for (OrderDetailDTO temp_orderDetailDTO : request.getOrderDetail()) {
            Long orderedProductId = temp_orderDetailDTO.getProduct_id();
            Integer orderedProductQty = temp_orderDetailDTO.getQuantity();
            log.info("http://product-service:8084/api/v1/products/" + orderedProductId);

            String categoryUrl = "http://product-service:8084/api/v1/products/" + orderedProductId;
            ResponseEntity<String> response = restTemplate.getForEntity(categoryUrl, String.class);
            log.info(String.valueOf(response));
            try{
                String responseBody = response.getBody();
                log.info("Response body: " + responseBody);
                if(responseBody == null || responseBody.isEmpty()){
                    throw new ProductNotFoundException(orderedProductId);
                }
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(responseBody);
                Integer stockExisting = root
                        .path("data")
                        .path(0)
                        .path("product")
                        .path("stock")
                        .asInt();
                log.info("Product id["+orderedProductId+"]"+"StockExisting: " + stockExisting);
                log.info("Product id["+orderedProductId+"]"+"StockOrdered: " + orderedProductQty);
                if(stockExisting < orderedProductQty){
                    log.info("Order failed, Insufficient Stock");
                    throw new StockInsufficientException(orderedProductId);
                }
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }

        log.info("Save the order entity :");
        OrderEntity saved_orderEntity = new OrderEntity();
        Double total_price = 0.0;
        for (OrderDetailDTO temp_orderDetailDTO : request.getOrderDetail()) {
            String productUrl = "http://product-service:8084/api/v1/products/" + temp_orderDetailDTO.getProduct_id();;
            ResponseEntity<String> productDetailResponse = restTemplate.getForEntity(productUrl, String.class);
            try{
                String productDetailResponseBody = productDetailResponse.getBody();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(productDetailResponseBody);
                log.info("Calculating total price ...");
                Double ProductPrice = root.path("data").path(0).path("product").path("price").asDouble();
                Double subtotal = ProductPrice*temp_orderDetailDTO.getQuantity();
                total_price += subtotal;
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }
        saved_orderEntity.setTotal(total_price);
        saved_orderEntity.setUserId(request.getOrder().getUser_id());
        orderRepository.save(saved_orderEntity);

        log.info("Initialize input order details");
        for (OrderDetailDTO temp_orderDetailDTO : request.getOrderDetail()) {
            //variable used to update stock
            Long orderedProductId = temp_orderDetailDTO.getProduct_id();
            Integer orderedProductQty = temp_orderDetailDTO.getQuantity();

            String productUrl = "http://product-service:8084/api/v1/products/" + orderedProductId;
            ResponseEntity<String> productDetailResponse = restTemplate.getForEntity(productUrl, String.class);
            log.info(String.valueOf(productDetailResponse));
            log.info("http://product-service:8084/api/v1/products/" + orderedProductId);
            try{
                //for mapping product detail
                String productDetailResponseBody = productDetailResponse.getBody();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(productDetailResponseBody);
                //varible for update
                Long ProductCategoryId = root.path("data").path(0).path("categoryDTO").path("p_category_id").asLong();
                String ProductName = root.path("data").path(0).path("product").path("name").asText();
                String ProductDescription = root.path("data").path(0).path("product").path("description").asText();
                Double ProductPrice = root.path("data").path(0).path("product").path("price").asDouble();
                Integer ProductStock = root.path("data").path(0).path("product").path("stock").asInt();

                ProductDTO updateProductRequestBody = new ProductDTO();
                updateProductRequestBody.setCategory_id(ProductCategoryId);
                log.info(String.valueOf(ProductCategoryId));
                updateProductRequestBody.setName(ProductName);
                updateProductRequestBody.setDescription(ProductDescription);
                updateProductRequestBody.setPrice(ProductPrice);
                //cari selisih stock
                Integer stockExisting = ProductStock;
                Integer stockRemaining = stockExisting - orderedProductQty;
                updateProductRequestBody.setStock(stockRemaining);

                log.info("Adjusting product ["+orderedProductId+"]'s Stock: "+stockExisting+" >> "+stockRemaining);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<ProductDTO> requestEntity = new HttpEntity<>(updateProductRequestBody, headers);
                restTemplate.exchange(productUrl, HttpMethod.PUT, requestEntity, GlobalResponse.class);

                log.info("Inputing Order Detail Product ["+orderedProductId+"]: ...");
                OrderDetailEntity saved_orderDetailEntity = new OrderDetailEntity();
                saved_orderDetailEntity.setOrder(saved_orderEntity);// input order_id
                saved_orderDetailEntity.setProduct_id(orderedProductId);
                saved_orderDetailEntity.setQuantity(temp_orderDetailDTO.getQuantity());
                saved_orderDetailEntity.setSale_price(ProductPrice);
                orderDetailRepository.save(saved_orderDetailEntity);
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }

        log.info("Create status entity based on saved order :");
        StatusEntity statusEntity = new StatusEntity();
        statusEntity.setOrder(saved_orderEntity);
        OrderStateEntity DefaultOrderState = orderStateRepository.findById(1).orElseThrow(() -> new OrderStateNotFoundException(1));
        statusEntity.setOrderState(DefaultOrderState);
        statusRepository.save(statusEntity);

        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Order Created.")
                .status(200)
                .data(List.of(request.getOrder(), request.getOrderDetail()))
                .build(), HttpStatus.CREATED);
    }

    //[Testing purpose only] Kwintansi kayanya di flag aja kalo gagal atau apa
    public ResponseEntity<GlobalResponse> delete(Long id) {
        OrderEntity order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException((id)));
        orderRepository.deleteById(id);
        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Order Id " + id + " Is Deleted.")
                .status(200)
                .data(List.of(order))
                .build(), HttpStatus.OK);
    }
    public ResponseEntity<GlobalResponse> updateStateOrder(Long id,Integer state) {
        OrderEntity order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException((id)));
        StatusEntity statusEntity = order.getStatus();
        OrderStateEntity updateOrderState = orderStateRepository.findById(state).orElseThrow(() -> new OrderStateNotFoundException(state));
        statusEntity.setOrderState(updateOrderState);
        statusRepository.save(statusEntity);
        //find status
        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Order Id "+ id +"'s status is Updated to"+state)
                .status(200)
                .data(List.of(order))
                .build(), HttpStatus.OK);
    }
    public ResponseEntity<GlobalResponse> cancelOrderByOrderId(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        /*
        * Optional : if the order_state is processed (2) or more cant be canceled
        * */
        if (order.getStatus().getOrderState().getOrder_state_id() != 1) {
            throw new OrderCancellationNotAllowedException(orderId);
        }
        StatusEntity statusEntity = order.getStatus();

        OrderStateEntity cancelOrderState = orderStateRepository.findById(5)
                .orElseThrow(() -> new OrderStateNotFoundException(5));
        statusEntity.setOrderState(cancelOrderState);

        statusRepository.save(statusEntity);

        return ResponseEntity.ok(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Order Id " + orderId + " Is Cancelled.")
                .status(200)
                .data(List.of(order))
                .build());
    }


}
