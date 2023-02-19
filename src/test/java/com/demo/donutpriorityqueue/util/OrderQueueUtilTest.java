package com.demo.donutpriorityqueue.util;

import com.demo.donutpriorityqueue.entity.Order;
import com.demo.donutpriorityqueue.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderQueueUtilTest {
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderQueueUtil orderQueueUtil;
    private OrderQueueUtil spyOrderQueueUtil;

    @BeforeEach
    public void setup() {
        spyOrderQueueUtil = Mockito.spy(orderQueueUtil);
    }

    @Test
    public void testGetPremiumOrders() {
        // when
        orderQueueUtil.getPremiumOrders();

        // then
        verify(orderRepository, times(1)).findByCustomerIdLessThan((short) 1000);
    }

    @Test
    public void testGetOrderQueue() {
        // given
        Order order1 = new Order((short) 10000, (short) 20);
        Order order2 = new Order((short) 20, (short) 25);
        Order order3 = new Order((short) 300, (short) 15);

        List<Order> premiumOrders = new ArrayList<>(List.of(order2, order3));
        List<Order> allOrders = List.of(order1, order2, order3);

        List<Order> expectedQueue = List.of(order2, order3, order1);
        when(spyOrderQueueUtil.getPremiumOrders()).thenReturn(premiumOrders);
        when(orderRepository.findAll()).thenReturn(allOrders);

        // when
        List<Order> orderQueue = spyOrderQueueUtil.getOrderQueue();

        // then
        assertEquals(expectedQueue, orderQueue);
    }

    @Test
    public void computeWaitTime() {
        // given
        Order currentOrder = new Order((short) 10000, (short) 20);
        Order order2 = new Order((short) 20, (short) 25);
        Order order3 = new Order((short) 300, (short) 15);
        Order order4 = new Order((short) 600, (short) 30);
        Order order5 = new Order((short) 400, (short) 15);

        List<Order> previousOrders = List.of(order2, order3, order4, order5);
        int expectedWaitTime = 15;

        // when
        int waitTime = orderQueueUtil.computeWaitTime(previousOrders, currentOrder);

        // then
        assertEquals(expectedWaitTime, waitTime);
    }
}
