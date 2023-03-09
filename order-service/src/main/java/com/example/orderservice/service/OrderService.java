package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequestDTO;
import com.example.orderservice.dto.GlobalResponse;
import com.example.orderservice.dto.OrderDetailDTO;
import com.example.orderservice.entity.OrderDetailEntity;
import com.example.orderservice.entity.OrderStateEntity;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.StatusEntity;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.exception.OrderStateNotFoundException;
import com.example.orderservice.repository.OrderDetailRepository;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.OrderStateRepository;
import com.example.orderservice.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
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
//    public ResponseEntity<GlobalResponse> getAllByUserId(Long user_id) {
//        List<Object> orders = orderRepository.findByUser_Id(user_id);
//        log.info("Get all orders by user_id :"+orders);
//        return new ResponseEntity<>(GlobalResponse.builder()
//                .timestamp(LocalDateTime.now())
//                .message("Orders Found.")
//                .status(200)
//                .data(orders)
//                .build(), HttpStatus.OK);
//    }
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
        // Kwitansi yang sudah dibuat tidak boleh diupdate pilihannya hapus atau manage statusnya

        log.info("Save the order entity for each order detail :");
        OrderEntity saved_orderEntity = new OrderEntity();
        saved_orderEntity.setUser_id(request.getOrder().getUser_id());
        log.info("Calculating the total price on order detail :");
        Double total_price = 0.0;
        for (OrderDetailDTO temp_orderDetailDTO : request.getOrderDetail()) {
            total_price += temp_orderDetailDTO.getSale_price();
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

        // Create status entity
        OrderStateEntity saved_orderStateEntity = new OrderStateEntity();

        StatusEntity statusEntity = new StatusEntity();
        statusEntity.setOrder(saved_orderEntity);
        OrderStateEntity DefaultOrderState = orderStateRepository.findById(1).orElseThrow(() -> new OrderStateNotFoundException(1));

        statusEntity.setOrderState(DefaultOrderState);
        statusEntity.setIs_paid(0);
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


}
