package com.tag.prietag.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.tag.prietag.core.auth.jwt.MyJwtProvider;
import com.tag.prietag.core.util.Fetch;
import com.tag.prietag.dto.ResponseDTO;
import com.tag.prietag.dto.User.UserLoginDTO;
import com.tag.prietag.dto.kakao.KakaoToken;
import com.tag.prietag.dto.kakao.OAuthProfile;
import com.tag.prietag.model.RoleEnum;
import com.tag.prietag.model.User;
import com.tag.prietag.repository.UserRepository;
import com.tag.prietag.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserServiceImpl userService;

    private final UserRepository userRepository;
    private final HttpSession session;

//    @MyErrorLog
//    @MyLog
//    @PostMapping("/join")
//    public ResponseEntity<?> join(@RequestBody @Valid UserRequest.JoinInDTO joinInDTO, Errors errors) {
//        UserResponse.JoinOutDTO joinOutDTO = userService.회원가입(joinInDTO);
//        ResponseDTO<?> responseDTO = new ResponseDTO<>(joinOutDTO);
//        return ResponseEntity.ok(responseDTO);
//    }
//
//
//    @GetMapping("/s/user/{id}")
//    public ResponseEntity<?> detail(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails) throws JsonProcessingException {
//        if(id.longValue() != myUserDetails.getUser().getId()){
//            throw new Exception403("권한이 없습니다");
//        }
//        UserResponse.DetailOutDTO detailOutDTO = userService.회원상세보기(id);
//        //System.out.println(new ObjectMapper().writeValueAsString(detailOutDTO));
//        ResponseDTO<?> responseDTO = new ResponseDTO<>(detailOutDTO);
//        return ResponseEntity.ok(responseDTO);
//    }
//}

//    @GetMapping("/")
//    public String main(){
//        return "main";
//    }


//    @GetMapping("/loginForm")
//    public String loginForm(){
//        return "loginForm";
//    }
//
//    @GetMapping("/callback")
//    public  ResponseEntity<String> callback(String code) throws JsonProcessingException {
//        System.out.println("code :"+code);
//        return "코드 : "+code;
//        return ResponseEntity.badRequest().body("인증된 코드가 없습니다.");
//    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody UserRequest.LoginDTO loginDTO){
//        String jwt = userService.로그인(loginDTO);
//        return ResponseEntity.ok().header(MyJwtProvider.HEADER, jwt).body("로그인완료");
//    }

    @GetMapping("/callback")
    public ResponseEntity<?> callback(String code) throws JsonProcessingException {
        // 1. code 값 존재 유무 확인
        if (code == null || code.isEmpty()) {
//            return ResponseEntity.badRequest().body("인증된 코드가 없습니다.");
            return ResponseEntity.badRequest().body(new ResponseDTO<>(HttpStatus.BAD_REQUEST, "코드 없음", "코드가 존재 하지 않습니다."));
        }

        // 2. code 값 카카오 전달 -> access token 받기
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "87bf3594adc40498df2b327e3e60f784");
        body.add("redirect_uri", "http://localhost:8080/callback"); // 2차 검증
        body.add("code", code); // 핵심

        ResponseEntity<String> codeEntity = Fetch.kakao("https://kauth.kakao.com/oauth/token", HttpMethod.POST, body);
//        return codeEntity;

        // 3. access token으로 카카오의 홍길동 resource 접근 가능해짐 -> access token을 파싱하고
        ObjectMapper om = new ObjectMapper();
        om.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        KakaoToken kakaoToken = om.readValue(codeEntity.getBody(), KakaoToken.class);

        // 4. access token으로 email 정보 받기 (ssar@gmail.com)
        ResponseEntity<String> tokenEntity = Fetch.kakao("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, kakaoToken.getAccessToken());
        OAuthProfile oAuthProfile = om.readValue(tokenEntity.getBody(), OAuthProfile.class);
//        return  ResponseEntity.ok().body("email :"+oAuthProfile.getKakaoAccount().getEmail()); // 이메일 받아옴
//        return ResponseEntity.ok().body("oAuthProfile :" + oAuthProfile); // 아이디랑 닉네임 받아옴
//        return ResponseEntity.ok().body("id :" + oAuthProfile.getId()); // 아이디만 받음
//        return ResponseEntity.ok().body("내용 : " + oAuthProfile.getProperties());// 이름만 받음
//        return ResponseEntity.ok().body("내용 : " + oAuthProfile.getProperties().toString());// 이름만 String으로 받음

//        // 5. 해당 provider_id 값으로 회원가입되어 있는 user의 email 정보가 있는지 DB 조회 (X)
////        Optional<User> user = userRepository.findByEmail(oAuthProfile.getKakaoAccount().getEmail());
        Optional<User> userOP = userRepository.findByUsername("kakao_" + oAuthProfile.getId());
////        return ResponseEntity.ok().body("user "+user);
//
        // 6. 있으면 그 user 정보로 session 만들어주고, (자동로그인) (X)
        if (userOP.isPresent()) {
            System.out.println("디버그 : 회원정보가 있어서 로그인을 바로 진행합니다");

            UserLoginDTO userLoginDTO = new UserLoginDTO();
            userLoginDTO.setEmail(oAuthProfile.getKakaoAccount().getEmail());
            userLoginDTO.setUsername("kakao_" + oAuthProfile.getId());
//            userLoginDTO.setPassword(passwordEncoder.encode("1234"));


            String jwt = userService.로그인(userLoginDTO);
            return ResponseEntity.ok().header(MyJwtProvider.HEADER, jwt).body("로그인완료 : " + jwt);
//            return ResponseEntity.ok().body("정보 있음 : "+ jwt);
//            session.setAttribute("principal", user);
//            jwt 토큰으로 대체 예정
        }
//
        // 7. 없으면 강제 회원가입 시키고, 그 정보로 session 만들어주고, (자동로그인)
        if (userOP.isEmpty()) {
            System.out.println("디버그 : 회원정보가 없어서 회원가입 후 로그인을 바로 진행합니다");
//            user.get().getEmail();
            User user = User.builder()
                    .password("1234") // 실제로 로그인 하지 않아서 임의의 값 넣음
                    .username("kakao_" + oAuthProfile.getId())
                    .email(oAuthProfile.getKakaoAccount().getEmail())
                    .role(RoleEnum.USER)
                    .build();

            userRepository.save(user);

            UserLoginDTO userLoginDTO = new UserLoginDTO();
            userLoginDTO.setEmail(oAuthProfile.getKakaoAccount().getEmail());
            userLoginDTO.setUsername("kakao_" + oAuthProfile.getId());
//            userLoginDTO.setPassword(passwordEncoder.encode("1234"));


            String jwt = userService.로그인(userLoginDTO);
            return ResponseEntity.ok().header(MyJwtProvider.HEADER, jwt).body("회원가입 및 로그인완료 : " + jwt);
            //jwt 토큰으로 대체 예정
//            return ResponseEntity.ok().body("정보 없음 : " + jwt);
//
//            session.setAttribute("principal", user);
        }
//
//        return ResponseEntity.ok().body("good");
//
        return ResponseEntity.badRequest().body(new ResponseDTO<>(HttpStatus.BAD_REQUEST, "실패", "존재하지 않는 값입니다"));
    }

    @GetMapping("/api/logout")
    public ResponseEntity<?> logout(String state) {
        if (state == null || state.isEmpty()) {

            //https://kauth.kakao.com/oauth/logout?client_id=87bf3594adc40498df2b327e3e60f784&logout_redirect_uri=http://localhost:8080/api/logout&state=logout
            return ResponseEntity.ok().body("로그아웃 완료 STATE NULL : ");
        }
        return ResponseEntity.ok().body("로그아웃 완료 : "+state);
    }

}


