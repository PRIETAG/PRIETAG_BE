package com.tag.prietag.JWToken;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.tag.prietag.core.auth.jwt.MyJwtProvider;
import com.tag.prietag.model.RoleEnum;
import com.tag.prietag.model.User;
import com.tag.prietag.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class MyJwtProviderTest {

    @Autowired
    private MyJwtProvider myJwtProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .username("kakao_1234567")
                .password(passwordEncoder.encode("1234"))
                .email("sss@naver.com")
                .role(RoleEnum.USER)
                .build();

        userRepository.save(user);
    }


    final String SUBJECT = "jwtstudy";
    final int EXP = 1000 * 60 * 60;
    final String TOKEN_PREFIX = "Bearer "; // 스페이스 필요함
    final String HEADER = "Authorization";
    final String SECRET = System.getenv("SECRET");
    final String KEY = "WrongKey";


    @Test
    @DisplayName("토큰이 올바르게 생성된다")
    void createToken_test() {

        //given
        User user = userRepository.findByUsername("kakao_1234567").orElseThrow();

        String Jwt = JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP))
                .withClaim("id", user.getId())
                .withClaim("role", user.getRole().toString())
                .sign(Algorithm.HMAC512(SECRET));

        String JwtToken = TOKEN_PREFIX + Jwt;
        //when

        //then
        assertTrue(JwtToken.startsWith("Bearer ")); // "Bearer "로 시작하는지 확인
    }


    @Test
    @DisplayName("유저네임 없어서 에러 발생")
    void failCreateToken_test() {
        //given

        //when
        NoSuchElementException exception = Assertions.assertThrows(NoSuchElementException.class, () -> {
            userRepository.findByUsername("kakao_12345677")
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다"));
        });

        //then
        assertEquals("존재하지 않는 회원입니다", exception.getMessage());
    }

    @Test
    @DisplayName("토큰 정보 확인")
    void verifyToken_test() {
        //given
        User user = userRepository.findByUsername("kakao_1234567").orElseThrow();

        String Jwt = JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP))
                .withClaim("id", user.getId())
                .withClaim("role", user.getRole().toString())
                .sign(Algorithm.HMAC512(SECRET));


        //when
        DecodedJWT decodedJwt = JWT.decode(Jwt);
        String subject = decodedJwt.getSubject();
        Date expiresAt = decodedJwt.getExpiresAt();
        Long id = decodedJwt.getClaim("id").asLong();
        String role = decodedJwt.getClaim("role").asString();

        System.out.println("subject : "+subject);
        System.out.println("expiresAt : "+expiresAt);
        System.out.println("id : "+id);
        System.out.println("user.getId() : "+user.getId());
        System.out.println("role : "+role);

        //then
        Assertions.assertEquals(SUBJECT, subject);
        Assertions.assertEquals(user.getId(), id);
        Assertions.assertEquals(user.getRole().toString(), role);
    }

    @Test
    @DisplayName("만료된 토큰으로 예외 확인")
    void invalidToken_test() {
        //given
        User user = userRepository.findByUsername("kakao_1234567").orElseThrow();

        String jwt = JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() - 1000)) // 현재시간 1초전으로 만료시간 지정
                .withClaim("id", user.getId())
                .withClaim("role", user.getRole().toString())
                .sign(Algorithm.HMAC512(SECRET));

//        myJwtProvider.verify(jwt);
//        JWT.require(Algorithm.HMAC512(SECRET))
//                .build().verify(jwt);

        DecodedJWT decodedJwt = JWT.decode(jwt);
        String subject = decodedJwt.getSubject();
        Date expiresAt = decodedJwt.getExpiresAt();
        Long id = decodedJwt.getClaim("id").asLong();
        String role = decodedJwt.getClaim("role").asString();

        Assertions.assertEquals(user.getId(), id);
        Assertions.assertThrows(TokenExpiredException.class, () -> {
            myJwtProvider.verify(jwt); // 토큰 유효성 검증 시 만료된 토큰으로 예외가 발생해야 합니다.
        });

        //테스트 코드에서는 만료된 토큰으로 디코딩 된 값이 나옴 근데 원래는 예외가 터져서 나올 수 없음
    }


    @Test
    @DisplayName("만료된 토큰으로 예외 확인")
    void invalidSecretKey_test() {
        //given
        User user = userRepository.findByUsername("kakao_1234567").orElseThrow();

        String jwt = JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() - 1000)) // 현재시간 1초전으로 만료시간 지정
                .withClaim("id", user.getId())
                .withClaim("role", user.getRole().toString())
                .sign(Algorithm.HMAC512(KEY));

//        myJwtProvider.verify(jwt);
//        JWT.require(Algorithm.HMAC512(SECRET))
//                .build().verify(jwt);

        DecodedJWT decodedJwt = JWT.decode(jwt);
        String subject = decodedJwt.getSubject();
        Date expiresAt = decodedJwt.getExpiresAt();
        Long id = decodedJwt.getClaim("id").asLong();
        String role = decodedJwt.getClaim("role").asString();

        //then
        Assertions.assertThrows(SignatureVerificationException.class, () -> {
            myJwtProvider.verify(jwt); // 토큰 유효성 검증 시 다른 시크릿 때문에 예외 발생
        });
    }
}