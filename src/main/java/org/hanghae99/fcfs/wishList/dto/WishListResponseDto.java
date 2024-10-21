package org.hanghae99.fcfs.wishList.dto;

import lombok.Getter;
import org.hanghae99.fcfs.wishList.entity.WishList;

@Getter
public class WishListResponseDto {
    private String username;
    private String title;
    private Integer quantity;

    public WishListResponseDto(WishList wishList) {
        this.username = wishList.getWishUserName();
        this.title = wishList.getWishProductTitle();
        this.quantity = wishList.getWishQuantity();
    }
}
