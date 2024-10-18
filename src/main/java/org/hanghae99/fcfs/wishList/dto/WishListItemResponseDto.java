package org.hanghae99.fcfs.wishList.dto;

import lombok.Getter;
import org.hanghae99.fcfs.wishList.entity.WishListItem;

@Getter
public class WishListItemResponseDto {
    private Long id;
    private Long productId;
    private String title;
    private String price;
    private Integer quantity;

    public WishListItemResponseDto(WishListItem wishListItem) {
        this.id = wishListItem.getId();
        this.productId = wishListItem.getProduct().getId();
        this.title = wishListItem.getProduct().getTitle();
        this.price = wishListItem.getProduct().getPrice().toString();
        this.quantity = wishListItem.getQuantity();
    }
}
