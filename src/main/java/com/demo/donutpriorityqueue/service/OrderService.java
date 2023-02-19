package com.demo.donutpriorityqueue.service;

import com.demo.donutpriorityqueue.dto.CreateOrderRequest;
import com.demo.donutpriorityqueue.dto.OrderQueueDto;
import com.demo.donutpriorityqueue.dto.OrderResponse;
import com.demo.donutpriorityqueue.dto.UpcomingDeliveryDto;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest createOrderRequest);
    void cancelOrder(Short customerId);
    OrderResponse viewOrder(Short customerId);
    OrderQueueDto viewQueue(Pageable pageable);
    UpcomingDeliveryDto viewNextDelivery();

}
