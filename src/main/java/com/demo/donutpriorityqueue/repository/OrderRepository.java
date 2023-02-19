package com.demo.donutpriorityqueue.repository;

import com.demo.donutpriorityqueue.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Delete an order by customer id
     * @param customerId id of a customer
     */
    void deleteByCustomerId(short customerId);

    /**
     * Find an order by customer id
     * @param customerId id of a customer
     * @return order entity
     */
    Order findByCustomerId(short customerId);

    /**
     * Find an order with customer id less than
     * @param customerId id of a customer
     * @return list of order entity
     */
    List<Order> findByCustomerIdLessThan(short customerId);

    /**
     * Find an order with timestamp less than
     * @param timestamp timestamp in seconds
     * @return list of order entity
     */
    List<Order> findByTimestampLessThan(long timestamp);
}
