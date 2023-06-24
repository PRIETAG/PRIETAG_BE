package com.tag.prietag.dto.User;

import com.tag.prietag.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSaveDTO {
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
