package com.tag.prietag.dto.User;

import com.tag.prietag.model.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
    private String username; // 수정: nickname -> username
    private String password;
    private String email;
    private RoleEnum role;
}
