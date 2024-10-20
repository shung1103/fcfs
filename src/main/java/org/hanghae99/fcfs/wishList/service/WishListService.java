package org.hanghae99.fcfs.wishList.service;

import lombok.RequiredArgsConstructor;
import org.hanghae99.fcfs.common.dto.ApiResponseDto;
import org.hanghae99.fcfs.product.entity.Product;
import org.hanghae99.fcfs.product.repository.ProductRepository;
import org.hanghae99.fcfs.user.entity.User;
import org.hanghae99.fcfs.user.repository.UserRepository;
import org.hanghae99.fcfs.wishList.dto.WishListRequestDto;
import org.hanghae99.fcfs.wishList.dto.WishListResponseDto;
import org.hanghae99.fcfs.wishList.entity.WishList;
import org.hanghae99.fcfs.wishList.repository.WishListRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishListService {
    private final UserRepository userRepository;
    private final WishListRepository wishListRepository;
    private final ProductRepository productRepository;

    public ResponseEntity<ApiResponseDto> takeItem(Long productNo, WishListRequestDto wishListRequestDto, Long userId) {
        Product product = productRepository.findById(productNo).orElseThrow(() -> new NullPointerException("Product not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NullPointerException("User with id " + userId + " not found"));

        if (wishListRepository.existsByWishUserNameAndWishProductTitle(user.getUsername(), product.getTitle())) {
            WishList wishList = wishListRepository.findByWishUserNameAndWishProductTitle(user.getUsername(), product.getTitle());
            Integer quantity = wishList.getWishQuantity() + wishListRequestDto.getQuantity();
            wishList.updateQuantity(quantity);
            wishListRepository.saveAndFlush(wishList);
        } else {
            WishList wishList = new WishList(user.getUsername(), product.getTitle(), wishListRequestDto.getQuantity());
            wishListRepository.save(wishList);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto("위시 리스트에 상품을 등록하였습니다.", HttpStatus.CREATED.value()));
    }

    public ResponseEntity<List<WishListResponseDto>> getWishListItems(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NullPointerException("User with id " + userId + " not found"));
        List<WishList> wishLists = wishListRepository.findAllByWishUserName(user.getUsername());
        List<WishListResponseDto> wishListResponseDtos = new ArrayList<>();
        for (WishList wishList : wishLists) {
            wishListResponseDtos.add(new WishListResponseDto(wishList));
        }
        return ResponseEntity.status(HttpStatus.OK).body(wishListResponseDtos);
    }

    public ResponseEntity<WishListResponseDto> updateWishListItemQuantity(Long wishListItemNo, WishListRequestDto wishListRequestDto, Long userId) {
        WishList wishList = wishListRepository.findById(wishListItemNo).orElseThrow(() -> new NullPointerException("WishList with id " + wishListItemNo + " not found"));
        wishList.updateQuantity(wishListRequestDto.getQuantity());
        wishListRepository.saveAndFlush(wishList);
        return ResponseEntity.status(HttpStatus.OK).body(new WishListResponseDto(wishList));
    }

    public ResponseEntity<ApiResponseDto> cancelItem(Long wishListItemNo, Long userId) {
        WishList wishList = wishListRepository.findById(wishListItemNo).orElseThrow(() -> new NullPointerException("WishList with id " + wishListItemNo + " not found"));
        wishListRepository.delete(wishList);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto("상품을 위시 리스트에서 제거했습니다.", HttpStatus.OK.value()));
    }
}
