package org.hanghae99.orderservice.controller;

import org.hanghae99.orderservice.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    OrderService orderService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createOrder() {
    }

    @Test
    void getMyOrders() {
    }

    @Test
    void getOneOrder() {
    }

    @Test
    void cancelOrder() {
    }

    @Test
    void completeOrder() {
    }

    @Test
    void adaptGetOrders() {
    }
}