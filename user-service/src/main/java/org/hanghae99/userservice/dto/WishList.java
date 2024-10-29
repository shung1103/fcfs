package org.hanghae99.userservice.dto;

import lombok.Getter;

@Getter
public class WishList {
    private Long id;
    private Long wishUserId;
    private Long wishProductId;
    private Integer wishQuantity;
}
