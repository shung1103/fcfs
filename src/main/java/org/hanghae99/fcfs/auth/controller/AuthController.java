package org.hanghae99.fcfs.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.hanghae99.fcfs.auth.service.GoogleService;
import org.hanghae99.fcfs.auth.service.KakaoService;
import org.hanghae99.fcfs.auth.service.NaverService;
import org.hanghae99.fcfs.common.security.JwtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final NaverService naverService;
    private final KakaoService kakaoService;
    private final GoogleService googleService;
    private final JwtUtil jwtUtil;

    @GetMapping("/naver/login")
    public RedirectView naverLogin(@RequestParam String code, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        String token = naverService.naverLogin(code, request);
        response.addHeader("Authorization", token);
        return new RedirectView("/");
    }

    @GetMapping("/kakao/login")
    public RedirectView kakaoLogin(@RequestParam String code, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        String token = kakaoService.kakaoLogin(code, request);
        response.addHeader("Authorization", token);
        return new RedirectView("/");
    }

    @GetMapping("/google/login")
    public RedirectView googleLogin(@RequestParam String code, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        String token = googleService.googleLogin(code, request);
        response.addHeader("Authorization", token);
        return new RedirectView("/");
    }
}
