package com.demo.donutpriorityqueue.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "donut_order")
public class Order {
    @Id
    @GeneratedValue
    private final Long id = 1L;
    @Column(unique = true)
    private Short customerId = 0;
    private Short quantity = 0;
    private final Long timestamp = Instant.now().getEpochSecond();

}

