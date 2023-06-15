package model.log;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.TemplateVersion;

import javax.persistence.*;
import java.time.ZonedDateTime;

@NoArgsConstructor
@Getter
@Table(name = "customerlog_tb")
@Entity
public class CustomerLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private TemplateVersion templatevs;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now();
    }
    public enum Type{
        VIEWER,SUBSCRIPTER
    }

    @Builder
    public CustomerLog(Long id, Type type, TemplateVersion templatevs) {
        this.id = id;
        this.type = type;
        this.templatevs = templatevs;
    }
}
