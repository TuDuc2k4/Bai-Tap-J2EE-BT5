package com.example.bai5_qlsp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String name;
    private String image;
    private Long price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
