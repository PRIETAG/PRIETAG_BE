package com.tag.prietag.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tag.prietag.core.auth.jwt.MyJwtProvider;
import com.tag.prietag.dto.ResponseDTO;
import com.tag.prietag.dto.User.UserLoginDTO;
import com.tag.prietag.dto.kakao.KakaoToken;
import com.tag.prietag.dto.kakao.OAuthProfile;
import com.tag.prietag.model.User;
import com.tag.prietag.repository.UserRepository;
import com.tag.prietag.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;


@RequiredArgsConstructor
@RestController
public class UserController {

    private final BCryptPasswordEncoder passwordEncoder; //패스워드 암호화시 필요
    private final UserServiceImpl userService;
    private final UserRepository userRepository;


    @GetMapping("/callback")
    public  ResponseEntity<?> callback(String code) throws JsonProcessingException {
        // 1. code 값 존재 유무 확인

        userService.accessTokenVerify(code);

        // 2. code 값 카카오 전달 -> access token 받기 --> 인가코드 에러 발생
        ResponseEntity<String> codeEntity = userService.accessTokenReceiving(code);

        // 3. access token으로 카카오의 홍길동 resource 접근 가능해짐 -> access token을 파싱하고
        KakaoToken kakaoToken = userService.changeType(codeEntity);
        ObjectMapper om = new ObjectMapper();

        // 4. access token으로 email 정보 받기 (ssar@gmail.com)
        OAuthProfile oAuthProfile =  userService.getResource(kakaoToken, om);


       // 5. 해당 provider_id 값으로 회원가입되어 있는 user의 email 정보가 있는지 DB 조회
//        Optional<User> user = userRepository.findByEmail(oAuthProfile.getKakaoAccount().getEmail()); // 이메일로 저장
        Optional<User> userOP = userService.getKakaoId(oAuthProfile);

        // 6. 있으면 그 user정보로 자동 로그인하고 JWT 토큰 전달
        if(userOP.isPresent()){
            System.out.println("디버그 : 회원정보가 있어서 로그인을 바로 진행합니다");

            UserLoginDTO userLoginDTO = new UserLoginDTO();
            String jwt = userService.login(userLoginDTO, oAuthProfile);

            return ResponseEntity.ok().header(MyJwtProvider.HEADER, jwt).body("로그인완료 : "+jwt);
        }

        // 7. 없으면 강제 회원가입 시키고, 그 정보로 자동로그인하고 JWT토큰 전달
        if(userOP.isEmpty()){
            System.out.println("디버그 : 회원정보가 없어서 회원가입 후 로그인을 바로 진행합니다");
            userService.userSave(oAuthProfile);

            UserLoginDTO userLoginDTO = new UserLoginDTO();
            String jwt = userService.login(userLoginDTO, oAuthProfile);

            return ResponseEntity.ok().header(MyJwtProvider.HEADER, jwt).body("회원가입 및 로그인완료 : "+jwt);

        }
        return ResponseEntity.badRequest().body(new ResponseDTO<>(HttpStatus.BAD_REQUEST, "실패", "실패"));
    }
}
