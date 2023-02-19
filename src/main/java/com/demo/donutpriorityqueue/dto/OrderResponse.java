package com.demo.donutpriorityqueue.dto;


public record OrderResponse(Short customerId, Short quantity, int position, int waitTime) {
}
