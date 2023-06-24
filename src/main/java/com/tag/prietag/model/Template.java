package com.tag.prietag.model;

import com.tag.prietag.core.util.TimeStamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Table(name = "template_tb")
@Entity
public class Template extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    @Column(nullable = false, length = 20, unique = true)
    String mainTitle;

    @Column(nullable = false)
    private boolean isDeleted;

    @Builder
    public Template(Long id, User user, String mainTitle) {
        this.id = id;
        this.user = user;
        this.mainTitle = mainTitle;
        this.isDeleted = false;
    }
    public Template(User user, String mainTitle) {
        this.user = user;
        this.mainTitle = mainTitle;
        this.isDeleted = false;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "Template{" +
                "id=" + id +
                ", user=" + user.getId() +
                ", mainTitle='" + mainTitle + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
