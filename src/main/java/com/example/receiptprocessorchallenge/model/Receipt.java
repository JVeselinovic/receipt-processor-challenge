package com.example.receiptprocessorchallenge.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Receipt pojo class that use lombok annotation
 * and validation annotation based on the OpenAPI
 * specification in the api.yaml
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Receipt implements Serializable {
    //all Receipt fields are required
    @NotBlank
    private String retailer;
    @NotBlank
    private String purchaseDate;
    @NotBlank
    private String purchaseTime;
    @Valid
    @NotNull
    private List<Item> items;
    @NotBlank
    @Pattern(regexp = "^\\d+\\.\\d{2}$", message = "Total amount must be in the format '0.00'")
    private String total;
}
