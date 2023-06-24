package com.tag.prietag.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.tag.prietag.core.auth.jwt.MyJwtProvider;
import com.tag.prietag.core.util.Fetch;
import com.tag.prietag.dto.ResponseDTO;
import com.tag.prietag.dto.user.UserLoginDTO;
import com.tag.prietag.dto.kakao.KakaoToken;
import com.tag.prietag.dto.kakao.OAuthProfile;
import com.tag.prietag.model.User;
import com.tag.prietag.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;


    // callback으로 코드 AccessCode 받음
    public ResponseEntity<?> accessTokenVerify(String code) {

        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseDTO<>(HttpStatus.BAD_REQUEST, "코드 없음", "코드가 존재 하지 않습니다."));
        }
        return null;
    }


    // code 값으로 AccessToken 받음
    public  ResponseEntity<String> accessTokenReceiving(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "87bf3594adc40498df2b327e3e60f784");
        body.add("redirect_uri", "http://localhost:8080/callback"); // 2차 검증
        body.add("code", code); // 핵심

        ResponseEntity<String> codeEntity = Fetch.kakao("https://kauth.kakao.com/oauth/token", HttpMethod.POST, body);

        return codeEntity;
    }

    //AccessToken의 네이밍 타입을 변경함
    public KakaoToken changeType(ResponseEntity<String> codeEntity) throws JsonProcessingException {

        ObjectMapper om = new ObjectMapper();
        om.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        KakaoToken kakaoToken = om.readValue(codeEntity.getBody(), KakaoToken.class);

        return kakaoToken;
    }



    // 받은 정보로 DB에서 조회함
    public Optional<User> getKakaoId(OAuthProfile oAuthProfile) {

        Optional<User> userOP = userRepository.findByUsername("kakao_" + oAuthProfile.getId());
        return userOP;
    }

    // AccessToken으로 카카오에서 정보 받아옴
    public OAuthProfile getResource(KakaoToken kakaoToken, ObjectMapper om) throws JsonProcessingException {

        ResponseEntity<String> tokenEntity = Fetch.kakao("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, kakaoToken.getAccessToken());
        OAuthProfile oAuthProfile = om.readValue(tokenEntity.getBody(), OAuthProfile.class);

//        return  ResponseEntity.ok().body("email :"+oAuthProfile.getKakaoAccount().getEmail()); // 이메일 받아옴
//        return ResponseEntity.ok().body("oAuthProfile :" + oAuthProfile); // 아이디랑 닉네임 받아옴
//        return ResponseEntity.ok().body("id :" + oAuthProfile.getId()); // 아이디만 받음
//        return ResponseEntity.ok().body("내용 : " + oAuthProfile.getProperties());// 이름만 받음
//        return ResponseEntity.ok().body("내용 : " + oAuthProfile.getProperties().toString());// 이름만 String으로 받음

        return oAuthProfile;
    }


    // 자동 로그인 로직
    public String login(UserLoginDTO userLoginDTO, OAuthProfile oAuthProfile) {
        userLoginDTO.setEmail(oAuthProfile.getKakaoAccount().getEmail());
        userLoginDTO.setUsername("kakao_"+oAuthProfile.getId());
        String jwt = 로그인(userLoginDTO);
        return jwt;
    }


    // 강제 회원 가입 로직
    public void userSave(OAuthProfile oAuthProfile) {

        User user = User.builder()
                .password("1234") // 실제로 로그인 하지 않아서 임의의 값 넣음
                .username("kakao_" + oAuthProfile.getId())
                .email(oAuthProfile.getKakaoAccount().getEmail())
                .role(User.RoleEnum.USER)
                .build();

        userRepository.save(user);

    }


    //로그인시 jwt 토큰 생성해서 전달

    public String 로그인(UserLoginDTO userLoginDTO) {
        Optional<User> userOP = userRepository.findByUsername(userLoginDTO.getUsername());
        if(userOP.isPresent()){
            User userPS = userOP.get();

            String jwt = MyJwtProvider.create(userPS);
            return jwt;
        }
        throw new RuntimeException("패스워드 다시 입력하세요");
    }
}
