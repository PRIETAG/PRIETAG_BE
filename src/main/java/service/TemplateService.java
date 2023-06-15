package service;

import core.exception.Exception400;
import dto.template.TemplateRequest;
import dto.template.TemplateResponse;
import lombok.RequiredArgsConstructor;
import model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TemplateService {
    private final FieldRopository fieldRepository;
    private final PriceCardRepository priceCardRepository;
    private final ChartRepository chartRepository;
    private final FaqRepository faqRepository;
    private final TemplateRepository templateRepository;
    private final TemplateVersionRepository templateVersionRepository;

    //템플릿 생성
    @Transactional
    public void createTemplate(TemplateRequest.SaveInDTO saveInDTO, User user) {
        if(templateRepository.findByTemplateName(saveInDTO.getTemplateName()).isPresent()){
            throw new Exception400("templateName", "이미 존재하는 템플릿 이름이 있습니다");
        }

        // Template 엔티티 생성 및 저장
        Template template = saveInDTO.toEntity(user);
        templateRepository.save(template);

        // TemplateVersion 엔티티 생성 및 저장
        TemplateVersion templateVersion = saveInDTO.toTemplateVersionEntity(1);
        templateVersion.setTemplate(template);
        templateVersionRepository.save(templateVersion);

        List<PriceCard> priceCards = mapAndSetTemplateVersion(saveInDTO.toPriceCardEntity(), templateVersion);
        priceCardRepository.saveAll(priceCards);

        List<Chart> charts = mapAndSetTemplateVersion(saveInDTO.toChartEntity(), templateVersion);
        chartRepository.saveAll(charts);

        List<Faq> faqs = mapAndSetTemplateVersion(saveInDTO.toFaqEntity(), templateVersion);
        faqRepository.saveAll(faqs);

        // Card Area, Chart Area, Faq Area 엔티티 등 생성 및 저장
        List<Field> cardAreas = mapAndSetTemplateVersion(saveInDTO.toCardAreaEntity(), templateVersion);
        fieldRepository.saveAll(cardAreas);

        List<Field> chartAreas = mapAndSetTemplateVersion(saveInDTO.toChartAreaEntity(), templateVersion);
        fieldRepository.saveAll(chartAreas);

        List<Field> faqAreas = mapAndSetTemplateVersion(saveInDTO.toFaqAreaEntity(), templateVersion);
        fieldRepository.saveAll(faqAreas);
    }

    //각 PriceCard, Chart, Faq, Field 들에 templateVersion 넣는 역할
    private <T> List<T> mapAndSetTemplateVersion(List<T> entities, TemplateVersion templateVersion) {
        return entities.stream()
                .peek(entity -> {
                    try {
                        Method setTemplateVersionMethod = entity.getClass().getMethod("setTemplateVersion", TemplateVersion.class);
                        setTemplateVersionMethod.invoke(entity, templateVersion);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                })
                .collect(Collectors.toList());
    }

    //템플릿 조회
    public List<TemplateResponse.getTemplatesOutDTO> getTemplates(User user, Pageable pageable){
        Page<Template> templatesPS = templateRepository.findByUserId(user.getId(), pageable);

        List<TemplateResponse.getTemplatesOutDTO> getTemplatesOutDTOList = new ArrayList<>();
        for (Template template: templatesPS){

            // 최신 버전 가져온 후 UpdateAt을 String yyyy.MM.dd HH.mm형식 으로 변경
            TemplateVersion templateVersion = templateVersionRepository.findByTemplateIdLimitOne(template.getId()).orElseThrow(
                    () -> new Exception400("Template조회", "templateVersion이 존재하지 않습니다.")
            );

            // 퍼블리싱된 templateVersion 있는지 확인
            boolean isMatching = templateVersionRepository.findByPublishTemplateId(user.getPublishId(), template.getId()).isPresent();

            getTemplatesOutDTOList.add(TemplateResponse.getTemplatesOutDTO.builder()
                    .id(template.getId())
                    .title(template.getMainTitle())
                    .updated_at(templateVersion.getUpdatedAt())
                    .image(templateVersion.getPreviewUrl())
                    .isPublished(isMatching)
                    .build());
        }

        return getTemplatesOutDTOList;
    }

    public List<TemplateResponse.getTemplatesVSOutDTO> getTemplatesVS(Long id, Pageable pageable){
        Template template = templateRepository.findById(id).orElseThrow(
                () -> new Exception400("template", "존재하지 않는 Template입니다"));

        List<TemplateResponse.getTemplatesVSOutDTO> getTemplatesVSOutDTOList = new ArrayList<>();
        Page<TemplateVersion> templateVersionPage = templateVersionRepository.findByTemplateId(id, pageable);
        for(TemplateVersion templateVersion: templateVersionPage){
            getTemplatesVSOutDTOList.add(TemplateResponse.getTemplatesVSOutDTO.builder()
                            .id(templateVersion.getId())
                            .title(templateVersion.getVersionTitle()+"v_"+templateVersion.getVersion())
                            .updated_at(templateVersion.getUpdatedAt())
                    .build());
        }

        return getTemplatesVSOutDTOList;
    }

    // 템플릿 버전 생성
    public void createTemplateVS(Long templateId, TemplateRequest.SaveInDTO saveInDTO, User user) {
        Template template = templateRepository.findById(templateId).orElseThrow(
                () -> new Exception400("template", "존재하지 않는 Template입니다"));

        if(!template.getUser().getId().equals(user.getId())){
            throw new Exception400("template", "해당 Template에 대한 권한이 없습니다");
        }

        // TemplateVersion 엔티티 생성 및 저장
        int versionId = templateVersionRepository.findMaxVersionByTemplateId(templateId) + 1;
        TemplateVersion templateVersion = saveInDTO.toTemplateVersionEntity(versionId);
        templateVersion.setTemplate(template);
        templateVersionRepository.save(templateVersion);

        List<PriceCard> priceCards = mapAndSetTemplateVersion(saveInDTO.toPriceCardEntity(), templateVersion);
        priceCardRepository.saveAll(priceCards);

        List<Chart> charts = mapAndSetTemplateVersion(saveInDTO.toChartEntity(), templateVersion);
        chartRepository.saveAll(charts);

        List<Faq> faqs = mapAndSetTemplateVersion(saveInDTO.toFaqEntity(), templateVersion);
        faqRepository.saveAll(faqs);

        // Card Area, Chart Area, Faq Area 엔티티 등 생성 및 저장
        List<Field> cardAreas = mapAndSetTemplateVersion(saveInDTO.toCardAreaEntity(), templateVersion);
        fieldRepository.saveAll(cardAreas);

        List<Field> chartAreas = mapAndSetTemplateVersion(saveInDTO.toChartAreaEntity(), templateVersion);
        fieldRepository.saveAll(chartAreas);

        List<Field> faqAreas = mapAndSetTemplateVersion(saveInDTO.toFaqAreaEntity(), templateVersion);
        fieldRepository.saveAll(faqAreas);

    }

    // 템플릿 복제 -> 최신 버전 1개 생성
    public void copyTemplate(Long templateId, User user) {
        // TODO: 새로운 템플릿 이름 중복 체크
//        if(templateRepository.findByTemplateName(새로운템플릿이름).isPresent()){
//            throw new Exception400("templateName", "이미 존재하는 템플릿 이름이 있습니다");
//        }
        Template originTemplate = templateRepository.findById(templateId).orElseThrow(
                () -> new Exception400("template", "존재하지 않는 Template입니다"));
        // 가장 높은 버전 템플릿 가져오기
        TemplateVersion originTemplateVersion = templateVersionRepository.findMaxVersionTemplate(templateId);

        // 새로운 Template 엔티티 생성 및 저장
        Template newTemplate = new Template(user, originTemplate.getMainTitle());
        templateRepository.save(newTemplate);

        // 새로운 TemplateVersion 엔티티 생성 및 저장
        TemplateVersion newTemplateVersion = new TemplateVersion(newTemplate, originTemplateVersion);
        templateVersionRepository.save(newTemplateVersion);

        // 새로운 PriceCard, Chart, Faq 엔티티 생성 및 저장
        List<PriceCard> priceCards = mapAndSetTemplateVersion(priceCardRepository.findAllByTemplateVersionIdOOrderByIndex(originTemplateVersion.getId()), newTemplateVersion);
        for (PriceCard priceCard: priceCards){
            priceCard.setTemplateVersion(newTemplateVersion);
        }
        priceCardRepository.saveAll(priceCards);

        List<Chart> charts = mapAndSetTemplateVersion(chartRepository.findAllByTemplateVersionIdOOrderByIndex(originTemplateVersion.getId()), newTemplateVersion);
        for (Chart chart: charts){
            chart.setTemplateVersion(newTemplateVersion);
        }
        chartRepository.saveAll(charts);

        List<Faq> faqs = mapAndSetTemplateVersion(faqRepository.findAllByTemplateVersionIdOOrderByIndex(originTemplateVersion.getId()), newTemplateVersion);
        for (Faq faq: faqs){
            faq.setTemplateVersion(newTemplateVersion);
        }
        faqRepository.saveAll(faqs);


        // 새로운 Card Area, Chart Area, Faq Area 엔티티 생성 및 저장
        List<Field> cardAreas = mapAndSetTemplateVersion(fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(originTemplateVersion.getId(), 1), newTemplateVersion);
        for (Field cardArea: cardAreas){
            cardArea.setTemplateVersion(newTemplateVersion);
        }
        fieldRepository.saveAll(cardAreas);

        List<Field> chartAreas = mapAndSetTemplateVersion(fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(originTemplateVersion.getId(), 2), newTemplateVersion);
        for (Field chartArea: chartAreas){
            chartArea.setTemplateVersion(newTemplateVersion);
        }
        fieldRepository.saveAll(chartAreas);

        List<Field> faqAreas = mapAndSetTemplateVersion(fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(originTemplateVersion.getId(), 3), newTemplateVersion);
        for (Field faqArea: faqAreas){
            faqArea.setTemplateVersion(newTemplateVersion);
        }
        fieldRepository.saveAll(faqAreas);
    }
    }
}
