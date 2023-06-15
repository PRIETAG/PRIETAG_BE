package com.tag.prietag.model.log;

import lombok.Getter;
import lombok.NoArgsConstructor;
import com.tag.prietag.model.TemplateVersion;
import com.tag.prietag.model.User;
import javax.persistence.*;
import java.time.ZonedDateTime;

@NoArgsConstructor
@Getter
@Table(name = "publishlog_tb")
@Entity
public class PublishLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private TemplateVersion templatevs;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now();
    }
}
