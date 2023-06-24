package com.tag.prietag.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Setter // DTO 만들면 삭제해야됨
@Getter
@Table(name = "user_tb")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nickname",nullable = false)
    private String username;
    @JsonIgnore
    private String Password;
    private String email;
    private Long publishId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleEnum role;
    private Boolean status;


    public void updateRole(RoleEnum role){
        this.role = role;
    }


    @Builder
    public User(Long id, String username, String password, String email, RoleEnum role, Boolean status) {
        this.id = id;
        this.username = username;
        this.Password = password;
        this.email = email;
        this.role = role;
        this.status = status;
    }
}
