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


    // 템플릿 퍼블리싱 (최신)
    @Transactional
    public void publishTemplate(Long templateId, User user) {
        // 버전이 가장 높은 templateVersion의 id
        Long maxVersionId = templateVersionRepository.findIdByTemplateIdMaxVersion(templateId);

        // 퍼블리싱된 templateVersion의 id 수정
        user.setPublishId(maxVersionId);
    }
