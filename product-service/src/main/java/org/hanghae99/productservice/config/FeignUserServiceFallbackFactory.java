package org.hanghae99.productservice.config;

import org.hanghae99.productservice.service.FeignUserService;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
public class FeignUserServiceFallbackFactory implements FallbackFactory<FeignUserService> {
    @Override
    public FeignUserService create(Throwable cause) {
        return wishLists -> new LinkedList<>();
    }
}
