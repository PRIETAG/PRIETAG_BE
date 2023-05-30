package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Setter // DTO 만들면 삭제해야됨
@Getter
@Table(name = "user_tb")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

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
