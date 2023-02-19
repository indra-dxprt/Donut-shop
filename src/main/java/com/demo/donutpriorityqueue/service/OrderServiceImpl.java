package com.demo.donutpriorityqueue.service;

import com.demo.donutpriorityqueue.dto.*;
import com.demo.donutpriorityqueue.entity.Order;
import com.demo.donutpriorityqueue.repository.OrderRepository;
import com.demo.donutpriorityqueue.util.Constant;
import com.demo.donutpriorityqueue.mapper.OrderMapper;
import com.demo.donutpriorityqueue.util.OrderQueueUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderQueueUtil orderQueueUtil;

    /**
     * Service for creating order
     * @param createOrderRequest the dto for creating an order
     * @return the response dto for an order
     * @throws EntityExistsException exception when customer id already existed
     * @throws IllegalStateException exception when bad request body
     */
    @Override
    public OrderResponse createOrder(CreateOrderRequest createOrderRequest) throws EntityExistsException,
            IllegalStateException {
        // check if the customer id exists
        if (orderRepository.findByCustomerId(createOrderRequest.customerId()) != null) {
            throw new EntityExistsException(String.format("Customer %d has an existing order!",
                    createOrderRequest.customerId()));
        }
        // check if the customer id is within the accepted range
        if (createOrderRequest.customerId() > Constant.MAX_CUSTOMER_ID ||
                createOrderRequest.customerId() < Constant.MIN_CUSTOMER_ID) {
            throw new IllegalStateException("Invalid customer id");
        }
        // check if the order quantity exceeds the maximum
        if (createOrderRequest.quantity() > Constant.CART_CAPACITY) {
            throw new IllegalStateException(String.format("The order exceeds the maximum quantity available to buy: %d",
                    Constant.CART_CAPACITY));
        }

        Order order = new Order(createOrderRequest.customerId(), createOrderRequest.quantity());
        return orderMapper.orderToOrderResponse(orderRepository.save(order));
    }

    /**
     * Service for cancelling order
     * @param customerId id of a customer
     * @throws NoResultException exception when order does not exist with the customer id
     */
    @Override
    public void cancelOrder(Short customerId) throws NoResultException {
        // check if the customer id exists
        if (orderRepository.findByCustomerId(customerId) == null) {
            throw new NoResultException(String.format("No order had been created by customer %d!", customerId));
        }

        orderRepository.deleteByCustomerId(customerId);
    }

    /**
     * Service for viewing order
     * @param customerId id of a customer
     * @return the response dto for an order
     * @throws NoResultException exception when order does not exist with the customer id
     */
    @Override
    public OrderResponse viewOrder(Short customerId) throws NoResultException {
        // check if the customer id exists
        if (orderRepository.findByCustomerId(customerId) == null) {
            throw new NoResultException(String.format("No order had been created by customer %d!", customerId));
        }

        return orderMapper.orderToOrderResponse(orderRepository.findByCustomerId(customerId));
    }

    /**
     * Service for viewing the order queue
     * @param pageable attributes for defining a page
     * @return the dto for the order queue
     */
    @Override
    public OrderQueueDto viewQueue(Pageable pageable) {
        List<Order> orderQueue = orderQueueUtil.getOrderQueue();
        List<OrderResponse> queuedOrderResponses = orderQueue.stream()
                .map(orderMapper::orderToOrderResponse).toList();

        return orderMapper.orderResponseListToOrderQueueDto(queuedOrderResponses, pageable);

    }

    /**
     * Service for viewing the next delivery
     * @return the dto for the next delivery
     */
    @Override
    public UpcomingDeliveryDto viewNextDelivery() {
        List<Order> orderQueue = orderQueueUtil.getOrderQueue();

        int cartCapacity = Constant.CART_CAPACITY;
        List<OrderDeliveryDto> deliveryList = new ArrayList<>();
        for (Order order : orderQueue) {
            // if cart has capacity for the order
            if (order.getQuantity() < cartCapacity) {
                // add order to the cart
                deliveryList.add(new OrderDeliveryDto(order.getCustomerId(), order.getQuantity()));
                cartCapacity -= order.getQuantity();
            } else {
                break; // give up the order
            }
        }

        return new UpcomingDeliveryDto(deliveryList);
    }


}
