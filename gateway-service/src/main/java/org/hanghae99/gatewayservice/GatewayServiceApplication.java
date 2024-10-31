package org.hanghae99.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

}
// Eureka 클러스터 설정을 할 경우 @EnableDiscoveryClient 추가 필요. 1번 서버에만 넣어도 유레카 서버들끼리 공유가 된다.