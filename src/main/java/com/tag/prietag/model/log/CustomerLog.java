package com.tag.prietag.model.log;

import com.tag.prietag.model.TemplateVersion;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
