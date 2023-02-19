package com.demo.donutpriorityqueue.mapper;

import com.demo.donutpriorityqueue.dto.OrderQueueDto;
import com.demo.donutpriorityqueue.dto.OrderResponse;
import com.demo.donutpriorityqueue.entity.Order;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-02-14T21:10:16+0100",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 18.0.2.1 (Eclipse Adoptium)"
)
@Component
public class OrderMapperImpl extends OrderMapper {

    @Override
    public OrderResponse orderToOrderResponse(Order order) {
        OrderResponse target = getQueuingStatus( order );
        if ( target != null ) {
            return target;
        }

        if ( order == null ) {
            return null;
        }

        Short customerId = null;
        Short quantity = null;

        customerId = order.getCustomerId();
        quantity = order.getQuantity();

        int position = 0;
        int waitTime = 0;

        OrderResponse orderResponse = new OrderResponse( customerId, quantity, position, waitTime );

        return orderResponse;
    }

    @Override
    public OrderQueueDto orderResponseListToOrderQueueDto(List<OrderResponse> queuedOrderResponses, Pageable pageable) {
        OrderQueueDto target = getOrderPage( queuedOrderResponses, pageable );
        if ( target != null ) {
            return target;
        }

        if ( queuedOrderResponses == null && pageable == null ) {
            return null;
        }

        Page<OrderResponse> orderResponsePage = null;

        OrderQueueDto orderQueueDto = new OrderQueueDto( orderResponsePage );

        return orderQueueDto;
    }
}
