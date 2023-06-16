package com.tag.prietag.repository;

import com.tag.prietag.model.Template;
import com.tag.prietag.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("dev")
@DataJpaTest
public class TemplateVersionRepositoryTest {

    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private TemplateVersionRepository templateVersionRepository;

    @Autowired
    private UserRepository userRepository;

    Long userid;
    @BeforeEach
    void setUp(){
        User user = User.builder()
                .email("dbs@naver.com")
                .username("Lee")
                .role(User.Role.USER)
                .build();
        userRepository.save(user);
        userid = user.getId();

        User userPS = userRepository.findById(userid).orElse(null);

        List<Template> templateList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Template template = Template.builder()
                    .user(userPS)
                    .mainTitle("내가만든 템플릿"+i)
                    .build();
            templateList.add(template);
        }
        templateRepository.saveAll(templateList);
    }
}
