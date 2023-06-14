package model;

import core.util.TimeStamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Table(name = "template_tb")
@Entity
public class Template extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    @Column(nullable = false)
    @OneToMany(fetch = FetchType.LAZY)
    List<TemplateVersion> templateVersions;

    @Column(nullable = false, length = 20, unique = true)
    String mainTitle;

    @Builder
    public Template(Long id, User user, List<TemplateVersion> templateVersions, String mainTitle) {
        this.id = id;
        this.user = user;
        this.templateVersions = templateVersions;
        this.mainTitle = mainTitle;
    }

    public void addTemplateVS(TemplateVersion templateVersion) {templateVersions.add(templateVersion);}
}
