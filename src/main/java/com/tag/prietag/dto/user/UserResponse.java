package com.tag.prietag.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tag.prietag.model.User;
import lombok.*;

import javax.validation.constraints.NotEmpty;

public class UserResponse {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class UserJwtOutDTO {
        private Long id;
        private String username;// 수정: nickname -> username
        @JsonIgnore
        private String password;
        @NotEmpty
        private String email;
        private User.RoleEnum role;

    }
}
