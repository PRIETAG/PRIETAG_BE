package com.tag.prietag.controller;

import com.tag.prietag.core.auth.session.MyUserDetails;
import com.tag.prietag.dto.ResponseDTO;
import com.tag.prietag.dto.log.LogRequest;
import com.tag.prietag.dto.log.LogResponse;
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
public class KpiLogController {
    KpiLogService kpiLogService;
    @PostMapping("/log/kpi")
    public ResponseEntity<?> saveCustomerKpi(@RequestBody @Valid LogRequest.CustomerLogInDTO customerLogInDTO, Error errors){
        kpiLogService.saveCustomerKpi(customerLogInDTO);
        return ResponseEntity.ok(new ResponseDTO<>());
    }

    @GetMapping("/dashboard/today")
    public ResponseEntity<?> getTodayKpi(@AuthenticationPrincipal MyUserDetails myUserDetails){
        LogResponse.GetTodayKpiOutDTO getTodayKpiOutDTO = kpiLogService.getTodayKpi(myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(getTodayKpiOutDTO));
    }

    @GetMapping("/dashboard/total")
    public ResponseEntity<?> getTotalKpi(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                         @RequestParam(value = "period") String period,
                                         @AuthenticationPrincipal MyUserDetails myUserDetails){
        List<LogResponse.GetTotalKpiOutDTO> getTotalKpiOutDTOList = kpiLogService.getTotalKpi(myUserDetails.getUser(), date.atStartOfDay(ZoneId.systemDefault()), period);
        return ResponseEntity.ok().body(new ResponseDTO<>(getTotalKpiOutDTOList));
    }

    @GetMapping("/dashboard/history")
    public ResponseEntity<?> getHistoryKpi(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                         @RequestParam(value = "period") String period,
                                         @AuthenticationPrincipal MyUserDetails myUserDetails){
        List<LogResponse.GetHistoryKpiOutDTO> getHistoryKpiOutDTOList = kpiLogService.getHistoryKpi(myUserDetails.getUser(),date.atStartOfDay(ZoneId.systemDefault()),period);
        return ResponseEntity.ok().body(new ResponseDTO<>(getHistoryKpiOutDTOList));
    }
}
