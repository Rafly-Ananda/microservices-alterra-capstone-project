package com.example.orderservice.service.service;

import com.example.orderservice.dto.*;
import com.example.orderservice.entity.OrderDetailEntity;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.OrderStateEntity;
import com.example.orderservice.entity.StatusEntity;
import com.example.orderservice.exception.OrderCancellationNotAllowedException;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.exception.ProductNotFoundException;
import com.example.orderservice.repository.OrderDetailRepository;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.OrderStateRepository;
import com.example.orderservice.repository.StatusRepository;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.service.OrderStateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private StatusRepository statusRepository;
    @Mock
    private OrderStateRepository orderStateRepository;
    @InjectMocks
    private OrderService orderService;
    @InjectMocks
    private OrderStateService orderStateService;
    @Mock
    private RestTemplate restTemplate;
    public void setUp() {
        // Insert test data for OrderState
        OrderStateEntity orderState = new OrderStateEntity();
        orderState.setOrder_state_id(1);
        orderState.setOrder_state_name("WAITING_PAYMENT");
        orderStateRepository.save(orderState);
    }
    @Test
    void testGetAll() {
        // Setup
        List<OrderEntity> orders = new ArrayList<>();
        OrderEntity order1 = new OrderEntity(1L, 1, null, null, 100.0, LocalDateTime.now(), LocalDateTime.now());
        OrderEntity order2 = new OrderEntity(2L, 2, null, null, 200.0, LocalDateTime.now(), LocalDateTime.now());
        orders.add(order1);
        orders.add(order2);

        when(orderRepository.findAll()).thenReturn(orders);

        // Execute
        ResponseEntity<GlobalResponse> responseEntity = orderService.getAll();

        // Verify
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        GlobalResponse globalResponse = responseEntity.getBody();
        assertEquals("Orders found.", globalResponse.getMessage());
        assertEquals(2, ((List<OrderEntity>) globalResponse.getData()).size());
        assertEquals(order1.getUserId(), ((List<OrderEntity>) globalResponse.getData()).get(0).getUserId());
        assertEquals(order2.getUserId(), ((List<OrderEntity>) globalResponse.getData()).get(1).getUserId());
        assertEquals(order1.getTotal(), ((List<OrderEntity>) globalResponse.getData()).get(0).getTotal());
        assertEquals(order2.getTotal(), ((List<OrderEntity>) globalResponse.getData()).get(1).getTotal());
    }
    @Test
    public void testGetById() {
        // Create a mock OrderEntity object
        OrderEntity order1 = new OrderEntity(1L, 1, null, null, 100.0, LocalDateTime.now(), LocalDateTime.now());

        // Set up the mock behavior for the orderRepository
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));

        // Call the getById method with the mock ID
        ResponseEntity<GlobalResponse> responseEntity = orderService.getById(1L);

        // Assert that the response status code is 200
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert that the response message is "Order Found."
        assertEquals("Order Found.", responseEntity.getBody().getMessage());

        // Assert that the response data contains the mock OrderEntity object
        assertEquals(Arrays.asList(Optional.of(order1)), responseEntity.getBody().getData());
    }
    @Test
    public void testGetAllByUserId() {
        // given
        Integer userId = 1;
        OrderEntity order1 = new OrderEntity(1L, userId, null, null, 100.0, LocalDateTime.now(), LocalDateTime.now());
        OrderEntity order2 = new OrderEntity(2L, userId, null, null, 200.0, LocalDateTime.now(), LocalDateTime.now());
        List<OrderEntity> orders = Arrays.asList(order1, order2);

        when(orderRepository.findByUserId(userId)).thenReturn(orders);

        // when
        ResponseEntity<GlobalResponse> response = orderService.getAllByUserId(userId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orders, response.getBody().getData());
    }
    @Test
    public void createOrderTest() throws Exception {
        OrderStateEntity orderStates = new OrderStateEntity(1,"WAITING_PAYMENT");
        when(orderStateRepository.findById(1)).thenReturn(Optional.of(orderStates));

        // Setup mock response for product 1
        String responseBody1 = "{\"data\":[{\"product\":{\"id\":1,\"name\":\"Product 1\",\"price\":10.0,\"description\":\"Product 1 description\",\"stock\":20},\"categoryDTO\":{\"id\":1,\"name\":\"Category 1\",\"description\":\"Category 1 description\"}}]}";
        ResponseEntity<String> response1 = ResponseEntity.ok(responseBody1);
        when(restTemplate.getForEntity(eq("http://product-service:8084/api/v1/products/1"), eq(String.class)))
                .thenReturn(response1);

        // Setup mock response for product 2
        String responseBody2 = "{\"data\":[{\"product\":{\"id\":2,\"name\":\"Product 2\",\"price\":5.0,\"description\":\"Product 2 description\",\"stock\":15},\"categoryDTO\":{\"id\":2,\"name\":\"Category 2\",\"description\":\"Category 2 description\"}}]}";
        ResponseEntity<String> response2 = ResponseEntity.ok(responseBody2);
        when(restTemplate.getForEntity(eq("http://product-service:8084/api/v1/products/2"), eq(String.class)))
                .thenReturn(response2);

        // Create the test order
        OrderService orderService = new OrderService(restTemplate, orderRepository, orderDetailRepository,statusRepository,orderStateRepository, orderStateService);


        CreateOrderRequestDTO request = new CreateOrderRequestDTO();
        request.setOrder(new OrderDTO(1));
        List<OrderDetailDTO> orderDetails = new ArrayList<>();
        orderDetails.add(new OrderDetailDTO(1L, 2));
        orderDetails.add(new OrderDetailDTO(2L, 5));
        request.setOrderDetail(orderDetails);

        // Call the create method and verify the results
        ResponseEntity<GlobalResponse> response = orderService.create(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Order Created.", response.getBody().getMessage());
    }
    @Test
    public void testDeleteOrder() {
        // arrange
        Long orderId = 1L;
        OrderEntity order = new OrderEntity(orderId, 1, null, null, 100.0, LocalDateTime.now(), LocalDateTime.now());
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // act
        ResponseEntity<GlobalResponse> responseEntity = orderService.delete(orderId);
        GlobalResponse globalResponse = responseEntity.getBody();

        // assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Order Id " + orderId + " Is Deleted.", globalResponse.getMessage());
        assertEquals(1, globalResponse.getData().size());
        assertEquals(order, globalResponse.getData().get(0));
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    public void testDeleteNonExistingOrder() {
        // arrange
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // act and assert
        assertThrows(OrderNotFoundException.class, () -> orderService.delete(orderId));
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).deleteById(orderId);
    }

    @Test
    void testUpdateStateOrder() {
        // Create test data
        OrderEntity order = new OrderEntity();
        order.setOrder_id(1L);
        order.setUserId(1);
        StatusEntity statusEntity = new StatusEntity();
        statusEntity.setStatus_id(1L);
        statusEntity.setOrderState(new OrderStateEntity(1, "New"));
        order.setStatus(statusEntity);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderStateRepository.findById(2)).thenReturn(Optional.of(new OrderStateEntity(2, "Processing")));

        // Call the service method
        ResponseEntity<GlobalResponse> response = orderService.updateStateOrder(1L, 2);

        // Verify the results
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GlobalResponse globalResponse = response.getBody();
        assertNotNull(globalResponse);
        assertEquals("Order Id 1's status is Updated to2", globalResponse.getMessage());
        assertEquals(200, globalResponse.getStatus());
        List<OrderEntity> data = (List<OrderEntity>) globalResponse.getData();
        assertNotNull(data);
        assertEquals(1, data.size());
        OrderEntity updatedOrder = data.get(0);
        assertNotNull(updatedOrder);
        assertEquals(order.getOrder_id(), updatedOrder.getOrder_id());
        assertEquals(order.getUserId(), updatedOrder.getUserId());
        assertNotNull(updatedOrder.getStatus());
        assertEquals(2, updatedOrder.getStatus().getOrderState().getOrder_state_id());
    }

    @Test
    public void testCancelOrderByOrderId() {
        // Create mock data
        Long orderId = 1L;
        OrderStateEntity orderStateEntity1 = new OrderStateEntity(1, "Processed");
        OrderStateEntity orderStateEntity2 = new OrderStateEntity(5, "Cancelled");
        StatusEntity statusEntity = new StatusEntity(1L, null, null, orderStateEntity1);
        OrderEntity orderEntity = new OrderEntity(orderId, 1, statusEntity, null, 100.0, null, null);



        // Mock dependencies
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(orderStateRepository.findById(5)).thenReturn(Optional.of(orderStateEntity2));
        when(statusRepository.save(statusEntity)).thenReturn(statusEntity);

        // Call the method to test
        ResponseEntity<GlobalResponse> responseEntity = orderService.cancelOrderByOrderId(orderId);

        // Verify the method behavior and response
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderStateRepository, times(1)).findById(5);
        verify(statusRepository, times(1)).save(statusEntity);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Order Id 1 Is Cancelled.", responseEntity.getBody().getMessage());
        assertEquals(200, responseEntity.getBody().getStatus());
        assertEquals(1, responseEntity.getBody().getData().size());
        assertEquals(orderEntity, responseEntity.getBody().getData().get(0));
    }

    @Test
    public void testCancelOrderByOrderId_OrderNotFound() {
        // Create mock data
        Long orderId = 1L;

        // Mock dependencies
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Call the method to test
        try {
            orderService.cancelOrderByOrderId(orderId);
            fail("Expected OrderNotFoundException to be thrown");
        } catch (OrderNotFoundException e) {
            // Test passed
        }
    }

    @Test
    public void testCancelOrderByOrderId_OrderCancellationNotAllowed() {
        // Create mock data
        Long orderId = 1L;
        OrderStateEntity orderStateEntity1 = new OrderStateEntity(2, "Processed");
        OrderStateEntity orderStateEntity2 = new OrderStateEntity(5, "Cancelled");
        StatusEntity statusEntity = new StatusEntity(1L, null, null, orderStateEntity1);
        OrderEntity orderEntity = new OrderEntity(orderId, 1, statusEntity, null, 100.0, null, null);

        // Mock dependencies
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
//        when(statusRepository.save(statusEntity)).thenReturn(statusEntity);

        // Call the method to test
        try {
            orderService.cancelOrderByOrderId(orderId);
            fail("Expected OrderCancellationNotAllowedException to be thrown");
        } catch (OrderCancellationNotAllowedException e) {
            // Test passed
        }
    }








}
