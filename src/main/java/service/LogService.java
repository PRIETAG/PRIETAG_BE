package service;

import core.exception.Exception400;
import dto.log.LogRequest;
import lombok.RequiredArgsConstructor;
import model.TemplateVersion;
import model.log.CustomerLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.CustomerLogRepository;
import repository.TemplateVersionRepository;

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
