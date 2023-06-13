package service;

import core.exception.Exception400;
import dto.template.TemplateRequest;
import lombok.RequiredArgsConstructor;
import model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TemplateService {
    private final FieldRopository fieldRopository;
    private final PriceCardRepository priceCardRepository;
    private final ChartRepository chartRepository;
    private final FaqRepository faqRepository;
    private final TemplateRepository templateRepository;
    private final TemplateVersionRepository templateVersionRepository;

    //템플릿 생성
    @Transactional
    public void createTemplate(TemplateRequest.SaveInDTO saveInDTO, User user){
        if(templateRepository.findByTemplateName(saveInDTO.getTemplateName()).isPresent()){
                throw new Exception400("templateName", "이미 존재하는 템플릿 이름이 있습니다");
        }

        TemplateVersion templateVersion = saveInDTO.toTemplateVersionEntity(1);
        Template template = saveInDTO.toEntity(user);
        template.addTemplateVS(templateVersion);

        templateRepository.save(template);
    }
}
