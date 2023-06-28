package com.tag.prietag.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tag.prietag.model.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserJwtOutDTO {
    private Long id;
    private String username;// 수정: nickname -> username
    @JsonIgnore
    private String password;
    private String email;
    private User.RoleEnum role;

}

