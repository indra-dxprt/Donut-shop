package com.demo.donutpriorityqueue.dto;

import org.springframework.data.domain.Page;

public record OrderQueueDto(Page<OrderResponse> orderResponsePage) {
}
