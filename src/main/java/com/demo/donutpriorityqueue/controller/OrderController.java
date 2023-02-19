package com.demo.donutpriorityqueue.controller;

import com.demo.donutpriorityqueue.service.OrderService;
import com.demo.donutpriorityqueue.dto.CreateOrderRequest;
import com.demo.donutpriorityqueue.dto.OrderQueueDto;
import com.demo.donutpriorityqueue.dto.OrderResponse;
import com.demo.donutpriorityqueue.dto.UpcomingDeliveryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import static com.demo.donutpriorityqueue.controller.OrderController.BASE_ORDER_URL;

@RestController
@RequestMapping(BASE_ORDER_URL)
public class OrderController {
    static final String BASE_ORDER_URL = "/api/order";

    @Autowired
    private OrderService orderService;

    /**
     * Route the POST request to create order
     * @param createOrderRequest the dto for creating an order
     * @return the response dto for an order
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        return orderService.createOrder(createOrderRequest);
    }

    /**
     * Route the DELETE request to cancel order
     * @param customerId id of a customer
     */
    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable Short customerId) {
        orderService.cancelOrder(customerId);
    }

    /**
     * Route the GET request with path variable customerId to view order
     * @param customerId id of a customer
     * @return the response dto for an order
     */
    @GetMapping("/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse viewOrder(@PathVariable Short customerId) {
        return orderService.viewOrder(customerId);
    }

    /**
     * Route the GET request at endpoint "queue" to view the order queue
     * @param pageable attributes for defining a page
     * @return the dto for the order queue
     */
    @GetMapping("/queue")
    @ResponseStatus(HttpStatus.OK)
    public OrderQueueDto viewQueue(Pageable pageable) {
        return orderService.viewQueue(pageable);
    }

    /**
     * Route the GET request at endpoint "next_delivery" to view the next delivery
     * @return the dto for the next delivery
     */
    @GetMapping("/next_delivery")
    @ResponseStatus(HttpStatus.OK)
    public UpcomingDeliveryDto viewNextDelivery() {
        return orderService.viewNextDelivery();
    }
}
