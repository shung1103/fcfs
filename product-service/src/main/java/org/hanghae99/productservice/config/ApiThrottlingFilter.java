package org.hanghae99.productservice.config;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiThrottlingFilter implements Filter {
    private static final String STOP_REQUEST = "There is No Token Anymore";
    private static final List<String> GREEDY_API = List.of();
    private static final List<String> INTERVAL_API = List.of("/api/product/{productNo}/re-stock");

    // Greedy Refill Bucket : duration/refill 초마다 1개의 토큰을 추가
    private static final Bucket greedyBucket = createGreedyBucket(3, 3, Duration.ofSeconds(3));
    // Interval Refill Bucket : duration 마다 refill 갯수 만큼 한번에 추가
    private static final Bucket intervalBucket = createIntervalBucket(1000, 1000, Duration.ofSeconds(1));

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // api 경로에 포함된다면 토큰 사용
        if (INTERVAL_API.contains(request.getRequestURI())) {
            log.info("interval bucket 사용");
            checkApi(intervalBucket, filterChain, servletRequest, servletResponse);
        }
        // 아닐 경우 필터 진행
        filterChain.doFilter(servletRequest, servletResponse);
    }

    // Greedy Refill Bucket 방식 : duration 동안 refill 개의 토큰을 지속적으로 채워 넣는다.
    private static Bucket createGreedyBucket(int capacity, int refill, Duration duration) {
        return Bucket.builder().addLimit(limit -> limit.capacity(capacity).refillGreedy(refill, duration)).build();
    }

    // Interval Refill Bucket 방식 : duration 마다 refill 개 만큼 한번에 추가
    private static Bucket createIntervalBucket(int capacity, int refill, Duration intervalDuration) {
        return Bucket.builder().addLimit(limit -> limit.capacity(capacity).refillIntervally(refill, intervalDuration)).build();
    }

    private void checkApi(Bucket bucket, FilterChain filterChain, ServletRequest request, ServletResponse response) throws IOException, ServletException {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        // 토큰이 남아있으면 요청을 처리하고, 없으면 에러 코드 429 반환
        if (probe.isConsumed()) {
            filterChain.doFilter(request, response);
            log.info("현재 남은 토큰 수 : {}", bucket.getAvailableTokens());
        } else {
            // 리필까지 남은 시간
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            // 응답
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setContentType("text/plain; charset=utf-8");
            httpServletResponse.setStatus(429);
            response.getWriter().write(STOP_REQUEST + String.format("\n %s초 뒤에 다시 시도해 주세요.", waitForRefill));
        }
    }
}
