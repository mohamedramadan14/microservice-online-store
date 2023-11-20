package com.mo.estore.productservice.command.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CreateProductModel {
    @NotBlank(message = "Product title is required")
    private String title;

    @Min(value = 1, message = "Product price must be greater than 0")
    private BigDecimal price;

    @Min(value = 1, message = "Product quantity must be greater than 0")
    private int qty;
}
