package com.demo.donutpriorityqueue.mapper;

import com.demo.donutpriorityqueue.entity.Order;
import com.demo.donutpriorityqueue.repository.OrderRepository;
import com.demo.donutpriorityqueue.dto.OrderQueueDto;
import com.demo.donutpriorityqueue.dto.OrderResponse;
import com.demo.donutpriorityqueue.util.OrderQueueUtil;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.min;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderQueueUtil orderQueueUtil;

    /**
     * Map an order entity to the response dto for the order
     * @param order order entity
     * @return the response dto for an order
     */
    public abstract OrderResponse orderToOrderResponse(Order order);

    /**
     * Map a list of order response dto to dto for the order queue
     * @param queuedOrderResponses list of order response dto
     * @param pageable attributes for defining a page
     * @return the dto for the order queue
     */
    public abstract OrderQueueDto orderResponseListToOrderQueueDto(List<OrderResponse> queuedOrderResponses,
                                                                   Pageable pageable);

    /**
     * Will be called by orderToOrderResponse to compute the queuing status
     * @param order order entity
     * @return the response dto for an order
     */
    @BeforeMapping
    public OrderResponse getQueuingStatus(Order order) {
        Set<Order> previousOrders = new HashSet<>(orderRepository.findByTimestampLessThan(order.getTimestamp()));
        List<Order> prioritizedOrders = orderQueueUtil.getPremiumOrders();

        // check if current order is from premium customer
        if (order.getCustomerId() < 1000) {
            // filter out non-premium orders from previous orders
            previousOrders = previousOrders
                    .stream()
                    .filter(prioritizedOrders::contains)
                    .collect(Collectors.toSet());
        } else {
            // add premium orders in previous orders
            previousOrders.addAll(prioritizedOrders);
        }

        int position = previousOrders.size() + 1;
        int waitTime = orderQueueUtil.computeWaitTime(previousOrders, order);

        return new OrderResponse(order.getCustomerId(), order.getQuantity(), position, waitTime);

    }

    /**
     * Will be called by orderResponseListToOrderQueueDto to get order queue with pagination
     * @param queuedOrderResponses list of order response dto
     * @param pageable attributes for defining a page
     * @return the dto for the order queue
     */
    @BeforeMapping
    public OrderQueueDto getOrderPage(List<OrderResponse> queuedOrderResponses, Pageable pageable) {
        int totalElements = queuedOrderResponses.size();
        int pageStart = (int) pageable.getOffset();
        int pageSize = pageable.getPageSize();
        int pageEnd = min(pageStart + pageSize, totalElements);

        return new OrderQueueDto(new PageImpl<>(queuedOrderResponses.subList(pageStart, pageEnd), pageable, totalElements));
    }

}
