package com.example.orderservice.service.service;

import com.example.orderservice.entity.OrderStateEntity;
import com.example.orderservice.repository.OrderStateRepository;
import com.example.orderservice.service.OrderStateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderStateServiceTest {
    @Mock
    private OrderStateRepository orderStateRepository;

    @InjectMocks
    private OrderStateService orderStateService;
    @Test
    public void testGetAll() {
        // given
        List<OrderStateEntity> orderStates = new ArrayList<>();
        orderStates.add(new OrderStateEntity(1, "WAITING_PAYMENT"));
        orderStates.add(new OrderStateEntity(2, "PROCESSED"));
        when(orderStateRepository.findAll()).thenReturn(orderStates);

        // when
        List<OrderStateEntity> result = orderStateService.getAll();

        // then
        verify(orderStateRepository, times(1)).findAll();
        assertEquals(orderStates.size(), result.size());
        assertEquals(orderStates.get(0).getOrder_state_id(), result.get(0).getOrder_state_id());
        assertEquals(orderStates.get(0).getOrder_state_name(), result.get(0).getOrder_state_name());
        assertEquals(orderStates.get(1).getOrder_state_id(), result.get(1).getOrder_state_id());
        assertEquals(orderStates.get(1).getOrder_state_name(), result.get(1).getOrder_state_name());
    }
    @Test
    public void testSeedOrderState() {
        // given
        List<OrderStateEntity> orderStates = new ArrayList<>();

//        orderStates.add(new OrderStateEntity(1,"WAITING_PAYMENT"));
//        orderStates.add(new OrderStateEntity(2,"PROCESSED"));
//        orderStates.add(new OrderStateEntity(3,"ON_DELIVERY"));
//        orderStates.add(new OrderStateEntity(4,"DELIVERED"));
//        orderStates.add(new OrderStateEntity(5,"CANCELLED"));

        when(orderStateRepository.findAll()).thenReturn(orderStates);

        // when
        List<OrderStateEntity> result = orderStateService.seedOrderState();

        // then
        verify(orderStateRepository, times(5)).save(any(OrderStateEntity.class));
        assertEquals(5, result.size());
    }
}
