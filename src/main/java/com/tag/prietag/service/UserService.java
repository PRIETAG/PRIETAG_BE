package com.tag.prietag.service;

import com.tag.prietag.core.auth.jwt.MyJwtProvider;
import com.tag.prietag.core.auth.session.MyUserDetails;
import com.tag.prietag.core.exception.Exception400;
import com.tag.prietag.core.exception.Exception401;
import com.tag.prietag.core.exception.Exception500;
import com.tag.prietag.dto.user.UserRequest;
import com.tag.prietag.model.User;
import com.tag.prietag.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public String join(UserRequest.SignupInDTO signupInDTO) {

        Optional<User> userOP = userRepository.findByUsername(signupInDTO.getUsername());
        if(userOP.isPresent()){
            // 이 부분이 try catch 안에 있으면 Exception500에게 제어권을 뺏긴다.
            throw new Exception400("id", "아이디가 존재합니다");
        }

        String encPassword = passwordEncoder.encode(signupInDTO.getEmail()); // 60Byte
        signupInDTO.setEmail(encPassword);
        System.out.println("encPassword : " + encPassword);

        // 디비 save 되는 쪽만 try catch로 처리하자.
        try {
            User userPS = userRepository.save(signupInDTO.toEntity());
            return userPS.getUsername();
        }catch (Exception e){
            throw new Exception500("회원가입 실패 : "+e.getMessage());
        }
    }

    public String login(UserRequest.LoginInDTO loginInDTO) {

        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(loginInDTO.getUsername(), loginInDTO.getEmail());
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();

            return MyJwtProvider.create(myUserDetails.getUser());

        }catch (Exception e){
            throw new Exception401("인증되지 않았습니다");
        }
    }
}
