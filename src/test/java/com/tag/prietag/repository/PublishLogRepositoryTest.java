package com.tag.prietag.repository;

import com.tag.prietag.model.Template;
import com.tag.prietag.model.TemplateVersion;
import com.tag.prietag.model.User;
import com.tag.prietag.repository.log.PublishLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ActiveProfiles("dev")
@DataJpaTest
public class PublishLogRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private TemplateVersionRepository templateVersionRepository;
    @Autowired
    private PublishLogRepository publishLogRepository;

    Long userId;
    Long templateId;
    @BeforeEach
    void setUp(){
        User user = User.builder()
                .email("dbs@naver.com")
                .username("Lee")
                .role(User.Role.USER)
                .publishId(6L)
                .build();
        userRepository.save(user);
        userId = user.getId();

        User userPS = userRepository.findById(userId).orElse(null);

        List<Template> templateList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Template template = Template.builder()
                    .user(userPS)
                    .mainTitle("내가만든 템플릿"+i)
                    .build();
            templateList.add(template);
        }
        templateRepository.saveAll(templateList);

        Template templatePS = templateRepository.findByTemplateName("내가만든 템플릿0").orElse(null);
        templateId = templatePS.getId();
        List<TemplateVersion> templateVersionList = new ArrayList<>();
        for (int i = 0; i < 6 ; i++) {
            TemplateVersion templateVersion = TemplateVersion.builder()
                    .version(i)
                    .versionTitle(templatePS.getMainTitle()+"_v"+i)
                    .font("ddd")
                    .isCardSet(false)
                    .isCheckPerPerson(false)
                    .isCheckPerYear(false)
                    .mainColor("#3214")
                    .subColor(new ArrayList<>(Arrays.asList("#4124", "#4153")))
                    .padding(new ArrayList<>(Arrays.asList(524, 423)))
                    .previewUrl("fadsfsaf.png")
                    .priceCardAreaPadding(250)
                    .build();
            templateVersion.setTemplate(templatePS);
            templateVersionList.add(templateVersion);
        }
        templateVersionRepository.saveAll(templateVersionList);


    }
}
