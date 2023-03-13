package com.example.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrderStateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="order_state_id")
    private Integer order_state_id;
    @Column(name="order_state_name")
    private String order_state_name;
}
