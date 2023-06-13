package model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;

@NoArgsConstructor
@Getter
@Table(name = "template_tb")
@Entity
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    @Column(nullable = false)
    @OneToMany(fetch = FetchType.LAZY)
    ArrayList<TemplateVersion> templateVersions;

    @Column(nullable = false, length = 20, unique = true)
    String mainTitle;

    @Builder
    public Template(Long id, User user, ArrayList<TemplateVersion> templateVersions, String mainTitle) {
        this.id = id;
        this.user = user;
        this.templateVersions = templateVersions;
        this.mainTitle = mainTitle;
    }

    public void addTemplateVS(TemplateVersion templateVersion) {templateVersions.add(templateVersion);}
}
