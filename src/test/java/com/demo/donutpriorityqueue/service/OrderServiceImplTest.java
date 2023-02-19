package com.demo.donutpriorityqueue.service;

import com.demo.donutpriorityqueue.dto.CreateOrderRequest;
import com.demo.donutpriorityqueue.dto.OrderDeliveryDto;
import com.demo.donutpriorityqueue.dto.OrderResponse;
import com.demo.donutpriorityqueue.dto.UpcomingDeliveryDto;
import com.demo.donutpriorityqueue.repository.OrderRepository;
import com.demo.donutpriorityqueue.dto.*;
import com.demo.donutpriorityqueue.entity.Order;
import com.demo.donutpriorityqueue.mapper.OrderMapper;
import com.demo.donutpriorityqueue.util.OrderQueueUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderQueueUtil orderQueueUtil;

    @Test
    void testCreateOrder() {
        // given
        CreateOrderRequest createOrderRequest = new CreateOrderRequest((short) 11, (short) 10);
        OrderResponse order = new OrderResponse(
                createOrderRequest.customerId(),
                createOrderRequest.quantity(),
                1,
                5);
        given(orderMapper.orderToOrderResponse(any())).willReturn(order);

        // when
        OrderResponse newOrder = orderService.createOrder(createOrderRequest);

        // then
        assertEquals(order, newOrder);
    }

    @Test
    void testCreateExistingOrders() {
        // given
        CreateOrderRequest createOrderRequest = new CreateOrderRequest((short) 11, (short) 10);
        given(orderRepository.findByCustomerId((short) 11)).willReturn(new Order());

        // then
        assertThrows(EntityExistsException.class, () -> orderService.createOrder(createOrderRequest));
    }

    @Test
    void testCreateOrdersExceedingMaximumCapacity() {
        // given
        CreateOrderRequest createOrderRequest = new CreateOrderRequest((short) 11, (short) 60);

        // then
        assertThrows(IllegalStateException.class, () -> orderService.createOrder(createOrderRequest));
    }

    @Test
    void testCreateOrdersWithInvalidCustomerId() {
        // given
        CreateOrderRequest createOrderRequest = new CreateOrderRequest((short) 40000, (short) 30);

        // then
        assertThrows(IllegalStateException.class, () -> orderService.createOrder(createOrderRequest));
    }

    @Test
    void testCancelOrder() {
        // given
        given(orderRepository.findByCustomerId((short) 1)).willReturn(new Order());

        // when
        orderService.cancelOrder((short) 1);

        // then
        verify(orderRepository, times(1)).deleteByCustomerId((short) 1);
    }

    @Test
    void testCancelNotExistingOrder() {
        // given
        given(orderRepository.findByCustomerId((short) 1)).willReturn(null);

        // when/then
        assertThrows(NoResultException.class, () -> orderService.cancelOrder((short) 1));

    }

    @Test
    void testViewOrder() {
        // given
        short customerId = 1;
        given(orderRepository.findByCustomerId(customerId)).willReturn(new Order());

        // when
        orderService.viewOrder(customerId);

        // then
        verify(orderRepository, atLeastOnce()).findByCustomerId(customerId);
        verify(orderMapper, times(1)).orderToOrderResponse(any());

    }

    @Test
    void testViewNotExistingOrder() {
        // given
        short customerId = 1;
        given(orderRepository.findByCustomerId(customerId)).willReturn(null);

        // when/then
        assertThrows(NoResultException.class, () -> orderService.viewOrder(customerId));
    }

    @Test
    void testViewQueue() {
        // given
        Pageable pageable = Pageable.ofSize(3).first();

        // when
        orderService.viewQueue(pageable);

        // then
        verify(orderQueueUtil, times(1)).getOrderQueue();
        verify(orderMapper, times(1)).orderResponseListToOrderQueueDto(any(), any());

    }

    @Test
    void testViewNextDelivery() {
        // given
        Order order1 = new Order((short) 1, (short) 20);
        Order order2 = new Order((short) 2, (short) 25);
        Order order3 = new Order((short) 3, (short) 15);

        List<Order> orders = List.of(order1, order2, order3);
        List<OrderDeliveryDto> expectedDeliveryDto = List.of(
                new OrderDeliveryDto(order1.getCustomerId(), order1.getQuantity()),
                new OrderDeliveryDto(order2.getCustomerId(), order2.getQuantity())
        );
        when(orderQueueUtil.getOrderQueue()).thenReturn(orders);

        // when
        UpcomingDeliveryDto upcomingDeliveryDto = orderService.viewNextDelivery();

        // then
        assertEquals(new UpcomingDeliveryDto(expectedDeliveryDto), upcomingDeliveryDto);
    }

}