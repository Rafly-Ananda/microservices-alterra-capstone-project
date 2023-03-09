package com.example.orderservice.service;

import com.example.orderservice.dto.GlobalResponse;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.OrderStateEntity;
import com.example.orderservice.entity.StatusEntity;
import com.example.orderservice.repository.OrderStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderStateService {
    private final OrderStateRepository orderStateRepository;

    public ResponseEntity<GlobalResponse> getAll() {
        List<OrderStateEntity> orderState = orderStateRepository.findAll();
        log.info("Get all order-states :");
        return new ResponseEntity<>(GlobalResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("Order-state :")
                .status(200)
                .data(orderState)
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<GlobalResponse> seedOrderState(){
        /*
        *   1 WAITING_PAYMENT (DEFAULT)
            2 PROCESSED
            3 ON_DELIVERY
            4 DELIVERED
            5 CANCELLED
        * */
        List<OrderStateEntity> cekOrderState = orderStateRepository.findAll();
        if(!cekOrderState.isEmpty()){
            throw new RuntimeException("Can't seed the order-state. Order state is already exist");
        }else{
            log.info("Seeding order-states :");
            List<OrderStateEntity> seed = new ArrayList<>();
            seed.add(new OrderStateEntity(1,"WAITING_PAYMENT"));
            seed.add(new OrderStateEntity(2,"PROCESSED"));
            seed.add(new OrderStateEntity(3,"ON_DELIVERY"));
            seed.add(new OrderStateEntity(4,"DELIVERED"));
            seed.add(new OrderStateEntity(5,"CANCELLED"));
            for(OrderStateEntity saved_seed : seed){
                OrderStateEntity saved_state = new OrderStateEntity();
                saved_state.setOrder_state_id(saved_seed.getOrder_state_id());
                saved_state.setOrder_state_name(saved_seed.getOrder_state_name());
                orderStateRepository.save(saved_state);
            }
            return new ResponseEntity<>(GlobalResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .message("seed Order-state success.")
                    .status(200)
                    .data(seed)
                    .build(), HttpStatus.OK);
        }
    }
}
