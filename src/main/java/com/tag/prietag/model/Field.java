package com.tag.prietag.model;

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

    @ManyToOne(fetch = FetchType.LAZY)
    TemplateVersion templateVersion;

    @Column(nullable = false)
    private Integer index;
    @Column(nullable = false)
    private Integer areaNum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String desc;
    public enum Role{
        TITLE,SUBTITLE,TEXT,PADDING
    }

    @Builder
    public Field(Long id, TemplateVersion templateVersion, Integer index,Integer areaNum, Role role, String desc) {
        this.id = id;
        this.templateVersion = templateVersion;
        this.index = index;
        this.areaNum = areaNum;
        this.role = role;
        this.desc = desc;
    }

    public Field toEntity(TemplateVersion newTemplateVersion) {
        return Field.builder()
                .templateVersion(newTemplateVersion)
                .index(index)
                .areaNum(areaNum)
                .role(role)
                .desc(desc)
                .build();
    }

    public void setTemplateVersion(TemplateVersion templateVersion){
        this.templateVersion = templateVersion;
    }
}
