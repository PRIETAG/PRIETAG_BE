package com.tag.prietag.repository;

import com.tag.prietag.model.Template;
import com.tag.prietag.model.TemplateVersion;
import com.tag.prietag.model.User;
import com.tag.prietag.model.log.PublishLog;
import com.tag.prietag.repository.log.PublishLogRepository;
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

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

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
                    .priceCardDetailMaxHeight(500)
                    .build();
            templateVersion.setTemplate(templatePS);
            templateVersionList.add(templateVersion);
        }
        templateVersionRepository.saveAll(templateVersionList);

        TemplateVersion templateVersionPS = templateVersionRepository.findById(1L).orElse(null);

        List<PublishLog> publishLogList = new ArrayList<>();
        for (int i = 0; i <5 ; i++) {
            publishLogList.add(PublishLog.builder()
                    .user(userPS)
                    .templatevs(templateVersionPS)
                    .build());
        }
        publishLogRepository.saveAll(publishLogList);
    }

    @Test
    @DisplayName("user의 publishLog조회")
    @DirtiesContext
    public void findByUserId_test(){
        List<PublishLog> templateList = publishLogRepository.findByUserId(userId).orElse(Collections.emptyList());

        for (PublishLog publishLog: templateList) {
            System.out.println(publishLog.toString());
        }
        assertThat(templateList.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("user의 일정 기간의 publishLog조회")
    @DirtiesContext
    public void findByBetweenDateUserId_test(){
        Pageable pageable = PageRequest.of(0, 3);
        ZonedDateTime startDate = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime endDate = startDate.plusDays(1);
        Page<PublishLog> templatePG = publishLogRepository.findByBetweenDateUserId(userId, startDate, endDate, pageable);

        for (PublishLog publishLog: templatePG) {
            System.out.println(publishLog.toString());
        }
        assertThat(templatePG.getSize()).isEqualTo(3);
        assertThat(templatePG.getTotalElements()).isEqualTo(5);
    }
}
