package com.tag.prietag.repository;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@DataJpaTest
public class TemplateRepositoryTest {

    @Autowired
    private TemplateRepository templateRepository;

    @BeforeEach
    void setUp(){

    }

}
