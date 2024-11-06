package org.hanghae99.orderservice.controller;

import com.jh.common.utils.ParseRequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hanghae99.orderservice.dto.ApiResponseDto;
import org.hanghae99.orderservice.dto.WishListRequestDto;
import org.hanghae99.orderservice.dto.WishListResponseDto;
import org.hanghae99.orderservice.entity.WishList;
import org.hanghae99.orderservice.service.WishListService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishList")
public class WishListController {
    private final WishListService wishListService;

    @Operation(summary = "위시 리스트에 상품 담기")
    @Transactional
    @PostMapping("/{productNo}")
    public ResponseEntity<ApiResponseDto> takeItem(@PathVariable Long productNo, @RequestBody WishListRequestDto wishListRequestDto, HttpServletRequest request) {
        Long userId = new ParseRequestUtil().extractUserIdFromRequest(request);
        return wishListService.takeItem(productNo, wishListRequestDto, userId);
    }

    @Operation(summary = "위시 리스트 내부 조회")
    @GetMapping("/list")
    public ResponseEntity<List<WishListResponseDto>> getWishListItems(HttpServletRequest request) {
        Long userId = new ParseRequestUtil().extractUserIdFromRequest(request);
        return wishListService.getWishListItems(userId);
    }

    @Operation(summary = "위시 리스트 내부 상품 개수 수정")
    @Transactional
    @PutMapping("/{wishListItemNo}")
    public ResponseEntity<WishListResponseDto> updateWishListItemQuantity(@PathVariable Long wishListItemNo, @RequestBody WishListRequestDto wishListRequestDto) {
        return wishListService.updateWishListItemQuantity(wishListItemNo, wishListRequestDto);
    }

    @Operation(summary = "위시 리스트 상품 취소")
    @Transactional
    @DeleteMapping("/{wishListItemNo}")
    public ResponseEntity<ApiResponseDto> cancelItem(@PathVariable Long wishListItemNo) {
        return wishListService.cancelItem(wishListItemNo);
    }

    @Operation(summary = "Eureka 위시 리스트 목록 조회")
    @GetMapping("/adapt/wishListList/{productId}")
    public List<WishList> adaptGetWishListList(@PathVariable Long productId) {
        return wishListService.adaptGetWishListList(productId);
    }
}
