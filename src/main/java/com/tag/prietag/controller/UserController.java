package com.tag.prietag.controller;

import com.tag.prietag.core.auth.jwt.MyJwtProvider;
import com.tag.prietag.dto.ResponseDTO;
import com.tag.prietag.dto.user.UserRequest;
import com.tag.prietag.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;


    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserRequest.SignupInDTO signupInDTO, Errors errors) {
        String username = userService.join(signupInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(username);
        return ResponseEntity.ok(responseDTO);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequest.LoginInDTO loginInDTO, Errors errors){
        String result = userService.login(loginInDTO);
        ResponseDTO<?> responseDTO = new ResponseDTO<>(result);
        return ResponseEntity.ok().header(MyJwtProvider.HEADER, result).body(responseDTO);
    }
}
