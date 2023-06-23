//package com.tag.prietag.repository;
//
//import com.tag.prietag.model.RoleEnum;
//import com.tag.prietag.model.User;
//import com.tag.prietag.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@ActiveProfiles("dev")
//@DataJpaTest
//public class UserRepositoryTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//
//    @Autowired
//    private UserService userService;
//
////    @BeforeEach
////    void setUp(){
////        User user = User.builder()
////                .email("sss@naver.com")
////                .username("kakao_"+"123745638")
////                .role(RoleEnum.USER)
////                .build();
////    }
//
//
//    @Test
//    @DisplayName("카카오 강제 회원가입 확인")
//    @DirtiesContext
//    public void UserSave_test() {
//        //given
//        User user = User.builder()
//                .email("sss@naver.com")
//                .username("kakao_"+"123745638")
//                .role(RoleEnum.USER)
//                .password("1234")
//                .build();
//        User userPS = userRepository.save(user);
//
//
//
//        //when
//        UserRepository userRepository = new UserRepository();
//        Optional<User> userOP = UserRepository.findByUsername("kakao_123745638").orElse(null);
//
//        //then
//
//        assertThat(userPS.getUsername().isEqualTo(userOP.get().getUsername()));
//    }
//
//
//}
