package org.hanghae99.fcfs.product.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ProductRequestDto {
    @NotBlank
    private String productName;

    @NotBlank
    private Long productPrice;

    @NotBlank
    private String productIntro;

    @NotBlank
    private Long productQuantity;
}
