package org.hanghae99.userservice.config;

import org.hanghae99.userservice.dto.OrderResponseDto;
import org.hanghae99.userservice.dto.WishListResponseDto;
import org.hanghae99.userservice.service.FeignOrderService;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FeignOrderServiceFallbackFactory implements FallbackFactory<FeignOrderService> {
    @Override
    public FeignOrderService create(Throwable cause) {
        return new FeignOrderService() {
            @Override
            public List<OrderResponseDto> adaptGetOrders(Long userId) {
                return List.of();
            }

            @Override
            public List<WishListResponseDto> adaptGetWishLists(Long userID) {
                return List.of();
            }
        };
    }
}
