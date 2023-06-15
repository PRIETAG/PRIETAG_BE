package com.tag.prietag.service;

import com.tag.prietag.core.exception.Exception400;
import com.tag.prietag.dto.log.LogRequest;
import com.tag.prietag.model.TemplateVersion;
import com.tag.prietag.model.log.CustomerLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tag.prietag.repository.CustomerLogRepository;
import com.tag.prietag.repository.TemplateVersionRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LogService {
    TemplateVersionRepository templateVersionRepository;
    CustomerLogRepository customerLogRepository;
    @Transactional
    public void saveCustomerKpi(LogRequest.CustomerLogInDTO customerLogInDTO){
        TemplateVersion templateVersion = templateVersionRepository.findById(customerLogInDTO.getTemplateVersionId()).orElseThrow(
                () -> new Exception400("templateVersion", "존재하지 않는 버전입니다")
        );
        CustomerLog customerLog = CustomerLog.builder()
                .type(customerLogInDTO.getType())
                .templatevs(templateVersion)
                .build();
        customerLogRepository.save(customerLog);
    }
}
