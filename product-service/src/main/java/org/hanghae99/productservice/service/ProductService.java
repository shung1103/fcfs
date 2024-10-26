package org.hanghae99.productservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hanghae99.productservice.dto.*;
import org.hanghae99.productservice.entity.Order;
import org.hanghae99.productservice.entity.Product;
import org.hanghae99.productservice.entity.User;
import org.hanghae99.productservice.entity.WishList;
import org.hanghae99.productservice.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
    private final FeignUserService feignUserService;

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
    public ProductResponseDto reStockProduct(Long productNo, ReStockRequestDto reStockRequestDto) {
        Product product = productRepository.findById(productNo).orElseThrow(() -> new NullPointerException("Product not found"));
        int newStock = product.getStock() + reStockRequestDto.getReStockQuantity();
        product.reStock(newStock);
        productRepository.saveAndFlush(product);

        List<WishList> wishLists = feignOrderService.adaptGetWishListList(productNo);
        Queue<User> userQueue = feignUserService.adaptGetUserQueue(wishLists);

        while (!userQueue.isEmpty()) {
            User user = userQueue.poll();
            sendMail(user.getEmail());
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

    public static void createNumber(){
        //인증번호 만들기
        number = (int)(Math.random() * (90000)) + 100000;// (int) Math.random() * (최댓값-최소값+1) + 최소값
    }

    public MimeMessage CreateMail(String mail){
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("이메일 인증");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");
        } catch (MessagingException e) {
            e.getStackTrace();
        }
        return message;
    }

    public int sendMail(String mail){
        MimeMessage message = CreateMail(mail);
        javaMailSender.send(message);

        return number;
    }

    public Product adaptGetProductNo(Long productNo) {
        return productRepository.findById(productNo).orElseThrow(() -> new NullPointerException("Product not found"));
    }

    public void adaptReStockProduct(Long productNo, Integer quantity) {
        Product product = productRepository.findById(productNo).orElseThrow(() -> new NullPointerException("Product not found"));
        product.reStock(quantity);
        productRepository.saveAndFlush(product);
    }

    public List<OrderResponseDto> adaptGetDtoList(Long userId, List<Order> orderList) {
        List<OrderResponseDto> orderResponseDtoList = new ArrayList<>();
        for (Order order : orderList) {
            Product product = productRepository.findById(order.getOrderProductId()).orElseThrow(() -> new NullPointerException("Product not found"));
            orderResponseDtoList.add(new OrderResponseDto(userId, product.getTitle(), order));
        }
        return orderResponseDtoList;
    }
}
