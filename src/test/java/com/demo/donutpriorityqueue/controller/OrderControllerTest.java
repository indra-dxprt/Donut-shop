package com.demo.donutpriorityqueue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.demo.donutpriorityqueue.dto.CreateOrderRequest;
import com.demo.donutpriorityqueue.service.OrderServiceImpl;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.demo.donutpriorityqueue.controller.OrderController.BASE_ORDER_URL;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderServiceImpl orderService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void testCreateOrder() throws Exception {
        // given
        CreateOrderRequest createOrderRequest = new CreateOrderRequest((short) 1, (short) 10);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_ORDER_URL)
                        .content(objectMapper.writeValueAsString(createOrderRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        verify(orderService, times(1)).createOrder(createOrderRequest);
    }

    @Test
    public void testCreateDuplicateOrders() throws Exception {
        // given
        CreateOrderRequest createOrderRequest = new CreateOrderRequest((short) 1, (short) 10);
        given(orderService.createOrder(createOrderRequest)).willThrow(
                new EntityExistsException("Customer 1 has an existing order!"));

        // when/then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_ORDER_URL)
                        .content(objectMapper.writeValueAsString(createOrderRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        verify(orderService, times(1)).createOrder(createOrderRequest);
    }

    @Test
    public void testCreateOrdersExceedsMaximumCapacity() throws Exception {
        // given
        CreateOrderRequest createOrderRequest = new CreateOrderRequest((short) 1, (short) 60);
        given(orderService.createOrder(createOrderRequest)).willThrow(
                new IllegalStateException("The order exceeds the maximum quantity available to buy: 50"));

        // when/then
        mockMvc.perform(MockMvcRequestBuilders
                        .post(BASE_ORDER_URL)
                        .content(objectMapper.writeValueAsString(createOrderRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(orderService, times(1)).createOrder(createOrderRequest);
    }

    @Test
    public void testCancelOrder() throws Exception {
        // given
        short customerId = 1;

        // when/then
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(String.format("%s/%d", BASE_ORDER_URL, customerId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(orderService, times(1)).cancelOrder(customerId);

    }

    @Test
    public void testCancelNotExistingOrder() throws Exception {
        // given
        short customerId = 1;
        doThrow(new NoResultException("Customer 1 had not placed an order!"))
                .when(orderService)
                .cancelOrder(customerId);

        // when/then
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(String.format("%s/%d", BASE_ORDER_URL, customerId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(orderService, times(1)).cancelOrder(customerId);

    }

    @Test
    public void testViewOrder() throws Exception {
        // given
        short customerId = 1;

        // when/then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format("%s/%d", BASE_ORDER_URL, customerId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(orderService, times(1)).viewOrder(customerId);

    }

    @Test
    public void testViewNotExistingOrder() throws Exception {
        // given
        short customerId = 1;
        given(orderService.viewOrder(customerId)).willThrow(new NoResultException("Customer 1 had not placed an order!"));

        // when/then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format("%s/%d", BASE_ORDER_URL, customerId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(orderService, times(1)).viewOrder(customerId);

    }

    @Test
    public void testViewQueue() throws Exception {
        // given
        short pageNumber = 0;
        short pageSize = 2;

        // when/then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format("%s/queue?page=%d&size=%d", BASE_ORDER_URL, pageNumber, pageSize))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(orderService, times(1)).viewQueue(Pageable.ofSize(2).first());

    }

    @Test
    public void testViewNextDelivery() throws Exception {
        // when/then
        mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format("%s/%s", BASE_ORDER_URL, "next_delivery"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(orderService, times(1)).viewNextDelivery();

    }
}