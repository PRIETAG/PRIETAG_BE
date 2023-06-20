package com.tag.prietag.controller;

import com.tag.prietag.core.auth.session.MyUserDetails;
import com.tag.prietag.dto.ResponseDTO;
import com.tag.prietag.dto.log.LogRequest;
import com.tag.prietag.dto.log.LogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.tag.prietag.service.KpiLogService;

import javax.validation.Valid;

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


}
