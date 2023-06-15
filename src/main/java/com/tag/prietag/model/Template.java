package com.tag.prietag.model;

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
    ArrayList<TemplateVersion> templatevs;

    @Column(nullable = false, length = 20)
    String mainTitle;
}
