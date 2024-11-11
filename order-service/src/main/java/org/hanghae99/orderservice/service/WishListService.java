package org.hanghae99.orderservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hanghae99.orderservice.client.FeignProductService;
import org.hanghae99.orderservice.dto.ApiResponseDto;
import org.hanghae99.orderservice.dto.Product;
import org.hanghae99.orderservice.dto.WishListRequestDto;
import org.hanghae99.orderservice.dto.WishListResponseDto;
import org.hanghae99.orderservice.entity.WishList;
import org.hanghae99.orderservice.repository.WishListRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishListService {
    private final WishListRepository wishListRepository;
    private final FeignProductService feignProductService;

    @Transactional
    public ResponseEntity<ApiResponseDto> takeItem(Long productNo, WishListRequestDto wishListRequestDto, Long userId) {
        Product product = feignProductService.getProduct(productNo);

        if (wishListRepository.existsByWishUserIdAndWishProductId(userId, product.getId())) {
            WishList wishList = wishListRepository.findByWishUserIdAndWishProductId(userId, product.getId());
            Integer quantity = wishList.getWishQuantity() + wishListRequestDto.getQuantity();
            wishList.updateQuantity(quantity);
            wishListRepository.saveAndFlush(wishList);
        } else {
            WishList wishList = new WishList(userId, product.getId(), wishListRequestDto.getQuantity());
            wishListRepository.save(wishList);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto("위시 리스트에 상품을 등록하였습니다.", HttpStatus.CREATED.value()));
    }

    public ResponseEntity<List<WishListResponseDto>> getWishListItems(Long userId) {
        List<WishList> wishLists = wishListRepository.findAllByWishUserId(userId);
        List<WishListResponseDto> wishListResponseDtos = new ArrayList<>();
        for (WishList wishList : wishLists) {
            wishListResponseDtos.add(new WishListResponseDto(wishList));
        }
        return ResponseEntity.status(HttpStatus.OK).body(wishListResponseDtos);
    }

    @Transactional
    public ResponseEntity<WishListResponseDto> updateWishListItemQuantity(Long wishListItemNo, WishListRequestDto wishListRequestDto) {
        WishList wishList = wishListRepository.findById(wishListItemNo).orElseThrow(() -> new NullPointerException("WishList with id " + wishListItemNo + " not found"));
        wishList.updateQuantity(wishListRequestDto.getQuantity());
        wishListRepository.saveAndFlush(wishList);
        return ResponseEntity.status(HttpStatus.OK).body(new WishListResponseDto(wishList));
    }

    @Transactional
    public ResponseEntity<ApiResponseDto> cancelItem(Long wishListItemNo) {
        WishList wishList = wishListRepository.findById(wishListItemNo).orElseThrow(() -> new NullPointerException("WishList with id " + wishListItemNo + " not found"));
        wishListRepository.delete(wishList);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto("상품을 위시 리스트에서 제거했습니다.", HttpStatus.OK.value()));
    }

    public List<WishList> adaptGetWishListList(Long productId) {
        Product product = feignProductService.getProduct(productId);
        return wishListRepository.findAllByWishProductId(product.getId());
    }
}
