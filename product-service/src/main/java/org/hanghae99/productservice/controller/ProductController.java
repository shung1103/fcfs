package org.hanghae99.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hanghae99.productservice.dto.ApiResponseDto;
import org.hanghae99.productservice.dto.ProductRequestDto;
import org.hanghae99.productservice.dto.ProductResponseDto;
import org.hanghae99.productservice.dto.ReStockRequestDto;
import org.hanghae99.productservice.entity.Product;
import org.hanghae99.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
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

    @Operation(summary = "상품 재입고", description = "관리자 제한")
    @PutMapping("/{productNo}/re-stock")
    public ResponseEntity<ProductResponseDto> reStockProduct(@PathVariable Long productNo, @RequestBody ReStockRequestDto reStockRequestDto, HttpServletRequest request) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
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
    @GetMapping("/adapt/{productId}")
    public Product adaptGetProductNo(@PathVariable("productId") Long productId) {
        return productService.adaptGetProductNo(productId);
    }

    @Transactional
    @Operation(summary = "Eureka 상품 재입고")
    @PutMapping("/adapt/{productId}/re-stock")
    public void adaptReStockProduct(@PathVariable("productId") Long productId, @RequestParam("quantity") Integer quantity) {
        productService.adaptReStockProduct(productId, quantity);
    }
}
