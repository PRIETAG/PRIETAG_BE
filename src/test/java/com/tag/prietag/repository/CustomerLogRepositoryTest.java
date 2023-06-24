package com.tag.prietag.repository;

import com.tag.prietag.model.Template;
import com.tag.prietag.model.TemplateVersion;
import com.tag.prietag.model.User;
import com.tag.prietag.model.log.CustomerLog;
import com.tag.prietag.model.log.PublishLog;
import com.tag.prietag.repository.log.CustomerLogRepository;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
@DataJpaTest
public class CustomerLogRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private TemplateVersionRepository templateVersionRepository;
    @Autowired
    private CustomerLogRepository customerLogRepository;

    Long userId;
    Long templateId;

    @BeforeEach
    void setUp() {
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
                    .mainTitle("내가만든 템플릿" + i)
                    .build();
            templateList.add(template);
        }
        templateRepository.saveAll(templateList);

        Template templatePS = templateRepository.findByTemplateName("내가만든 템플릿0").orElse(null);
        templateId = templatePS.getId();
        List<TemplateVersion> templateVersionList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            TemplateVersion templateVersion = TemplateVersion.builder()
                    .version(i)
                    .versionTitle(templatePS.getMainTitle() + "_v" + i)
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

        List<CustomerLog> customerLogList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            customerLogList.add(CustomerLog.builder()
                    .userId(userId)
                    .type(CustomerLog.Type.VIEWER)
                    .templatevs(templateVersionPS)
                    .build());
        }
        customerLogRepository.saveAll(customerLogList);
    }

    @Test
    @DisplayName("해당 유저의 일정날짜 Log조회")
    @DirtiesContext
    public void findByBetweenDateUserId_test() {
        ZonedDateTime startDate = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime endDate = startDate.plusDays(1).minusNanos(1);
        List<CustomerLog> customerLogList = customerLogRepository.findByBetweenDateUserId(userId, startDate, endDate).orElse(Collections.emptyList());

        for (CustomerLog customerLog : customerLogList) {
            System.out.println(customerLog.toString());
        }
        assertThat(customerLogList.size()).isEqualTo(5);
    }

}
