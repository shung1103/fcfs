package org.hanghae99.fcfs.wishList.dto;

import lombok.Getter;
import org.hanghae99.fcfs.wishList.entity.WishList;

import java.util.List;

@Getter
public class WishListResponseDto {
    private String username;
    private Long wishlistId;
    private Long totalPrice;
    private List<WishListItemResponseDto> wishListItemList;

    public WishListResponseDto(String username, WishList wishList, Long totalPrice) {
        this.username = username;
        this.wishlistId = wishList.getId();
        this.totalPrice = totalPrice;
        this.wishListItemList = wishList.getWishListItemList().stream().map(WishListItemResponseDto::new).toList();
    }
}
