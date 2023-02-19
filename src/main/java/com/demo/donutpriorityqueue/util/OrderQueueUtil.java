package com.demo.donutpriorityqueue.util;

import com.demo.donutpriorityqueue.entity.Order;
import com.demo.donutpriorityqueue.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class OrderQueueUtil {
    @Autowired
    OrderRepository orderRepository;

    /**
     * get orders made by premium customers
     * @return list of order entity
     */
    public List<Order> getPremiumOrders() {
        return orderRepository.findByCustomerIdLessThan((short) 1000);
    }

    /**
     * get the order queue sorted by priority and timestamp
     * @return list of order entity
     */
    public List<Order> getOrderQueue() {
        List<Order> premiumOrders = this.getPremiumOrders();
        List<Order> normalOrders = orderRepository.findAll()
                .stream()
                .filter(Predicate.not(premiumOrders::contains))
                .collect(Collectors.toList());

        premiumOrders.sort(((o1, o2) -> (int) (o1.getTimestamp() - (o2.getTimestamp()))));
        normalOrders.sort(((o1, o2) -> (int) (o1.getTimestamp() - (o2.getTimestamp()))));

        return Stream.concat(premiumOrders.stream(), normalOrders.stream()).toList();
    }

    /**
     * compute the wait time for the current order
     * @param previousOrders collections of previous orders
     * @param currentOrder current order entity
     * @return wait time for the current order
     */
    public int computeWaitTime(Collection<Order> previousOrders, Order currentOrder) {
        List<Order> sortedOrders = previousOrders
                .stream()
                .sorted((o1, o2) -> (int) (o1.getTimestamp() - (o2.getTimestamp())))
                .toList();

        int orderBatchNumber = 1;
        int currentCartCapacity = Constant.CART_CAPACITY;
        for (Order o : sortedOrders) {
            if (o.getQuantity() > currentCartCapacity) {
                orderBatchNumber ++;
                currentCartCapacity = Constant.CART_CAPACITY - o.getQuantity();
            } else {
                currentCartCapacity -= o.getQuantity();
            }
        }

        return Constant.DELIVERY_TIME * (currentOrder.getQuantity() > currentCartCapacity ?
                ++ orderBatchNumber : orderBatchNumber);

    }
}
