package com.tag.prietag.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tag.prietag.core.auth.jwt.MyJwtProvider;
import com.tag.prietag.dto.ResponseDTO;
import com.tag.prietag.dto.user.UserJwtOutDTO;
import com.tag.prietag.dto.user.UserLoginDTO;
import com.tag.prietag.dto.kakao.KakaoToken;
import com.tag.prietag.dto.kakao.OAuthProfile;
import com.tag.prietag.model.User;
import com.tag.prietag.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Tag(name = "User", description = "유저 API Document")
public class UserController {

    private final BCryptPasswordEncoder passwordEncoder; //패스워드 암호화시 필요
    private final UserService userService;

    @GetMapping("/callback")
    @Operation(summary = "로그인 및 회원가입", description = "카카오 코드를 이용해 회원가입 및 로그인을 합니다")
    public  ResponseEntity<?> callback(@RequestParam(value = "code") String code) throws JsonProcessingException {
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

            UserJwtOutDTO userJwtOutDTO = new UserJwtOutDTO().builder()
                    .id(userOP.get().getId())
//                    .username(userOP.get().getUsername())
//                    .role(userOP.get().getRole())
                    .email(userOP.get().getEmail())
                    .build();


            return ResponseEntity.ok().header(MyJwtProvider.HEADER, jwt).body(new ResponseDTO<>(userJwtOutDTO));
        }

        // 7. 없으면 강제 회원가입 시키고, 그 정보로 자동로그인하고 JWT토큰 전달
        if(userOP.isEmpty()){
            System.out.println("디버그 : 회원정보가 없어서 회원가입 후 로그인을 바로 진행합니다");
            User userPS = userService.userSave(oAuthProfile);

            UserLoginDTO userLoginDTO = new UserLoginDTO();
            String jwt = userService.login(userLoginDTO, oAuthProfile);

            UserJwtOutDTO userJwtOutDTO = new UserJwtOutDTO().builder()
                    .id(userPS.getId())
//                    .username(userPS.getUsername())
//                    .role(userPS.getRole())
                    .email(userPS.getEmail())
                    .build();

            return ResponseEntity.ok().header(MyJwtProvider.HEADER, jwt).body(new ResponseDTO<>(userJwtOutDTO));


        }
        return ResponseEntity.badRequest().body(new ResponseDTO<>(HttpStatus.BAD_REQUEST, "실패", "실패"));
    }

    // 회원가입
    @PostMapping("/test/join")
    public ResponseEntity<?> join(@RequestBody com.tag.prietag.dto.user.UserRequest.SignupInDTO signupInDTO, Errors errors) {
        String username = userService.joinTest(signupInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(username);
        return ResponseEntity.ok(responseDTO);
    }

    // 로그인
    @PostMapping("/test/login")
    public ResponseEntity<?> login(@RequestBody com.tag.prietag.dto.user.UserRequest.LoginInDTO loginInDTO, Errors errors){
        String result = userService.loginTest(loginInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(result);
        return ResponseEntity.ok().header(MyJwtProvider.HEADER, result).body(responseDTO);
    }
}
