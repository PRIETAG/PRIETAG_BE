package com.tag.prietag.controller;

import com.tag.prietag.core.auth.session.MyUserDetails;
import com.tag.prietag.dto.ResponseDTO;
import com.tag.prietag.dto.log.LogRequest;
import com.tag.prietag.dto.log.LogResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.tag.prietag.service.KpiLogService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
@Tag(name = "DashBoard", description = "대시보드 API Document")
public class KpiLogController {
    private final KpiLogService kpiLogService;
    @PostMapping("/log/kpi")
    @Operation(summary = "고객 방문 로그 저장", description = "고객이 방문 또는 결재 했을때의 로그를 저장합니다")
    public ResponseEntity<?> saveCustomerKpi(@RequestBody @Valid LogRequest.CustomerLogInDTO customerLogInDTO, Error errors){
        kpiLogService.saveCustomerKpi(customerLogInDTO);
        return ResponseEntity.ok(new ResponseDTO<>());
    }

    @GetMapping("/dashboard/today")
    @Operation(summary = "오늘 kpi 조회", description = "오늘의 kpi데이터를 조회합니다")
    public ResponseEntity<?> getTodayKpi(@AuthenticationPrincipal MyUserDetails myUserDetails){
        LogResponse.GetTodayKpiOutDTO getTodayKpiOutDTO = kpiLogService.getTodayKpi(myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(getTodayKpiOutDTO));
    }

    @GetMapping("/dashboard/total")
    @Operation(summary = "날짜에 대한 kpi 차트 조회", description = "받은 데이터 기준으로 조회해 차트에 대한 kpi를 제공합니다")
    public ResponseEntity<?> getTotalKpi(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                         @RequestParam(value = "period") String period,
                                         @AuthenticationPrincipal MyUserDetails myUserDetails){
        List<LogResponse.GetTotalKpiOutDTO> getTotalKpiOutDTOList = kpiLogService.getTotalKpi(myUserDetails.getUser(), date.atStartOfDay(ZoneId.systemDefault()), period);
        return ResponseEntity.ok().body(new ResponseDTO<>(getTotalKpiOutDTOList));
    }

    @GetMapping("/dashboard/history")
    @Operation(summary = "날짜에 대한 kpi 목록조회", description = "받은 데이터 기준으로 조회해 kpi목록을 제공합니다")
    public ResponseEntity<?> getHistoryKpi(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                         @RequestParam(value = "period") String period,
                                         @AuthenticationPrincipal MyUserDetails myUserDetails){
        List<LogResponse.GetHistoryKpiOutDTO> getHistoryKpiOutDTOList = kpiLogService.getHistoryKpi(myUserDetails.getUser(),date.atStartOfDay(ZoneId.systemDefault()),period);
        return ResponseEntity.ok().body(new ResponseDTO<>(getHistoryKpiOutDTOList));
    }
}
