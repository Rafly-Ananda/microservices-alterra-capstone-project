package com.example.orderservice.service;

import com.example.orderservice.dto.GlobalResponse;
import com.example.orderservice.entity.OrderDetailEntity;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.StatusEntity;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.repository.OrderDetailRepository;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final StatusRepository statusRepository;

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
    public ResponseEntity<GlobalResponse> getAllByUserId(Long user_id) {
        List<Object> orders = orderRepository.findByUser_Id(user_id);
        log.info("Get all orders by user_id :"+orders);
        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Orders Found.")
                .status(200)
                .data(orders)
                .build(), HttpStatus.OK);
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
    public ResponseEntity<GlobalResponse> create(OrderEntity orderEntity, List<OrderDetailEntity> orderDetailEntities) {
        // Kwitansi yang sudah dibuat tidak boleh diupdate pilihannya hapus atau manage statusnya
        log.info("Set the order entity for each order detail :");
        for (OrderDetailEntity orderDetailEntity : orderDetailEntities) {
            log.info(String.valueOf(orderDetailEntity.getOrder_detail_id()));
            orderDetailEntity.setOrder(orderEntity);
        }

        log.info("Set the is_paid attribute of the status entity to 0 :");
        StatusEntity statusEntity = orderEntity.getStatus();
        statusEntity.setIs_paid(0);

        log.info("Save the order entity to get the generated order_id");
        OrderEntity savedOrderEntity = orderRepository.save(orderEntity);

        log.info("Set the order_id attribute of each order detail entity to the generated order_id");
        for (OrderDetailEntity orderDetailEntity : orderDetailEntities) {
            orderDetailEntity.setOrder(savedOrderEntity);
        }
        // Save the order detail entities
        orderDetailRepository.saveAll(orderDetailEntities);

        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Order Created.")
                .status(200)
                .data(List.of(savedOrderEntity, orderDetailEntities))
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
