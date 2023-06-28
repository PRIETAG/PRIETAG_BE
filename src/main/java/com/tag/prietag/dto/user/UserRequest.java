package com.tag.prietag.dto.user;

import com.tag.prietag.model.User;
import lombok.*;

import javax.validation.constraints.NotEmpty;

public class UserRequest {
    @Getter
    @Setter
    public static class SignupInDTO {
        @NotEmpty
        private String username;
        @NotEmpty
        private String password;
        @NotEmpty
        private String email;


        public User toEntity() {
            return User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .publishId(null)
                    .role(User.RoleEnum.USER)
                    .status(true)
                    .build();
        }
    }

    @Getter
    @Setter
    public static class LoginInDTO {
        @NotEmpty
        private String username;
        @NotEmpty
        private String password;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class UserLoginDTO {

        @NotEmpty
        private String username; // 수정: nickname -> username
        private String password;
        @NotEmpty
        private String email;
        @NotEmpty
        private User.RoleEnum role;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class UserSaveInDTO {
        //    private Long kakaoId;
        private String username; // kakao_152378934567 방식으로 저장
        private String password;
        private String email;
        private User.RoleEnum role;

        private User toEntity() {

            return User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(User.RoleEnum.USER)
                    .status(true)
                    .build();
        }
    }

}
