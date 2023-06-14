package service;

import core.exception.Exception400;
import dto.template.TemplateRequest;
import dto.template.TemplateResponse;
import lombok.RequiredArgsConstructor;
import model.Template;
import model.TemplateVersion;
import model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    public List<TemplateResponse.getTemplatesOutDTO> getTemplates(User user, Pageable pageable){
        Page<Template> templatesPS = templateRepository.findByUserId(user.getId(), pageable);

        List<TemplateResponse.getTemplatesOutDTO> getTemplatesOutDTOList = new ArrayList<>();
        for (Template template: templatesPS){

            // 최신 순으로 정렬 후 UpdateAt을 String yyyy.MM.dd HH.mm형식 으로 변경
            List<TemplateVersion> versions = template.getTemplateVersions();
            Collections.sort(versions, Collections.reverseOrder(Comparator.comparing(TemplateVersion::getUpdatedAt)));

            boolean isMatching = versions.stream()
                    .anyMatch(version -> version.getId().equals(user.getPublishId()));

            getTemplatesOutDTOList.add(TemplateResponse.getTemplatesOutDTO.builder()
                    .id(template.getId())
                    .title(template.getMainTitle())
                    .updated_at(versions.get(0).getUpdatedAt())
                    .image(versions.get(0).getLogoImageUrl())
                    .isPublished(isMatching)
                    .build());
        }
        getTemplatesOutDTOList.sort(Comparator.comparing(TemplateResponse.getTemplatesOutDTO::getUpdated_at).reversed());

        return getTemplatesOutDTOList;
    }
}
