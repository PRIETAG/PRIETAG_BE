package com.tag.prietag.repository;

import com.tag.prietag.model.Template;
import com.tag.prietag.model.TemplateVersion;
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
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
@DataJpaTest
public class TemplateVersionRepositoryTest {

    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private TemplateVersionRepository templateVersionRepository;

    @Autowired
    private UserRepository userRepository;

    Long userId;
    Long templateId;
    @BeforeEach
    void setUp(){
        User user = User.builder()
                .email("dbs@naver.com")
                .username("Lee")
                .role(User.RoleEnum.USER)
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
                    .priceCardDetailMaxHeight(400)
                    .build();
            templateVersion.setTemplate(templatePS);
            templateVersionList.add(templateVersion);
        }
        templateVersionRepository.saveAll(templateVersionList);
    }

    @Test
    @DisplayName("template중 가장 최신 Version")
    @DirtiesContext
    public void findByTemplateIdLimitOne_test(){
        Pageable pageable = PageRequest.of(0, 1);
        Page<TemplateVersion> templateVersionPG = templateVersionRepository.findByTemplateIdLimitOne(templateId, pageable);

        for (TemplateVersion templateVersion: templateVersionPG) {
            System.out.println(templateVersion.toString());
        }

        assertThat(templateVersionPG.getSize()).isEqualTo(1);
    }

    @Test
    @DisplayName("지정 template에 publish된 version 있는지 찾기")
    @DirtiesContext
    public void findByPublishTemplateId_test(){
        User user = userRepository.findById(userId).orElse(null);
        boolean isPublishing = templateVersionRepository.findByPublishTemplateId(user.getPublishId(), templateId).isPresent();

        assertThat(isPublishing).isEqualTo(true);
    }

    @Test
    @DisplayName("template에 대한 Version들 조회")
    @DirtiesContext
    public void findByTemplateId_test(){
        Pageable pageable = PageRequest.of(0, 4);
        Page<TemplateVersion> templateVersionPG = templateVersionRepository.findByTemplateId(templateId, pageable);

        for (TemplateVersion templateVersion: templateVersionPG) {
            System.out.println(templateVersion.toString());
        }

        assertThat(templateVersionPG.getTotalElements()).isEqualTo(6);
    }
}
