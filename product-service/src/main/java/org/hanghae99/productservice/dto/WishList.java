package org.hanghae99.productservice.dto;

import lombok.Getter;

@Getter
public class WishList {
    private Long id;
    private Long wishUserId;
    private Long wishProductId;
    private Integer wishQuantity;
}
