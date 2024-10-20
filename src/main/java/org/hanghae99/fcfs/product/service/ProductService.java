package org.hanghae99.fcfs.product.service;

import lombok.RequiredArgsConstructor;
import org.hanghae99.fcfs.common.dto.ApiResponseDto;
import org.hanghae99.fcfs.product.dto.ProductRequestDto;
import org.hanghae99.fcfs.product.dto.ProductResponseDto;
import org.hanghae99.fcfs.product.dto.ReStockRequestDto;
import org.hanghae99.fcfs.product.entity.Product;
import org.hanghae99.fcfs.product.repository.ProductRepository;
import org.hanghae99.fcfs.user.entity.User;
import org.hanghae99.fcfs.user.repository.UserRepository;
import org.hanghae99.fcfs.user.service.UserService;
import org.hanghae99.fcfs.wishList.entity.WishList;
import org.hanghae99.fcfs.wishList.repository.WishListRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final WishListRepository wishListRepository;
    private final UserService userService;

    @CacheEvict(value = "Products", allEntries = true, cacheManager = "productCacheManager")
    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
        if (productRepository.existsByTitle(productRequestDto.getTitle())) throw new IllegalArgumentException("중복된 상품명이 존재합니다.");
        Product product = new Product(productRequestDto);
        productRepository.save(product);
        return new ProductResponseDto(product);
    }

    @Transactional
    @Cacheable(value = "Products", cacheManager = "productCacheManager")
    public List<ProductResponseDto> getProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductResponseDto> productResponseDtos = new ArrayList<>();
        for (Product product : products) productResponseDtos.add(new ProductResponseDto(product));
        return productResponseDtos;
    }

    public ProductResponseDto getProduct(Long productNo) {
        Product product = productRepository.findById(productNo).orElseThrow(() -> new NullPointerException("Product not found"));
        return new ProductResponseDto(product);
    }

    @Transactional
    @Cacheable(value = "Products", key = "#productNo", cacheManager = "productCacheManager")
    public Integer getProductStock(Long productNo) {
        Product product = productRepository.findById(productNo).orElseThrow(() -> new NullPointerException("Product not found"));
        return product.getStock();
    }

    @CacheEvict(value = "Products", allEntries = true, cacheManager = "productCacheManager")
    public ProductResponseDto reStockProduct(Long productNo, ReStockRequestDto reStockRequestDto) {
        Product product = productRepository.findById(productNo).orElseThrow(() -> new NullPointerException("Product not found"));
        int newStock = product.getStock() + reStockRequestDto.getReStockQuantity();
        product.reStock(newStock);
        productRepository.saveAndFlush(product);

        List<WishList> wishLists = wishListRepository.findAllByWishProductTitle(product.getTitle());
        Queue<User> userQueue = new ArrayDeque<>();

        for (WishList wishList : wishLists) {
            User user = userRepository.findByUsername(wishList.getWishUserName()).orElseThrow(() -> new NullPointerException("User not found"));
            userQueue.offer(user);
        }

        while (!userQueue.isEmpty()) {
            User user = userQueue.poll();
            userService.sendMail(user.getEmail());
        }

        return new ProductResponseDto(product);
    }

    @CacheEvict(value = "Products", allEntries = true, cacheManager = "productCacheManager")
    public ProductResponseDto updateProduct(Long productNo, ProductRequestDto productRequestDto) {
        Product product = productRepository.findById(productNo).orElseThrow(() -> new NullPointerException("Product not found"));
        product.update(productRequestDto);
        productRepository.save(product);
        return new ProductResponseDto(product);
    }

    @CacheEvict(value = "Products", allEntries = true, cacheManager = "productCacheManager")
    public ApiResponseDto deleteProduct(Long productNo) {
        Product product = productRepository.findById(productNo).orElseThrow(() -> new NullPointerException("Product not found"));
        productRepository.delete(product);
        return new ApiResponseDto("상품 삭제", HttpStatus.OK.value());
    }
}
