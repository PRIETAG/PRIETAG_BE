package com.tag.prietag.model.log;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.tag.prietag.model.TemplateVersion;
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

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

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
    public CustomerLog(Long id, Long userId, Type type, TemplateVersion templatevs) {
        this.id = id;
        this.type = type;
        this.templatevs = templatevs;
        this.userId = userId;
    }
}
