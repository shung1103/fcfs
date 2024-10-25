package org.hanghae99.orderservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.hanghae99.gatewayservice.security.UserDetailsImpl;
import org.hanghae99.orderservice.dto.ApiResponseDto;
import org.hanghae99.orderservice.dto.WishListRequestDto;
import org.hanghae99.orderservice.dto.WishListResponseDto;
import org.hanghae99.orderservice.service.WishListService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WishListController {
    private final WishListService wishListService;

    @Operation(summary = "위시 리스트에 상품 담기")
    @Transactional
    @PostMapping("/wishList/{productNo}")
    public ResponseEntity<ApiResponseDto> takeItem(@PathVariable Long productNo, @RequestBody WishListRequestDto wishListRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return wishListService.takeItem(productNo, wishListRequestDto, userDetails.getUser().getId());
    }

    @Operation(summary = "위시 리스트 내부 조회")
    @GetMapping("/wishList")
    public ResponseEntity<List<WishListResponseDto>> getWishListItems(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return wishListService.getWishListItems(userDetails.getUser().getId());
    }

    @Operation(summary = "위시 리스트 내부 상품 개수 수정")
    @Transactional
    @PutMapping("/wishListItem/{wishListItemNo}")
    public ResponseEntity<WishListResponseDto> updateWishListItemQuantity(@PathVariable Long wishListItemNo, @RequestBody WishListRequestDto wishListRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return wishListService.updateWishListItemQuantity(wishListItemNo, wishListRequestDto, userDetails.getUser().getId());
    }

    @Operation(summary = "위시 리스트 상품 취소")
    @Transactional
    @DeleteMapping("/wishList/{wishListItemNo}")
    public ResponseEntity<ApiResponseDto> cancelItem(@PathVariable Long wishListItemNo, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return wishListService.cancelItem(wishListItemNo, userDetails.getUser().getId());
    }
}
