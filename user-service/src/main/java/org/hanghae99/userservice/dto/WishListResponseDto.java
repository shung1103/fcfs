package org.hanghae99.userservice.dto;

import lombok.Getter;
import org.hanghae99.userservice.entity.WishList;

@Getter
public class WishListResponseDto {
    private Long userId;
    private Long productId;
    private Integer quantity;

    public WishListResponseDto(WishList wishList) {
        this.userId = wishList.getWishUserId();
        this.productId = wishList.getWishProductId();
        this.quantity = wishList.getWishQuantity();
    }
}
