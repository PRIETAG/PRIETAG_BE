package com.tag.prietag.service;

import com.tag.prietag.core.exception.Exception400;
import com.tag.prietag.dto.template.TemplateRequest;
import com.tag.prietag.dto.template.TemplateResponse;
import com.tag.prietag.model.*;
import com.tag.prietag.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
        // 해당 유저의 템플릿 네임만 찾아야 하나
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

    //템플릿 목록 조회
    public List<TemplateResponse.getTemplatesOutDTO> getTemplates(User user, Pageable pageable){
        Page<Template> templatesPS = templateRepository.findByUserId(user.getId(), pageable);

        List<TemplateResponse.getTemplatesOutDTO> getTemplatesOutDTOList = new ArrayList<>();
        for (Template template: templatesPS){

            // 최신 버전 가져온 후 UpdateAt을 String yyyy.MM.dd HH.mm형식 으로 변경
            Pageable pageableLimitOne = PageRequest.of(0, 1);
            Page<TemplateVersion> templateVersions = templateVersionRepository.findByTemplateIdLimitOne(template.getId(), pageableLimitOne);
            if(!templateVersions.hasContent()){
                throw new Exception400("templateVersion", "version이 존재하지 않습니다");
            }
            TemplateVersion templateVersion = templateVersions.getContent().get(0);

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
        Template newTemplate = Template.builder()
                .mainTitle(originTemplate.getMainTitle())
                .user(user)
                .build();
        templateRepository.save(newTemplate);

        // 새로운 TemplateVersion 엔티티 생성 및 저장
        TemplateVersion newTemplateVersion = TemplateVersion.builder()
                .template(newTemplate)
                .version(1)
                .versionTitle(originTemplateVersion.getVersionTitle())
                .mainColor(originTemplateVersion.getMainColor())
                .subColor(List.of(originTemplateVersion.getSubColor1(), originTemplateVersion.getSubColor2()))
                .font(originTemplateVersion.getFont())
                .logoImageUrl(originTemplateVersion.getLogoImageUrl())
                .previewUrl(originTemplateVersion.getPreviewUrl())
                .padding(List.of(originTemplateVersion.getPadding1(), originTemplateVersion.getPadding2()))
                .isCheckPerPerson(originTemplateVersion.isCheckPerPerson())
                .headCount(originTemplateVersion.getHeadCount())
                .headDiscountRate(originTemplateVersion.getHeadDiscountRate())
                .isCheckPerYear(originTemplateVersion.isCheckPerYear())
                .yearDiscountRate(originTemplateVersion.getYearDiscountRate())
                .isCardSet(originTemplateVersion.isCardSet())
                .priceCardAreaPadding(originTemplateVersion.getPriceCardAreaPadding())
                .priceCardDetailMaxHeight(originTemplateVersion.getPriceCardDetailMaxHeight())
                .build();
        templateVersionRepository.save(newTemplateVersion);

        // 새로운 PriceCard, Chart, Faq 엔티티 생성 및 저장
        List<PriceCard> priceCards = mapAndSetTemplateVersion(priceCardRepository.findAllByTemplateVersionIdOrderByIndex(originTemplateVersion.getId()), newTemplateVersion);
        for (PriceCard priceCard: priceCards){
            priceCard.setTemplateVersion(newTemplateVersion);
        }
        priceCardRepository.saveAll(priceCards);

        List<Chart> charts = mapAndSetTemplateVersion(chartRepository.findAllByTemplateVersionIdOrderByIndex(originTemplateVersion.getId()), newTemplateVersion);
        for (Chart chart: charts){
            chart.setTemplateVersion(newTemplateVersion);
        }
        chartRepository.saveAll(charts);

        List<Faq> faqs = mapAndSetTemplateVersion(faqRepository.findAllByTemplateVersionIdOrderByIndex(originTemplateVersion.getId()), newTemplateVersion);
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


    // 템플릿 퍼블리싱 (최신)
    @Transactional
    public void publishTemplate(Long templateId, User user) {
        // 버전이 가장 높은 templateVersion의 id
        Long maxVersionId = templateVersionRepository.findIdByTemplateIdMaxVersion(templateId);

        // 퍼블리싱된 templateVersion의 id 수정
        user.setPublishId(maxVersionId);
    }


    // 템플릿 퍼블리싱 (버전 선택)
    @Transactional
    public void publishTemplateVS(Long versionId, User user) {
        // 퍼블리싱된 templateVersion의 id 수정
        user.setPublishId(versionId);
    }


    // 템플릿 불러오기 (퍼블리싱)
    public TemplateResponse.TemplateVSOutDTO getPublishedTemplateVS(Long userId, User user) {
        // TODO: 로그인된 유저 정보를 사용할지?
        Long publishId = user.getPublishId();

        TemplateVersion templateVersion = templateVersionRepository.findById(publishId).orElseThrow(
                () -> new Exception400("templateVersion", "존재하지 않는 TemplateVersion입니다"));
        Long versionId = templateVersion.getId();

        // 카드, 차트, faq area 정보 불러오기
        List<Field> cardArea = fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(versionId, 1);
        List<Field> chartArea = fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(versionId, 2);
        List<Field> faqArea = fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(versionId, 3);

        // 카드, 차트, faq 정보 불러오기
        List<PriceCard> priceCard = priceCardRepository.findAllByTemplateVersionIdOrderByIndex(versionId);
        List<Chart> chart = chartRepository.findAllByTemplateVersionIdOrderByIndex(versionId);
        List<Faq> faq = faqRepository.findAllByTemplateVersionIdOrderByIndex(versionId);

        return new TemplateResponse.TemplateVSOutDTO(cardArea, chartArea, faqArea, priceCard, chart, faq, templateVersion);
    }


    // 템플릿 불러오기 (버전 선택)
    public TemplateResponse.TemplateVSOutDTO getTemplateVS(Long versionId, User user) {
        TemplateVersion templateVersion = templateVersionRepository.findById(versionId).orElseThrow(
                () -> new Exception400("templateVersion", "존재하지 않는 TemplateVersion입니다"));

        // 카드, 차트, faq area 정보 불러오기
        List<Field> cardArea = fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(versionId, 1);
        List<Field> chartArea = fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(versionId, 2);
        List<Field> faqArea = fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(versionId, 3);

        // 카드, 차트, faq 정보 불러오기
        List<PriceCard> priceCard = priceCardRepository.findAllByTemplateVersionIdOrderByIndex(versionId);
        List<Chart> chart = chartRepository.findAllByTemplateVersionIdOrderByIndex(versionId);
        List<Faq> faq = faqRepository.findAllByTemplateVersionIdOrderByIndex(versionId);

        return new TemplateResponse.TemplateVSOutDTO(cardArea, chartArea, faqArea, priceCard, chart, faq, templateVersion);
    }


    // 템플릿 버전(히스토리) 삭제
    @Transactional
    public void deleteTemplateVS(TemplateRequest.DeleteInDTO deleteInDTO, User user) {

        for (Long id: deleteInDTO.getId()){
            TemplateVersion templateVersion = templateVersionRepository.findById(id).orElseThrow(
                    () -> new Exception400("templateVersion", "존재하지 않는 TemplateVersion입니다"));
            templateVersion.setDeleted(true);
        }
    }


    // 템플릿 삭제 (버전 포함 템플릿 자체 삭제)
    public void deleteTemplate(Long templateId, User user) {
        Template template = templateRepository.findById(templateId).orElseThrow(
                () -> new Exception400("template", "존재하지 않는 Template입니다"));
        template.setDeleted(true);

        List<TemplateVersion> templateVersions = templateVersionRepository.findAllByTemplateId(templateId);
        for (TemplateVersion templateVersion: templateVersions){
            templateVersion.setDeleted(true);
        }
    }


}