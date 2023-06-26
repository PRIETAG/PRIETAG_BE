package com.tag.prietag.dto.user;

import com.tag.prietag.model.User;
import lombok.Getter;
import lombok.Setter;

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
}
