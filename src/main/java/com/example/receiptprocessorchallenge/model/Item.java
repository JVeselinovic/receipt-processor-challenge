package com.example.receiptprocessorchallenge.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Item implements Serializable {
    @NotBlank
    @Pattern(regexp = "^[\\w\\s\\-]+$", message = "Short description must only contain alphanumeric characters, spaces, and hyphens")
    private String shortDescription;
    @NotBlank
    @Pattern(regexp = "^\\d+\\.\\d{2}$", message = "Item price must be in the format '0.00'")
    private String price;
}
