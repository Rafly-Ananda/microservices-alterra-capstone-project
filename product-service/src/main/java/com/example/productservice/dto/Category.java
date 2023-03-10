package com.example.productservice.dto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Category {
    private Long category_id;
    private String name;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;
}
