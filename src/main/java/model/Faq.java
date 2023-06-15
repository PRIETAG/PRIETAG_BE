package model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Table(name = "faq_tb")
@Entity
public class Faq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    TemplateVersion templateVersion;

    private String question;
    private String answer;

    @Builder
    public Faq(Long id, TemplateVersion templateVersion, String question, String answer) {
        this.id = id;
        this.templateVersion = templateVersion;
        this.question = question;
        this.answer = answer;
    }

    public void setTemplateVersion(TemplateVersion templateVersion){
        this.templateVersion = templateVersion;
    }
}
