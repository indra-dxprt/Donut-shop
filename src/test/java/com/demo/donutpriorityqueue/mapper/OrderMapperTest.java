package com.demo.donutpriorityqueue.mapper;

import com.demo.donutpriorityqueue.repository.OrderRepository;
import com.demo.donutpriorityqueue.dto.OrderResponse;
import com.demo.donutpriorityqueue.entity.Order;
import com.demo.donutpriorityqueue.util.OrderQueueUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderMapperTest {
    @InjectMocks
    private OrderMapperImpl orderMapper;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderQueueUtil orderQueueUtil;

    OrderMapperImpl spyOrderMapper;

    @BeforeEach
    public void setup() {
        spyOrderMapper = Mockito.spy(orderMapper);
    }

    @Test
    public void testPremiumOrderToOrderResponse() {
        // given
        Order order = new Order((short) 1, (short) 5);

        // when
        spyOrderMapper.orderToOrderResponse(order);

        // then
        verify(spyOrderMapper, times(1)).getQueuingStatus(order);

    }

    @Test
    public void testNormalOrderToOrderResponse() {
        // given
        Order order = new Order((short) 1000, (short) 5);

        // when
        spyOrderMapper.orderToOrderResponse(order);

        // then
        verify(spyOrderMapper, times(1)).getQueuingStatus(order);

    }

    @Test
    public void testOrderResponseListToOrderQueueDto() {
        // given
        OrderResponse orderResponse = new OrderResponse((short) 1, (short) 5, 1, 5);

        // when
        spyOrderMapper.orderResponseListToOrderQueueDto(List.of(orderResponse), Pageable.ofSize(5).first());

        // then
        verify(spyOrderMapper, times(1)).getOrderPage(List.of(orderResponse),
                Pageable.ofSize(5).first());

    }
}
