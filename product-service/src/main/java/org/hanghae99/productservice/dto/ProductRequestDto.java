package org.hanghae99.productservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ProductRequestDto {
    @NotBlank
    private String category;

    @NotBlank
    private String title;

    @NotBlank
    private Long price;

    @NotBlank
    private String intro;

    @NotBlank
    private Integer stock;
}
