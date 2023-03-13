package com.example.orderservice.service;

import com.example.orderservice.dto.GlobalResponse;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.OrderStateEntity;
import com.example.orderservice.entity.StatusEntity;
import com.example.orderservice.repository.OrderStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableCaching
public class OrderStateService {
    private final OrderStateRepository orderStateRepository;
    @Cacheable(value="orderState")
    public List<OrderStateEntity> getAll() {
        log.info("Get all order-states from DB :");
        List<OrderStateEntity> orderState = orderStateRepository.findAll();
        log.info("Get all order-states :");
        return orderState;
    }
    @CacheEvict(cacheNames = "orderState", allEntries = true)
    public List<OrderStateEntity> seedOrderState(){
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
            return seed;
        }
    }
}
