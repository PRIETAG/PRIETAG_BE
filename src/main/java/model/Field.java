package model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Table(name = "field_tb")
@Entity
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String desc;
    public enum Role{
        TITLE,SUBTITLE,TEXT,PADDING
    }

    @Builder
    public Field(Long id, Role role, String desc) {
        this.id = id;
        this.role = role;
        this.desc = desc;
    }
}
