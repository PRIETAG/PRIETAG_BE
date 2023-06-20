package com.tag.prietag.service;

import com.tag.prietag.core.auth.jwt.MyJwtProvider;
import com.tag.prietag.dto.User.UserLoginDTO;
import com.tag.prietag.model.User;
import com.tag.prietag.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

//    public void joinSave(UserSaveDTO userSaveDTO) {
//        User user = User.builder()
//                .username(userSaveDTO.getUsername())
//                .password(userSaveDTO.getPassword())
//                .email(userSaveDTO.getEmail())
//                .role(userSaveDTO.getRole())
//                .build();
//        userRepository.joinSave(user);
//    }

//    public String 로그인(UserLoginDTO userLoginDTO) {
//        Optional<User> userOP = userRepository.findByUsername(userLoginDTO.getUsername());
//        if(userOP.isPresent()){
//            User userPS = userOP.get();
//            if(passwordEncoder.matches(userLoginDTO.getPassword(), userPS.getPassword())){
//                // matches 사용하면 디티오 로우패스워드랑 DB의 인코딩된 패스워드 비교 가능
//                String jwt = MyJwtProvider.create(userPS);
//                return jwt;
//            }
//            throw new RuntimeException("패스워드 다시 입력하세요");
//        }else{
//            throw new RuntimeException("존재하지 않는 유저입니다");
//        }
//    }

    public String 로그인(UserLoginDTO userLoginDTO) {
        Optional<User> userOP = userRepository.findByUsername(userLoginDTO.getUsername());
        if(userOP.isPresent()){
            User userPS = userOP.get();

            String jwt = MyJwtProvider.create(userPS);
            return jwt;
        }
        throw new RuntimeException("패스워드 다시 입력하세요");
    }
//    throw new RuntimeException("존재하지 않는 유저입니다");
//    @Override
//    public UserSaveDTO 회원가입(UserSaveDTO userSaveDTO) {
//        return null;
//    }
}
