package com.tag.prietag.service;

import com.tag.prietag.core.exception.Exception400;
import com.tag.prietag.dto.log.LogRequest;
import com.tag.prietag.model.Template;
import com.tag.prietag.model.TemplateVersion;
import com.tag.prietag.model.User;
import com.tag.prietag.model.log.CustomerLog;
import com.tag.prietag.repository.log.CustomerLogRepository;
import com.tag.prietag.repository.TemplateVersionRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class LogServiceTest {

    @InjectMocks
    private KpiLogService logService;

    @Mock
    private CustomerLogRepository customerLogRepository;

    @Mock
    private TemplateVersionRepository templateVersionRepository;

    User user;
    Template template;
    TemplateVersion templateVersion;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("dasfd@naver.com")
                .username("Lee")
                .publishId(1L)
                .role(User.Role.USER)
                .build();
        template = Template.builder()
                .id(1L)
                .mainTitle("내가 만듬")
                .user(user)
                .build();

        templateVersion = TemplateVersion.builder()
                .id(1L)
                .isCardSet(true)
                .font("dsa")
                .isCheckPerPerson(true)
                .logoImageUrl("dafdafa.jpg")
                .isCheckPerYear(true)
                .mainColor("#3214214")
                .subColor(new ArrayList<>(List.of("#312f11", "#fdas2f")))
                .yearDiscountRate(30)
                .versionTitle(template.getMainTitle())
                .padding(new ArrayList<>(List.of(400, 300)))
                .headCount(List.of(5, 8, 10))
                .headDiscountRate(List.of(10, 20, 30))
                .template(template)
                .updateAt(ZonedDateTime.now())
                .priceCardDetailMaxHeight(400)
                .build();
    }

    @Nested
    @DisplayName("방문기록KPI 저장")
    class SaveCustomerKpi {

        @Test
        @DisplayName("존재하지 않는 TemplateVersion")
        void fail() {
            saveCustomerKpiSetting();

            LogRequest.CustomerLogInDTO customerLogInDTO = LogRequest.CustomerLogInDTO.builder()
                    .templateVersionId(2L)
                    .type(CustomerLog.Type.VIEWER)
                    .build();
            //when then
            Assertions.assertThrows(Exception400.class, () -> logService.saveCustomerKpi(customerLogInDTO));
        }

        @Test
        @DisplayName("성공")
        void success() {
            //given
            saveCustomerKpiSetting();

            LogRequest.CustomerLogInDTO customerLogInDTO = LogRequest.CustomerLogInDTO.builder()
                    .templateVersionId(templateVersion.getId())
                    .type(CustomerLog.Type.VIEWER)
                    .build();

            logService.saveCustomerKpi(customerLogInDTO);
            //then
            verify(templateVersionRepository, times(1)).findById(templateVersion.getId());

            Assertions.assertDoesNotThrow(() -> logService.saveCustomerKpi(customerLogInDTO));
        }

        void saveCustomerKpiSetting() {
            lenient().when(templateVersionRepository.findById(anyLong()))
                    .thenAnswer(invocation -> {
                        Long templateVersionId = invocation.getArgument(0);
                        if (!templateVersion.getId().equals(templateVersionId))
                            throw new Exception400("templateVersion", "존재하지 않는 버전입니다");
                        return Optional.of(templateVersion);
                    });
        }
    }

}
