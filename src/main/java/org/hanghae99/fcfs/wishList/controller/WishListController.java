package org.hanghae99.fcfs.wishList.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.hanghae99.fcfs.common.dto.ApiResponseDto;
import org.hanghae99.fcfs.common.security.UserDetailsImpl;
import org.hanghae99.fcfs.wishList.dto.WishListItemRequestDto;
import org.hanghae99.fcfs.wishList.dto.WishListResponseDto;
import org.hanghae99.fcfs.wishList.service.WishListService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WishListController {
    private final WishListService wishListService;

    @Operation(summary = "위시 리스트 내부 조회")
    @GetMapping("/wishList")
    public ResponseEntity<WishListResponseDto> getWishListItems(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return wishListService.getWishListItems(userDetails.getUser());
    }

    @Operation(summary = "위시 리스트에 상품 담기")
    @Transactional
    @PostMapping("/wishList/{productNo}")
    public ResponseEntity<ApiResponseDto> takeItem(@PathVariable Long productNo, @RequestBody WishListItemRequestDto wishListItemRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return wishListService.takeItem(productNo, wishListItemRequestDto, userDetails.getUser());
    }

    @Operation(summary = "위시 리스트 내부 상품 개수 수정")
    @Transactional
    @PutMapping("/wishListItem/{wishListItemNo}")
    public ResponseEntity<WishListResponseDto> updateWishListItemQuantity(@PathVariable Long wishListItemNo, @RequestBody WishListItemRequestDto wishListItemRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return wishListService.updateWishListItemQuantity(wishListItemNo, wishListItemRequestDto, userDetails.getUser());
    }

    @Operation(summary = "위시 리스트 상품 취소")
    @Transactional
    @DeleteMapping("/wishList/{wishListItemNo}")
    public ResponseEntity<ApiResponseDto> cancelItem(@PathVariable Long wishListItemNo, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return wishListService.cancelItem(wishListItemNo, userDetails.getUser());
    }
}
