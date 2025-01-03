package org.hanghae99.productservice.client;

import org.hanghae99.productservice.dto.User;
import org.hanghae99.productservice.dto.WishList;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
public class FeignOrderServiceFallbackFactory implements FallbackFactory<FeignOrderService> {
    @Override
    public FeignOrderService create(Throwable cause) {
        return new FeignOrderService() {
            @Override
            public List<WishList> adaptGetWishListList(Long productId) {
                return List.of();
            }

            @Override
            public Queue<User> adaptGetUserQueue(List<Long> wishUserIdList) {
                return new LinkedList<>();
            }
        };
    }
}
