package com.tag.prietag.service;

import com.tag.prietag.core.exception.Exception400;
import com.tag.prietag.dto.log.LogRequest;
import com.tag.prietag.dto.log.LogResponse;
import com.tag.prietag.model.Template;
import com.tag.prietag.model.TemplateVersion;
import com.tag.prietag.model.User;
import com.tag.prietag.model.log.CustomerLog;
import com.tag.prietag.model.log.PublishLog;
import com.tag.prietag.repository.log.CustomerLogRepository;
import com.tag.prietag.repository.TemplateVersionRepository;
import com.tag.prietag.repository.log.PublishLogRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
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
    private PublishLogRepository publishLogRepository;
    @Mock
    private CustomerLogRepository customerLogRepository;
    @Mock
    private TemplateVersionRepository templateVersionRepository;

    User user;
    Template template;
    TemplateVersion templateVersion;
    List<CustomerLog> customerLogList;
    List<PublishLog> publishLogList;

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

        publishLogList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            publishLogList.add(PublishLog.builder()
                    .user(user)
                    .templatevs(templateVersion)
                    .createdAt(ZonedDateTime.now())
                    .build());
        }

        customerLogList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            customerLogList.add(CustomerLog.builder()
                    .userId(user.getId())
                    .type(CustomerLog.Type.VIEWER)
                    .templatevs(templateVersion)
                    .createdAt(ZonedDateTime.now())
                    .build());
        }
        for (int i = 0; i < 2; i++) {
            customerLogList.add(CustomerLog.builder()
                    .userId(user.getId())
                    .type(CustomerLog.Type.SUBSCRIPTER)
                    .templatevs(templateVersion)
                    .createdAt(ZonedDateTime.now())
                    .build());
        }
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

    @Nested
    @DisplayName("오늘 KPI지표 조회")
    class getTodayKpi {

        @Test
        @DisplayName("성공")
        void success() {
            //given
            ZonedDateTime startDate = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
            ZonedDateTime endDate = startDate.plusDays(1).minusNanos(1);
            lenient().when(customerLogRepository.findByBetweenDateUserId(anyLong(), eq(startDate), eq(endDate)))
                    .thenReturn(Optional.of(customerLogList));

            lenient().when(publishLogRepository.findByUserId(anyLong()))
                    .thenReturn(Optional.of(publishLogList));

            LogResponse.GetTodayKpiOutDTO getTodayKpiOutDTO = logService.getTodayKpi(user);
            System.out.println(getTodayKpiOutDTO.getViewCount().getToday());
            System.out.println(getTodayKpiOutDTO.getViewCount().getAvgFromLast());
            verify(customerLogRepository, times(1)).findByBetweenDateUserId(user.getId(), startDate, endDate);
            verify(publishLogRepository, times(1)).findByUserId(user.getId());

            Assertions.assertDoesNotThrow(() -> logService.getTodayKpi(user));
        }

    }

    @Nested
    @DisplayName("대시보드 TotalKpi 조회")
    class getTotalKpi {

        @Test
        @DisplayName("잘못된 period")
        void fail() {

            Assertions.assertThrows(Exception400.class, () -> logService.getTotalKpi(user, ZonedDateTime.now(), "egeK"));
        }

        @Test
        @DisplayName("WEEK 성공")
        void successWeek() {
            ZonedDateTime startDate = ZonedDateTime.now().minusDays(6).truncatedTo(ChronoUnit.DAYS);
            ZonedDateTime endDate = ZonedDateTime.now().with(LocalTime.MAX);
            lenient().when(customerLogRepository.findByBetweenDateUserId(anyLong(), eq(startDate), eq(endDate)))
                    .thenReturn(Optional.of(customerLogList));

            List<LogResponse.GetTotalKpiOutDTO> getTotalKpiOutDTOList = logService.getTotalKpi(user, ZonedDateTime.now(), "WEEK");
            for(LogResponse.GetTotalKpiOutDTO getTotalKpiOutDTO : getTotalKpiOutDTOList) {
                System.out.println( getTotalKpiOutDTO.getLabel()+" : "+getTotalKpiOutDTO.getViewCount()+
                        ", " + getTotalKpiOutDTO.getLeaveCount() + ", " + getTotalKpiOutDTO.getConversionRate());
            }
            verify(customerLogRepository, times(1)).findByBetweenDateUserId(user.getId(), startDate, endDate);

            Assertions.assertDoesNotThrow(() -> logService.getTotalKpi(user, ZonedDateTime.now(), "WEEK"));
        }

        @Test
        @DisplayName("MONTH 성공")
        void successMonth() {
            ZonedDateTime startDate = ZonedDateTime.now().minusMonths(1).truncatedTo(ChronoUnit.DAYS);
            ZonedDateTime endDate = ZonedDateTime.now().with(LocalTime.MAX);
            lenient().when(customerLogRepository.findByBetweenDateUserId(anyLong(), eq(startDate), eq(endDate)))
                    .thenReturn(Optional.of(customerLogList));

            List<LogResponse.GetTotalKpiOutDTO> getTotalKpiOutDTOList = logService.getTotalKpi(user, ZonedDateTime.now(), "MONTH");
            for(LogResponse.GetTotalKpiOutDTO getTotalKpiOutDTO : getTotalKpiOutDTOList) {
                System.out.println( getTotalKpiOutDTO.getLabel()+" : "+getTotalKpiOutDTO.getViewCount()+
                        ", " + getTotalKpiOutDTO.getLeaveCount() + ", " + getTotalKpiOutDTO.getConversionRate());
            }
            verify(customerLogRepository, times(1)).findByBetweenDateUserId(user.getId(), startDate, endDate);

            Assertions.assertDoesNotThrow(() -> logService.getTotalKpi(user, ZonedDateTime.now(), "MONTH"));
        }

        @Test
        @DisplayName("YEAR 성공")
        void successYear() {
            ZonedDateTime startDate = ZonedDateTime.now().minusYears(1).plusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).truncatedTo(ChronoUnit.DAYS);
            ZonedDateTime endDate = ZonedDateTime.now().with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
            lenient().when(customerLogRepository.findByBetweenDateUserId(anyLong(), eq(startDate), eq(endDate)))
                    .thenReturn(Optional.of(customerLogList));

            List<LogResponse.GetTotalKpiOutDTO> getTotalKpiOutDTOList = logService.getTotalKpi(user, ZonedDateTime.now(), "YEAR");
            for(LogResponse.GetTotalKpiOutDTO getTotalKpiOutDTO : getTotalKpiOutDTOList) {
                System.out.println( getTotalKpiOutDTO.getLabel()+" : "+getTotalKpiOutDTO.getViewCount()+
                        ", " + getTotalKpiOutDTO.getLeaveCount() + ", " + getTotalKpiOutDTO.getConversionRate());
            }
            verify(customerLogRepository, times(1)).findByBetweenDateUserId(user.getId(), startDate, endDate);

            Assertions.assertDoesNotThrow(() -> logService.getTotalKpi(user, ZonedDateTime.now(), "YEAR"));
        }
    }

}
