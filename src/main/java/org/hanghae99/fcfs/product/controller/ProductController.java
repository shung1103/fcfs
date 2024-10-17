package org.hanghae99.fcfs.product.controller;

import lombok.RequiredArgsConstructor;
import org.hanghae99.fcfs.common.dto.ApiResponseDto;
import org.hanghae99.fcfs.product.dto.ProductRequestDto;
import org.hanghae99.fcfs.product.dto.ProductResponseDto;
import org.hanghae99.fcfs.product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;

    @Secured("ROLE_ADMIN")
    @PostMapping("/product")
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto productRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productRequestDto));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDto>> getProducts() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProducts());
    }

    @GetMapping("/product/{productNo}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long productNo) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProduct(productNo));
    }

    @Transactional
    @Secured("ROLE_ADMIN")
    @PutMapping("/product/{productNo}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long productNo, @RequestBody ProductRequestDto productRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(productNo, productRequestDto));
    }

    @Transactional
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/product/{productNo}")
    public ResponseEntity<ApiResponseDto> deleteProduct(@PathVariable Long productNo) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.deleteProduct(productNo));
    }
}
