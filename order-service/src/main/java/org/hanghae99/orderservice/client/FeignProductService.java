package org.hanghae99.orderservice.client;

import org.hanghae99.orderservice.dto.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "gateway-service", fallbackFactory = FeignProductServiceFallbackFactory.class)
public interface FeignProductService {
    @GetMapping("/api/product/adapt/{productId}")
    Product getProduct(@PathVariable("productId") Long productId);

    @PutMapping("/api/product/adapt/{productId}/re-stock")
    void reStockProduct(@PathVariable("productId") Long productId, @RequestParam("quantity") Integer quantity);

    @GetMapping("/api/product/adapt/product-list")
    List<Product> getAdaptProductList(@RequestParam("productIds") List<Long> productIds);
}
