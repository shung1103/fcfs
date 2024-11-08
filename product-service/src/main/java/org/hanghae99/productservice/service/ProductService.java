package org.hanghae99.productservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hanghae99.productservice.client.FeignOrderService;
import org.hanghae99.productservice.config.AES128;
import org.hanghae99.productservice.dto.*;
import org.hanghae99.productservice.entity.Product;
import org.hanghae99.productservice.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final JavaMailSender javaMailSender;
    private final FeignOrderService feignOrderService;
    private final AES128 aes128;

    private static final String senderEmail= "hoooly1103@gmail.com";
    private static int number;

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
    public ProductResponseDto reStockProduct(Long productNo, ReStockRequestDto reStockRequestDto) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Product product = productRepository.findById(productNo).orElseThrow(() -> new NullPointerException("Product not found"));
        int newStock = product.getStock() + reStockRequestDto.getReStockQuantity();
        product.reStock(newStock);
        productRepository.saveAndFlush(product);

        List<WishList> wishLists = feignOrderService.adaptGetWishListList(productNo);

        List<Long> wishUserIdList = new ArrayList<>();
        for (WishList wishList : wishLists) wishUserIdList.add(wishList.getWishUserId());

        Queue<User> userQueue = new ArrayDeque<>();
        if (!wishLists.isEmpty()) userQueue = feignOrderService.adaptGetUserQueue(wishUserIdList);

        while (!userQueue.isEmpty()) {
            User user = userQueue.poll();
            String email = aes128.decryptAes(user.getEmail());
            sendReStockMail(email, product.getTitle());
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

    public MimeMessage CreateReStockMail(String mail, String title) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("상품 재입고 알림");
            String body = "";
            body += "<h3>위시 리스트에 등록하신 " + title + " 이 재입고되었습니다.</h3>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");
        } catch (MessagingException e) {
            e.getStackTrace();
        }
        return message;
    }

    public void sendReStockMail (String mail, String title) {
        MimeMessage reStockMessage = CreateReStockMail(mail, title);
        javaMailSender.send(reStockMessage);
    }

    public Product adaptGetProductNo(Long productNo) {
        return productRepository.findById(productNo).orElseThrow(() -> new NullPointerException("Product not found"));
    }

    public void adaptReStockProduct(Long productNo, Integer quantity) {
        Product product = productRepository.findProductById(productNo).orElseThrow(() -> new NullPointerException("Product not found"));
        product.reStock(quantity);
        productRepository.saveAndFlush(product);
    }
}
