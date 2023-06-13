package service;

import dto.template.TemplateRequest;
import lombok.RequiredArgsConstructor;
import model.Template;
import model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.*;

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

    @Transactional
    public void createTemplate(TemplateRequest.SaveInDTO saveInDTO, User user){
        Template template = saveInDTO.toEntity(user);

    }
}
