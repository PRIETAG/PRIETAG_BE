package model;

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
    @Builder
    public User(Long id, Role role) {
        this.id = id;
        this.role = role;
    }

    public enum Role {
        USER,
        ADMIN,
    }

}
