package com.tag.prietag.service;

import com.tag.prietag.core.exception.Exception400;
import com.tag.prietag.dto.log.LogRequest;
import com.tag.prietag.dto.log.LogResponse;
import com.tag.prietag.model.TemplateVersion;
import com.tag.prietag.model.User;
import com.tag.prietag.model.log.CustomerLog;
import com.tag.prietag.model.log.PublishLog;
import com.tag.prietag.repository.log.PublishLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tag.prietag.repository.log.CustomerLogRepository;
import com.tag.prietag.repository.TemplateVersionRepository;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KpiLogService {
    TemplateVersionRepository templateVersionRepository;
    CustomerLogRepository customerLogRepository;
    PublishLogRepository publishLogRepository;

    @Transactional
    public void saveCustomerKpi(LogRequest.CustomerLogInDTO customerLogInDTO) {
        TemplateVersion templateVersion = templateVersionRepository.findById(customerLogInDTO.getTemplateVersionId()).orElseThrow(
                () -> new Exception400("templateVersion", "존재하지 않는 버전입니다")
        );
        CustomerLog customerLog = CustomerLog.builder()
                .type(customerLogInDTO.getType())
                .templatevs(templateVersion)
                .userId(customerLogInDTO.getUserId())
                .build();
        customerLogRepository.save(customerLog);
    }

    // 대시보드 TodayKpi 조회
    public LogResponse.GetTodayKpiOutDTO getTodayKpi(User user) {
        //오늘 kpi검색
        List<CustomerLog> customerLogList = customerLogRepository.findByCurrentDateUserId(user.getId()).orElse(Collections.emptyList());
        int viewCount = 0;
        int leaveCount = 0;
        int conversionRate = 0;

        int conversionCount = 0;
        if (!customerLogList.isEmpty()) {
            for (CustomerLog customerLog : customerLogList) {
                if (customerLog.getType().equals(CustomerLog.Type.VIEWER)) {
                    viewCount++;
                } else
                    conversionCount++;
            }
            leaveCount = viewCount - conversionCount;
            conversionRate = (conversionCount / viewCount) * 100;
        }

        //지난 버전 검색
        List<PublishLog> publishLogList = publishLogRepository.findByUserId(user.getId()).orElse(Collections.emptyList());
        int preViewCount = 0;
        int preLeaveCount = 0;
        int preConversionRate = 0;

        int preConversionCount = 0;
        // 지난버전이 존재할 경우
        if (!publishLogList.isEmpty() && publishLogList.size() > 1) {
            ZonedDateTime publishStart = publishLogList.get(1).getCreatedAt();
            ZonedDateTime publishEnd = publishLogList.get(0).getCreatedAt();
            long daysBetween = ChronoUnit.DAYS.between(publishStart.toLocalDate(), publishEnd.toLocalDate());
            // 그 버전을 사용했을 때 log데이터를 가져오기
            List<CustomerLog> betweenCustomerLogList = customerLogRepository.findByBetweenDateUserId(user.getId(), publishStart, publishEnd).orElse(Collections.emptyList());
            if (!betweenCustomerLogList.isEmpty()) {
                for (CustomerLog customerLog : betweenCustomerLogList) {
                    if (customerLog.getType().equals(CustomerLog.Type.VIEWER)) {
                        preViewCount++;
                    } else
                        preConversionCount++;
                }
                preLeaveCount = preViewCount - preConversionCount;

                preViewCount /= (int) daysBetween;
                preConversionCount /= (int) daysBetween;
                preLeaveCount /= (int) daysBetween;
                preConversionRate = (preConversionCount / preViewCount) * 100;
            }
        }

        return LogResponse.GetTodayKpiOutDTO.builder()
                .viewCount(LogResponse.GetTodayKpiOutDTO.Kpi.builder()
                        .today(viewCount)
                        .avgFromLast(preViewCount)
                        .build())
                .leaveCount(LogResponse.GetTodayKpiOutDTO.Kpi.builder()
                        .today(leaveCount)
                        .avgFromLast(preLeaveCount)
                        .build())
                .conversionRate(LogResponse.GetTodayKpiOutDTO.Kpi.builder()
                        .today(conversionRate)
                        .avgFromLast(preConversionRate)
                        .build())
                .build();
    }

    // 대시보드 TotalKpi 조회
    public List<LogResponse.GetTotalKpiOutDTO> getTotalKpi(User user, ZonedDateTime date, String period) {
        List<LogResponse.GetTotalKpiOutDTO> getTotalKpiOutDTOList;

        if (period.equals("WEEK")) {
            ZonedDateTime startDate = date.minusDays(6).truncatedTo(ChronoUnit.DAYS);
            ZonedDateTime endDate = date.with(LocalTime.MAX);

            List<CustomerLog> betweenCustomerLogList = customerLogRepository.findByBetweenDateUserId(user.getId(), startDate, endDate).orElse(Collections.emptyList());
            getTotalKpiOutDTOList = countCustomerLogsByWeek(betweenCustomerLogList);
        } else if (period.equals("MONTH")) {
            ZonedDateTime startDate = date.with(TemporalAdjusters.firstDayOfMonth());
            ZonedDateTime endDate = date.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);

            List<CustomerLog> betweenCustomerLogList = customerLogRepository.findByBetweenDateUserId(user.getId(), startDate, endDate).orElse(Collections.emptyList());
            getTotalKpiOutDTOList = countCustomerLogsByMonth(betweenCustomerLogList);
        } else if (period.equals("YEAR")) {
            ZonedDateTime startDate = date.with(TemporalAdjusters.firstDayOfYear()).truncatedTo(ChronoUnit.DAYS);
            ZonedDateTime endDate = date.with(TemporalAdjusters.lastDayOfYear()).with(LocalTime.MAX);

            List<CustomerLog> betweenCustomerLogList = customerLogRepository.findByBetweenDateUserId(user.getId(), startDate, endDate).orElse(Collections.emptyList());
            getTotalKpiOutDTOList = countCustomerLogsByYear(betweenCustomerLogList);
        } else {
            throw new Exception400("period", "period 단위가 잘못되었습니다");
        }

        return getTotalKpiOutDTOList;
    }

    public List<LogResponse.GetTotalKpiOutDTO> countCustomerLogsByWeek(List<CustomerLog> customerLogs) {
        //TreeMap으로 순서 정렬
        Map<LocalDate, Map<CustomerLog.Type, Integer>> countMap = new TreeMap<>();

        // 로그를 날짜별로 그룹화하여 수를 계산
        for (CustomerLog log : customerLogs) {
            LocalDate localDate = log.getCreatedAt().toLocalDate();
            CustomerLog.Type type = log.getType();

            // 날짜별로 내부 맵을 생성하고 해당 타입의 수를 1씩 증가
            countMap.computeIfAbsent(localDate, k -> new HashMap<>())
                    .compute(type, (k, v) -> (v == null) ? 1 : v + 1);
        }

        // 시작일 부터 7일후로 나눠논 데이터 저장
        List<LogResponse.GetTotalKpiOutDTO> getTotalKpiOutDTOList = new ArrayList<>();
        for (Map.Entry<LocalDate, Map<CustomerLog.Type, Integer>> entry : countMap.entrySet()) {
            int day = entry.getKey().getDayOfMonth();
            Map<CustomerLog.Type, Integer> typeCountMap = entry.getValue();

            int viewerCount = typeCountMap.getOrDefault(CustomerLog.Type.VIEWER, 0);
            int subscripterCount = typeCountMap.getOrDefault(CustomerLog.Type.SUBSCRIPTER, 0);

            getTotalKpiOutDTOList.add(LogResponse.GetTotalKpiOutDTO.builder()
                    .label(day)
                    .viewCount(viewerCount)
                    .leaveCount(viewerCount - subscripterCount)
                    .conversionRate((subscripterCount / viewerCount) * 100)
                    .build());
        }

        return getTotalKpiOutDTOList;
    }

    public List<LogResponse.GetTotalKpiOutDTO> countCustomerLogsByMonth(List<CustomerLog> customerLogs) {
        // TreeMap을 사용하여 순서유지
        Map<Integer, Map<CustomerLog.Type, Integer>> countMap = new TreeMap<>();

        for (CustomerLog log : customerLogs) {
            ZonedDateTime createdAt = log.getCreatedAt();

            // 주차 계산을 위해 로컬 날짜로 변환
            LocalDate localDate = createdAt.toLocalDate();

            // 주차 계산을 위해 로컬 날짜를 조정
            LocalDate weekStartDate = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            int weekOfYear = weekStartDate.get(WeekFields.of(DayOfWeek.MONDAY, 1).weekOfYear());

            // 날짜별로 내부 맵을 생성하고 해당 타입의 수를 1씩 증가
            countMap.computeIfAbsent(weekOfYear, k -> new HashMap<>())
                    .compute(log.getType(), (k, v) -> (v == null) ? 1 : v + 1);
        }

        List<LogResponse.GetTotalKpiOutDTO> getTotalKpiOutDTOList = new ArrayList<>();
        for (Map.Entry<Integer, Map<CustomerLog.Type, Integer>> entry : countMap.entrySet()) {
            Integer week = entry.getKey() - 1;
            Map<CustomerLog.Type, Integer> typeCountMap = entry.getValue();

            int viewerCount = typeCountMap.getOrDefault(CustomerLog.Type.VIEWER, 0);
            int subscripterCount = typeCountMap.getOrDefault(CustomerLog.Type.SUBSCRIPTER, 0);

            getTotalKpiOutDTOList.add(LogResponse.GetTotalKpiOutDTO.builder()
                    .label(week)
                    .viewCount(viewerCount)
                    .leaveCount(viewerCount - subscripterCount)
                    .conversionRate((subscripterCount / viewerCount) * 100)
                    .build());
        }

        return getTotalKpiOutDTOList;
    }

    public List<LogResponse.GetTotalKpiOutDTO> countCustomerLogsByYear(List<CustomerLog> customerLogs) {
        Map<Month, Map<CustomerLog.Type, Integer>> countMap = new TreeMap<>(Comparator.comparingInt(Month::getValue));

        for (CustomerLog log : customerLogs) {
            ZonedDateTime createdAt = log.getCreatedAt();

            // 월 계산을 위해 로컬 날짜로 변환
            LocalDate localDate = createdAt.toLocalDate();

            // 월별로 내부 맵을 생성하고 해당 타입의 수를 1씩 증가
            countMap.computeIfAbsent(localDate.getMonth(), k -> new HashMap<>())
                    .compute(log.getType(), (k, v) -> (v == null) ? 1 : v + 1);
        }

        List<LogResponse.GetTotalKpiOutDTO> getTotalKpiOutDTOList = new ArrayList<>();
        for (Map.Entry<Month, Map<CustomerLog.Type, Integer>> entry : countMap.entrySet()) {
            int month = entry.getKey().getValue() - 1;
            Map<CustomerLog.Type, Integer> typeCountMap = entry.getValue();

            int viewerCount = typeCountMap.getOrDefault(CustomerLog.Type.VIEWER, 0);
            int subscripterCount = typeCountMap.getOrDefault(CustomerLog.Type.SUBSCRIPTER, 0);

            getTotalKpiOutDTOList.add(LogResponse.GetTotalKpiOutDTO.builder()
                    .label(month)
                    .viewCount(viewerCount)
                    .leaveCount(viewerCount - subscripterCount)
                    .conversionRate((subscripterCount / viewerCount) * 100)
                    .build());
        }

        return getTotalKpiOutDTOList;
    }

    // 대시보드 HistoryKpi 조회
    public List<LogResponse.GetHistoryKpiOutDTO> getHistoryKpi(User user, ZonedDateTime date, String period, Pageable pageable) {
        ZonedDateTime startDate;
        ZonedDateTime endDate = date.with(LocalTime.MAX);
        if (period.equals("WEEK")) {
            startDate = date.minusDays(6).truncatedTo(ChronoUnit.DAYS);
        } else if (period.equals("MONTH")) {
            startDate = date.minusMonths(1).truncatedTo(ChronoUnit.DAYS);
        } else if (period.equals("YEAR")) {
            startDate = date.minusYears(1).with(TemporalAdjusters.firstDayOfMonth()).plusMonths(1).truncatedTo(ChronoUnit.DAYS);
        } else {
            throw new Exception400("period", "period 단위가 잘못되었습니다");
        }

        List<LogResponse.GetHistoryKpiOutDTO> getHistoryKpiOutDTOList = new ArrayList<>();

        Page<PublishLog> publishLogPage = publishLogRepository.findByBetweenDateUserId(user.getId(), startDate, endDate, pageable);
        List<CustomerLog> betweenCustomerLogList = customerLogRepository.findByBetweenDateUserId(user.getId(), startDate, endDate).orElse(Collections.emptyList());
        for (int i = 0; i < publishLogPage.getSize(); i++) {
            PublishLog publishLog = publishLogPage.getContent().get(i);
            int viewCount = 0;
            int conversionCount = 0;
            for (CustomerLog customerLog : betweenCustomerLogList) {
                if ((customerLog.getTemplatevs().getId() == publishLog.getTemplatevs().getId())
                        && (customerLog.getCreatedAt().compareTo(publishLog.getCreatedAt()) >= 0)) {
                    if (i != publishLogPage.getSize() - 1 && customerLog.getCreatedAt().compareTo(publishLogPage.getContent().get(i + 1).getCreatedAt()) < 0) {
                        int a = customerLog.getType().equals(CustomerLog.Type.VIEWER) ? viewCount++ : conversionCount++;
                    } else {
                        int a = customerLog.getType().equals(CustomerLog.Type.VIEWER) ? viewCount++ : conversionCount++;
                    }
                }
            }

            getHistoryKpiOutDTOList.add(LogResponse.GetHistoryKpiOutDTO.builder()
                    .id(publishLog.getTemplatevs().getId())
                    .publishDate(publishLog.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.M.d")))
                    .versionName(publishLog.getTemplatevs().getVersionTitle())
                    .isDeleted(publishLog.getTemplatevs().isDeleted())
                    .viewCount(viewCount)
                    .leaveCount(conversionCount - viewCount)
                    .conversionRate((conversionCount / viewCount) * 100)
                    .build());
        }

        return getHistoryKpiOutDTOList;
    }
}
