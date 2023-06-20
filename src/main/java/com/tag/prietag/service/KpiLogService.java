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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tag.prietag.repository.log.CustomerLogRepository;
import com.tag.prietag.repository.TemplateVersionRepository;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KpiLogService {
    TemplateVersionRepository templateVersionRepository;
    CustomerLogRepository customerLogRepository;
    PublishLogRepository publishLogRepository;
    @Transactional
    public void saveCustomerKpi(LogRequest.CustomerLogInDTO customerLogInDTO){
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
    public LogResponse.GetTodayKpiOutDTO getTodayKpi(User user){
        List<CustomerLog> customerLogList = customerLogRepository.findByCurrentDateUserId(user.getId()).orElse(Collections.emptyList());
        int viewCount = 0;
        int leaveCount = 0;
        int conversionRate = 0;

        int conversionCount = 0;
        if(!customerLogList.isEmpty()){
            for (CustomerLog customerLog : customerLogList){
                if (customerLog.getType().equals(CustomerLog.Type.VIEWER)) {
                    viewCount++;
                }else
                    conversionCount++;
            }
            leaveCount = viewCount - conversionCount;
            conversionRate = (conversionCount/viewCount)*100;
        }

        //지난 버전 검색
        List<PublishLog> publishLogList = publishLogRepository.findByUserId(user.getId()).orElse(Collections.emptyList());
        int preViewCount = 0;
        int preLeaveCount = 0;
        int preConversionRate = 0;

        int preConversionCount = 0;
        // 지난버전이 존재할 경우
        if(!publishLogList.isEmpty() && publishLogList.size() > 1){
            ZonedDateTime publishStart = publishLogList.get(1).getCreatedAt();
            ZonedDateTime publishEnd = publishLogList.get(0).getCreatedAt();
            long daysBetween = ChronoUnit.DAYS.between(publishStart.toLocalDate(), publishEnd.toLocalDate());
            // 그 버전을 사용했을 때 log데이터를 가져오기
            List<CustomerLog> betweenCustomerLogList = customerLogRepository.findByBetweenDateUserId(user.getId(), publishStart, publishEnd).orElse(Collections.emptyList());
            if(!betweenCustomerLogList.isEmpty()){
                for (CustomerLog customerLog : betweenCustomerLogList){
                    if (customerLog.getType().equals(CustomerLog.Type.VIEWER)) {
                        preViewCount++;
                    }else
                        preConversionCount++;
                }
                preLeaveCount = preViewCount - preConversionCount;

                preViewCount /= (int)daysBetween;
                preConversionCount /= (int)daysBetween;
                preLeaveCount /= (int)daysBetween;
                preConversionRate = (preConversionCount/preViewCount)*100;
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
}
