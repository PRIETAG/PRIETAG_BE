package controller;

import dto.ResponseDTO;
import dto.log.LogRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.LogService;

import javax.validation.Valid;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class KpiLogController {
    LogService logService;
    @PostMapping("/log/kpi")
    public ResponseEntity<?> saveCustomerKpi(@RequestBody @Valid LogRequest.CustomerLogInDTO customerLogInDTO, Error errors){
        logService.saveCustomerKpi(customerLogInDTO);
        return ResponseEntity.ok(new ResponseDTO<>());
    }
}
