package org.hanghae99.fcfs.wishList.service;

import lombok.RequiredArgsConstructor;
import org.hanghae99.fcfs.common.dto.ApiResponseDto;
import org.hanghae99.fcfs.product.entity.Product;
import org.hanghae99.fcfs.product.repository.ProductRepository;
import org.hanghae99.fcfs.user.entity.User;
import org.hanghae99.fcfs.wishList.dto.WishListItemRequestDto;
import org.hanghae99.fcfs.wishList.dto.WishListResponseDto;
import org.hanghae99.fcfs.wishList.entity.WishList;
import org.hanghae99.fcfs.wishList.entity.WishListItem;
import org.hanghae99.fcfs.wishList.repository.WishListItemRepository;
import org.hanghae99.fcfs.wishList.repository.WishListRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishListService {
    private final WishListRepository wishListRepository;
    private final WishListItemRepository wishListItemRepository;
    private final ProductRepository productRepository;

    public WishList createWishList(User user) {
        if (!wishListRepository.existsById(user.getId())) {
            WishList wishList = new WishList(user);
            wishListRepository.save(wishList);
            return wishList;
        } else {
            return wishListRepository.findByUser(user);
        }
    }

    public ResponseEntity<WishListResponseDto> getWishListItems(User user) {
        WishList wishList = createWishList(user);
        List<WishListItem> wishListItemList = wishList.getWishListItemList();

        long totalPrice = 0L;
        for (WishListItem wishListItem : wishListItemList) totalPrice += wishListItem.getProduct().getPrice() * wishListItem.getWishListItemQuantity();

        return ResponseEntity.status(HttpStatus.OK).body(new WishListResponseDto(user.getUsername(), wishList, totalPrice));
    }

    public ResponseEntity<ApiResponseDto> takeItem(Long productNo, WishListItemRequestDto wishListItemRequestDto, User user) {
        Product product = productRepository.findById(productNo).orElseThrow(() -> new NullPointerException("Product not found"));
        WishList wishList = createWishList(user);

        if (wishListItemRepository.existsByWishListIdAndProductId(wishList.getId(), product.getId())) {
            WishListItem wishListItem = wishListItemRepository.findByWishListIdAndProductId(wishList.getId(), product.getId());
            wishListItem.updateQuantity(wishListItem.getWishListItemQuantity() + wishListItemRequestDto.getQuantity());
            wishListItemRepository.save(wishListItem);
        } else {
            WishListItem wishListItem = new WishListItem(wishListItemRequestDto.getQuantity(), wishList, product);
            wishListItemRepository.save(wishListItem);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto("위시 리스트에 상품을 등록하였습니다.", HttpStatus.CREATED.value()));
    }

    public ResponseEntity<WishListResponseDto> updateWishListItemQuantity(Long wishListItemNo, WishListItemRequestDto wishListItemRequestDto, User user) {
        WishListItem wishListItem = wishListItemRepository.findByIdAndWishListId(wishListItemNo, user.getWishList().getId()).orElseThrow(
                () -> new NullPointerException("해당 번호의 아이템이 귀하 유저의 위시 리스트에 존재하지 않습니다."));
        wishListItem.updateQuantity(wishListItemRequestDto.getQuantity());
        wishListItemRepository.save(wishListItem);
        return getWishListItems(user);
    }

    public ResponseEntity<ApiResponseDto> cancelItem(Long wishListItemNo, User user) {
        WishListItem wishListItem = wishListItemRepository.findByIdAndWishListId(wishListItemNo, user.getWishList().getId()).orElseThrow(
                () -> new NullPointerException("해당 번호의 아이템이 귀하 유저의 위시 리스트에 존재하지 않습니다."));
        wishListItemRepository.delete(wishListItem);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto("상품을 위시 리스트에서 제거했습니다.", HttpStatus.OK.value()));
    }
}
