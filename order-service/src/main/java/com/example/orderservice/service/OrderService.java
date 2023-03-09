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
        List<?> order_byuser = orderRepository.findByUserId(user_id);
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
        /*
        * Kwitansi yang dibuat tidak dapat diupdate hanya bisa dibatalkan
        * */

//        log.info("Check availabilty of each Product :");
//        for (OrderDetailDTO temp_orderDetailDTO : request.getOrderDetail()) {
//            Long orderedProductId = temp_orderDetailDTO.getProduct_id();
//            Integer orderedProductQty = temp_orderDetailDTO.getQuantity();
//            try{
//                String categoryUrl = "http://product-service:8084/api/v1/products/" + orderedProductId;
//                ResponseEntity<String> response = restTemplate.getForEntity(categoryUrl, String.class);
//
//                ObjectMapper mapper = new ObjectMapper();
//                JsonNode root = mapper.readTree(response.getBody());
//                Integer stockExisting = (root.path("data").path(0).path("stock").asInt());
//                if(stockExisting < orderedProductQty){
//                    throw new StockInsufficientException(orderedProductId);
//                }
//            }catch(Exception e){
//                throw new ProductNotFoundException(orderedProductId);
//            }
//        }

        log.info("Save the order entity for each order detail :");
        OrderEntity saved_orderEntity = new OrderEntity();
        saved_orderEntity.setUserId(request.getOrder().getUser_id());
        log.info("Calculating the total price on order detail and adjust stock in inventory :");
        Double total_price = 0.0;
        for (OrderDetailDTO temp_orderDetailDTO : request.getOrderDetail()) {
//            Long orderedProductId = temp_orderDetailDTO.getProduct_id();
//            Integer orderedProductQty = temp_orderDetailDTO.getQuantity();
//            log.info("Get Existing stock of Product ["+orderedProductId+"] ...");
//            Integer stockExisting = 0;
//            try{
//                String categoryUrl = "http://product-service:8084/api/v1/products/" + orderedProductId;
//                ResponseEntity<String> response = restTemplate.getForEntity(categoryUrl, String.class);
//                ObjectMapper mapper = new ObjectMapper();
//                JsonNode root = mapper.readTree(response.getBody());
//                stockExisting += (root.path("data").path(0).path("stock").asInt());
//                log.info("stockExisting of ["+orderedProductId+"] : "+stockExisting);
//            }catch(Exception e){
//                log.info("Fail Get Existing stock : "+orderedProductId);
//                throw new RuntimeException(e);
//            }
//            log.info("Adjsuting Stock ...");
//            Integer StockRemain = stockExisting - orderedProductQty;
//            log.info("StockRemain of Product ["+orderedProductId+"] : "+StockRemain);
//            try {
//                String endpointUrl = "http://product-service:8084/api/v1/products/" + orderedProductId;
//                Map<String, Integer> requestBody = new HashMap<>();
//                requestBody.put("stock", StockRemain);
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(MediaType.APPLICATION_JSON);
//                HttpEntity<Map<String, Integer>> requestEntity = new HttpEntity<>(requestBody, headers);
//                ResponseEntity<GlobalResponse> responseEntity = restTemplate.exchange(endpointUrl, HttpMethod.PUT, requestEntity, GlobalResponse.class);
//                log.info("Adjusted stockRemain of Product ["+orderedProductId+"]");
//            }catch (Exception e){
//                throw new RuntimeException(e);
//            }

            log.info("Calculating total price ...");
            Double subtotal = temp_orderDetailDTO.getSale_price()*temp_orderDetailDTO.getQuantity();
            total_price += subtotal;
        }
        saved_orderEntity.setTotal(total_price);
        orderRepository.save(saved_orderEntity);

        log.info("Save the order entity for each order detail :");
        for (OrderDetailDTO orderDetailDTO : request.getOrderDetail()) {
            OrderDetailEntity saved_orderDetailEntity = new OrderDetailEntity();
            saved_orderDetailEntity.setOrder(saved_orderEntity);
            saved_orderDetailEntity.setProduct_id(orderDetailDTO.getProduct_id());
            saved_orderDetailEntity.setQuantity(orderDetailDTO.getQuantity());
            saved_orderDetailEntity.setSale_price(orderDetailDTO.getSale_price());
            orderDetailRepository.save(saved_orderDetailEntity);
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

    //Kwintansi kayanya di flag aja kalo gagal atau apa
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
        StatusEntity statusEntity = new StatusEntity();
        OrderStateEntity CancelOrderState = orderStateRepository.findById(state).orElseThrow(() -> new OrderStateNotFoundException(state));
        statusEntity.setOrder(order);
        statusEntity.setOrderState(CancelOrderState);
        //find status
        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Order Id "+ id +"'s status is Updated to"+state)
                .status(200)
                .data(List.of(order))
                .build(), HttpStatus.OK);
    }
    public ResponseEntity<GlobalResponse> cancelOrder(Long id) {
        OrderEntity order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException((id)));
        StatusEntity statusEntity = new StatusEntity();
        OrderStateEntity CancelOrderState = orderStateRepository.findById(5).orElseThrow(() -> new OrderStateNotFoundException(5));
        statusEntity.setOrder(order);
        statusEntity.setOrderState(CancelOrderState);
        //find status
        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Order Id " + id + " Is Cancelled.")
                .status(200)
                .data(List.of(order))
                .build(), HttpStatus.OK);
    }


}
