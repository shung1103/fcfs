package org.hanghae99.productservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WishList {
    private Long id;
    private Long wishUserId;
    private Long wishProductId;
    private Integer wishQuantity;
}
