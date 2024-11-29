package org.hanghae99.userservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hanghae99.userservice.client.FeignOrderService;
import org.hanghae99.userservice.config.AES128;
import org.hanghae99.userservice.config.RedisDao;
import org.hanghae99.userservice.dto.*;
import org.hanghae99.userservice.entity.User;
import org.hanghae99.userservice.entity.UserRoleEnum;
import org.hanghae99.userservice.repository.UserRepository;
import org.hanghae99.userservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisDao redisDao;
    private final JavaMailSender javaMailSender;
    private final AES128 aes128;
    private final JwtUtil jwtUtil;
    private final FeignOrderService feignOrderService;

    private static final String senderEmail= "hoooly1103@gmail.com";
    private static int number;

    // ADMIN_TOKEN
    @Value("${ADMIN_TOKEN}")
    private String ADMIN_TOKEN;

    @Transactional
    public ResponseEntity<UserResponseDto> signup(SignupRequestDto requestDto) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new IllegalArgumentException("중복된 ID가 존재합니다.");
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀렸습니다.");
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

        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDto(user));
    }

    public ResponseEntity<ApiResponseDto> checkEmail(String email) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String encryptedEmail = aes128.encryptAes(email);
        if (userRepository.existsByEmail(encryptedEmail)) throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        redisTemplate.opsForValue().set(email, sendMail(email), 5, TimeUnit.MINUTES);
        return ResponseEntity.ok().body(new ApiResponseDto("인증 번호를 전송하였습니다.", HttpStatus.CREATED.value()));
    }

    public ResponseEntity<ApiResponseDto> login(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("등록된 사용자가 없습니다."));
        if(!passwordEncoder.matches(password, user.getPassword())) throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");

        return ResponseEntity.ok().body(new ApiResponseDto("로그인 성공", HttpStatus.OK.value()));
    }

    public void logout(HttpServletRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NullPointerException("User not found."));

        String passwordVersion = user.getPasswordVersion();
        String accessToken = jwtUtil.resolveToken(request);
        Long expiration = jwtUtil.getExpiration(accessToken);
        // 레디스에 accessToken 사용못하도록 등록
        redisDao.setBlackList(accessToken, "logout", expiration);

        if (redisDao.hasKey(passwordVersion)) redisDao.deleteRefreshToken(passwordVersion);
        else throw new IllegalArgumentException("이미 로그아웃한 유저입니다.");
        // 소셜 로그인 유저의 경우 로그 아웃 시 비밀번호를 바꿔 모든 기기 로그 아웃
        if (user.getSocial() != null) {
            user.updatePassword(UUID.randomUUID().toString());
            userRepository.saveAndFlush(user);
        }
    }

    @Transactional
    public UserResponseDto getUser(Long id, int page, int size) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        User user = userRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        Pageable pageable = PageRequest.of(page, size);

        List<OrderResponseDto> orderResponseDtoList = feignOrderService.adaptGetOrders(user.getId());
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), orderResponseDtoList.size());
        Page<OrderResponseDto> orderResponseDtoPage = new PageImpl<>(orderResponseDtoList.subList(start, end), pageable, orderResponseDtoList.size());

        String email = aes128.decryptAes(user.getEmail());
        String realName = aes128.decryptAes(user.getRealName());
        String address = aes128.decryptAes(user.getAddress());
        String phone = aes128.decryptAes(user.getPhone());

        return new UserResponseDto(user, email, realName, address, phone, orderResponseDtoPage);
    }

    @Transactional
    public UserResponseDto updateUser(Long userId, UserRequestDto userRequestDto) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        User user = userRepository.findById(userId).orElseThrow(() -> new NullPointerException("User not found."));
        String email = aes128.encryptAes(userRequestDto.getEmail());
        String realName = aes128.encryptAes(userRequestDto.getRealName());
        String address = aes128.encryptAes(userRequestDto.getAddress());
        String phone = aes128.encryptAes(userRequestDto.getPhone());
        user.updateProfile(email, realName, address, phone);
        return new UserResponseDto(userRepository.save(user));
    }

    @Transactional
    public ResponseEntity<ApiResponseDto> updatePassword(Long userId, PasswordRequestDto passwordRequestDto, HttpServletRequest request, HttpServletResponse response) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NullPointerException("User not found."));
        String currentPassword = passwordRequestDto.getCurrentPassword();
        String newPassword = passwordEncoder.encode(passwordRequestDto.getNewPassword());

        if (passwordEncoder.matches(currentPassword, user.getPassword())) {
            user.updatePassword(newPassword);
            userRepository.save(user);
            jwtUtil.reissueAtk(userId, user.getUsername(), user.getRole(), redisDao.getRefreshToken(user.getPasswordVersion()), request, response, user.getPasswordVersion());
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
            e.getStackTrace();
        }
        return message;
    }

    public String sendMail(String mail){
        MimeMessage message = CreateMail(mail);
        javaMailSender.send(message);

        return String.valueOf(number);
    }

    public void verifyNumber(String email, String number) {
        String codeNumber = redisTemplate.opsForValue().get(email);
        if (codeNumber == null) throw new IllegalArgumentException("인증 번호가 만료되었습니다. 다시 발급 받아 주세요");
        if (!number.equals(codeNumber)) throw new IllegalArgumentException("잘못된 인증 번호입니다.");
        else redisTemplate.delete(email);
    }

    public Queue<User> adaptGetUserQueue(List<Long> wishUserIdList) {
        Queue<User> userQueue = new ArrayDeque<>();

        for (Long wishUserId : wishUserIdList) {
            User user = userRepository.findById(wishUserId).orElseThrow(() -> new NullPointerException("User not found"));
            userQueue.offer(user);
        }
        return userQueue;
    }
}
