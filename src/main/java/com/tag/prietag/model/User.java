package com.tag.prietag.model;

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

    @Column(nullable = false)
    private String username;

    private String email;

    private Long publishId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public void updateRole(Role role){
        this.role = role;
    }

    public void setPublishId(Long vid) {this.publishId = vid;}
    @Builder
    public User(Long id, String username, String email, Long publishId, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.publishId = publishId;
        this.role = role;
    }

    public enum Role {
        USER,
        ADMIN,
    }

}
