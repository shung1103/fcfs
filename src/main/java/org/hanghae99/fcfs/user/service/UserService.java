package org.hanghae99.fcfs.user.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.hanghae99.fcfs.common.config.AES128;
import org.hanghae99.fcfs.common.config.RedisDao;
import org.hanghae99.fcfs.common.dto.ApiResponseDto;
import org.hanghae99.fcfs.common.dto.TokenResponse;
import org.hanghae99.fcfs.common.entity.UserRoleEnum;
import org.hanghae99.fcfs.common.security.JwtUtil;
import org.hanghae99.fcfs.user.dto.*;
import org.hanghae99.fcfs.user.entity.User;
import org.hanghae99.fcfs.user.repository.UserRepository;
import org.hanghae99.fcfs.wishList.service.WishListService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final WishListService wishListService;
    private final PasswordEncoder passwordEncoder;
    private final RedisDao redisDao;
    private final JavaMailSender javaMailSender;
    private final AES128 aes128;
    private final JwtUtil jwtUtil;

    private static final String senderEmail= "hoooly1103@gmail.com";
    private static int number;

    // ADMIN_TOKEN
    @Value("${ADMIN_TOKEN}")
    private String ADMIN_TOKEN;

    public ResponseEntity<UserResponseDto> signup(SignupRequestDto requestDto) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new IllegalArgumentException("중복된 ID가 존재합니다.");
        } else if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        } else {
//            sendMail(requestDto.getEmail());
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String realName = aes128.encryptAes(requestDto.getRealName());
        String address = aes128.encryptAes(requestDto.getAddress());
        String phone = aes128.encryptAes(requestDto.getPhone());
        String email = aes128.encryptAes(requestDto.getEmail());

        User user = new User(username, password, realName, address, phone, email, role);
        userRepository.save(user);
        wishListService.createWishList(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDto(user));
    }

    public ResponseEntity<ApiResponseDto> login(LoginRequestDto loginRequestDto, HttpServletRequest request, HttpServletResponse response) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("등록된 사용자가 없습니다."));
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String secChUaPlatform = request.getHeader("Sec-Ch-Ua-Platform");
        TokenResponse token = jwtUtil.createTokenByLogin(username, user.getRole(), secChUaPlatform, user.getPasswordChangeCount());
        response.addHeader("Authorization", token.getAccessToken());
        return ResponseEntity.ok().body(new ApiResponseDto("로그인 성공", HttpStatus.OK.value()));
    }

    public void logout(HttpServletRequest request, User user) {
        String username = user.getUsername();
        String accessToken = jwtUtil.resolveToken(request);
        Long expiration = jwtUtil.getExpiration(accessToken);
        // 레디스에 accessToken 사용못하도록 등록
        redisDao.setBlackList(accessToken, "logout", expiration);
        if (redisDao.hasKey(username)) redisDao.deleteRefreshToken(username);
        else throw new IllegalArgumentException("이미 로그아웃한 유저입니다.");
    }

    public UserResponseDto getUser(User user) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String email = aes128.decryptAes(user.getEmail());
        String realName = aes128.decryptAes(user.getRealName());
        String address = aes128.decryptAes(user.getAddress());
        String phone = aes128.decryptAes(user.getPhone());
        return new UserResponseDto(user, email, realName, address, phone);
    }

    public UserResponseDto updateUser(User user, UserRequestDto userRequestDto) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String address = aes128.encryptAes(userRequestDto.getAddress());
        String phone = aes128.encryptAes(userRequestDto.getPhone());
        user.updateProfile(address, phone);
        return new UserResponseDto(userRepository.save(user));
    }

    public ResponseEntity<ApiResponseDto> updatePassword(User user, PasswordRequestDto passwordRequestDto, HttpServletRequest request) {
        String currentPassword = passwordRequestDto.getCurrentPassword();
        String newPassword = passwordEncoder.encode(passwordRequestDto.getNewPassword());

        if (passwordEncoder.matches(currentPassword, user.getPassword())) {
            user.updatePassword(newPassword);
            userRepository.save(user);
            jwtUtil.reissueAtk(user.getUsername(), user.getRole(), redisDao.getRefreshToken(user.getUsername()), request, user.getPasswordChangeCount());
        } else {
            throw new IllegalArgumentException("잘못된 이전 비밀번호입니다.");
        }

        return ResponseEntity.ok().body(new ApiResponseDto("비밀번호 수정 완료", HttpStatus.OK.value()));
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
            e.printStackTrace();
        }
        return message;
    }

    public int sendMail(String mail){
        MimeMessage message = CreateMail(mail);
        javaMailSender.send(message);

        return number;
    }
}
