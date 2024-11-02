package org.hanghae99.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hanghae99.productservice.dto.*;
import org.hanghae99.productservice.entity.Product;
import org.hanghae99.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;

    @Transactional
    @Operation(summary = "상품 생성", description = "관리자 제한")
    @PostMapping("/create")
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto productRequestDto, HttpServletRequest request) {
        if (!request.getHeader("x-claim-role").equals("ADMIN")) throw new IllegalArgumentException("관리자 권한이 아닙니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productRequestDto));
    }

    @Operation(summary = "전체 상품 목록 조회", description = "로그인 없이도 이용할 수 있습니다.")
    @GetMapping("/list")
    public ResponseEntity<List<ProductResponseDto>> getProducts() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProducts());
    }

    @Operation(summary = "상품 상세 조회", description = "로그인 없이도 이용할 수 있습니다.")
    @GetMapping("/search/{productNo}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long productNo) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProduct(productNo));
    }

    @Operation(summary = "상품 재고 확인")
    @GetMapping("/{productNo}/stock")
    public Integer getProductStock(@PathVariable Long productNo) {
        return productService.getProductStock(productNo);
    }

    @Operation(summary = "상품 재입고")
    @PutMapping("/{productNo}/re-stock")
    public ResponseEntity<ProductResponseDto> reStockProduct(@PathVariable Long productNo, @RequestBody ReStockRequestDto reStockRequestDto, HttpServletRequest request) {
        if (!request.getHeader("x-claim-role").equals("ADMIN")) throw new IllegalArgumentException("관리자 권한이 아닙니다.");
        return ResponseEntity.status(HttpStatus.OK).body(productService.reStockProduct(productNo, reStockRequestDto));
    }

    @Transactional
    @Operation(summary = "상품 정보 수정", description = "관리자 제한")
    @PutMapping("/{productNo}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long productNo, @RequestBody ProductRequestDto productRequestDto, HttpServletRequest request) {
        if (!request.getHeader("x-claim-role").equals("ADMIN")) throw new IllegalArgumentException("관리자 권한이 아닙니다.");
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(productNo, productRequestDto));
    }

    @Transactional
    @Operation(summary = "상품 삭제", description = "관리자 제한")
    @DeleteMapping("/{productNo}")
    public ResponseEntity<ApiResponseDto> deleteProduct(@PathVariable Long productNo, HttpServletRequest request) {
        if (!request.getHeader("x-claim-role").equals("ADMIN")) throw new IllegalArgumentException("관리자 권한이 아닙니다.");
        return ResponseEntity.status(HttpStatus.OK).body(productService.deleteProduct(productNo));
    }

    @Operation(summary = "Eureka 상품 단건 조회")
    @GetMapping("/adapt/{productNo}")
    public Product adaptGetProductNo(@PathVariable Long productNo) {
        return productService.adaptGetProductNo(productNo);
    }

    @Operation(summary = "Eureka 상품 재입고")
    @PutMapping("/adapt/{productNo}/re-stock/{quantity}")
    public void adaptReStockProduct(@PathVariable Long productNo, @PathVariable Integer quantity) {
        productService.adaptReStockProduct(productNo, quantity);
    }

    @Operation(summary = "Eureka orderResponseDtoList 출력")
    @GetMapping("/adapt/{userId}/dtoList")
    public List<OrderResponseDto> adaptGetDtoList(@PathVariable Long userId, List<Order> orderList) {
        return productService.adaptGetDtoList(userId, orderList);
    }
}
