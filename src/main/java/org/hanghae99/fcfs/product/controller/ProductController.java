package org.hanghae99.fcfs.product.controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Transactional
    @Operation(summary = "상품 생성", description = "관리자 제한")
    @Secured("ROLE_ADMIN")
    @PostMapping("/product")
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto productRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productRequestDto));
    }

    @Operation(summary = "전체 상품 목록 조회", description = "로그인 없이도 이용할 수 있습니다.")
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDto>> getProducts() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProducts());
    }

    @Operation(summary = "상품 상세 조회")
    @GetMapping("/product/{productNo}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long productNo) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProduct(productNo));
    }

    @Operation(summary = "상품 재고 확인")
    @GetMapping("/product/{productNo}/stock")
    public Long getProductStock(@PathVariable Long productNo) {
        return productService.getProductStock(productNo);
    }

    @Transactional
    @Operation(summary = "상품 정보 수정", description = "관리자 제한")
    @Secured("ROLE_ADMIN")
    @PutMapping("/product/{productNo}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long productNo, @RequestBody ProductRequestDto productRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(productNo, productRequestDto));
    }

    @Transactional
    @Operation(summary = "상품 삭제", description = "관리자 제한")
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/product/{productNo}")
    public ResponseEntity<ApiResponseDto> deleteProduct(@PathVariable Long productNo) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.deleteProduct(productNo));
    }
}
