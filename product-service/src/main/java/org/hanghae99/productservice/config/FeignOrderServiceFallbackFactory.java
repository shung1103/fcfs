package org.hanghae99.productservice.config;

import org.hanghae99.productservice.service.FeignOrderService;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FeignOrderServiceFallbackFactory implements FallbackFactory<FeignOrderService> {
    @Override
    public FeignOrderService create(Throwable cause) {
        return productId -> List.of();
    }
}
