package com.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer order_state_id;

    private String order_state_name;

}
