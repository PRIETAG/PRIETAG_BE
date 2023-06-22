package com.tag.prietag.repository;

import com.tag.prietag.model.Template;
import com.tag.prietag.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
@DataJpaTest
public class TemplateRepositoryTest {

    @Autowired
    private TemplateRepository templateRepository;

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

    @Test
    @DisplayName("templateName 있는지 조회")
    @DirtiesContext
    public void findByTemplateName_test(){
        String mainTitle = "내가만든 템플릿0";
        Template template = templateRepository.findByTemplateName(mainTitle).orElse(null);

        assertThat(template.getMainTitle()).isEqualTo(mainTitle);
    }

    @Test
    @DisplayName("해당 유저의 template 조회")
    @DirtiesContext
    public void findByUserId_test(){
        Pageable pageable = PageRequest.of(0, 3);
        Page<Template> templatePG = templateRepository.findByUserId(userid, pageable);

        for (Template template: templatePG) {
            System.out.println(template.toString());
        }
        assertThat(templatePG.getSize()).isEqualTo(3);
    }

}
