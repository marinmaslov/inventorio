package com.inventorio.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 10, nullable = false)
    @Size(min = 10, max = 10)
    @Pattern(regexp = "^[A-Za-z0-9]{10}$", message = "Code must be 10 alphanumeric characters")
    private String code;

    @NotBlank
    private String name;

    @DecimalMin("0.0")
    private BigDecimal priceEur;

    @DecimalMin("0.0")
    private BigDecimal priceUsd;

    private boolean isAvailable;
}
