package com.tag.prietag.dto.user;


import com.tag.prietag.model.User;
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
    private User.RoleEnum role;
}
