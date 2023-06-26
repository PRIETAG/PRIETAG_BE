package com.tag.prietag.service;

import com.tag.prietag.core.exception.Exception400;
import com.tag.prietag.core.util.S3Uploader;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final S3Uploader s3Uploader;

    //템플릿 생성
    @Transactional
    public void createTemplate(TemplateRequest.SaveInDTO saveInDTO, User user, MultipartFile logoImg, MultipartFile previewImg) throws IOException {
        if(templateRepository.findByTemplateName(saveInDTO.getTemplateName()).isPresent()){
            throw new Exception400("templateName", "이미 존재하는 템플릿 이름이 있습니다");
        }

        // Template 엔티티 생성 및 저장
        Template template = saveInDTO.toEntity(user);
        templateRepository.save(template);

        // TemplateVersion version 1로 엔티티 생성 및 저장
        TemplateVersion templateVersion = saveInDTO.toTemplateVersionEntity(1);
        if(logoImg != null && !logoImg.isEmpty()){
            String storedFileName = s3Uploader.upload(logoImg, "logos");
            templateVersion.setLogoImageUrl(storedFileName);
        }
        if(previewImg != null && !previewImg.isEmpty()){
            String storedFileName = s3Uploader.upload(previewImg, "preview");
            templateVersion.setPreviewUrl(storedFileName);
        }
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
    public String createTemplateVS(Long templateId, TemplateRequest.SaveInDTO saveInDTO, User user) {
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

        return "template id = " + templateId + ", version = " + templateVersion.getVersion() + ", version id = " + versionId;
    }


    // 템플릿 복제 -> 최신 버전 1개 생성
    @Transactional
    public String copyTemplate(Long templateId, User user) {
        // TODO: 새로운 템플릿 이름 중복 체크
//        if(templateRepository.findByTemplateName(새로운템플릿이름).isPresent()){
//            throw new Exception400("templateName", "이미 존재하는 템플릿 이름이 있습니다");
//        }
        Template originTemplate = templateRepository.findById(templateId).orElseThrow(
                () -> new Exception400("template", "존재하지 않는 Template입니다"));
        // 가장 높은 버전 템플릿 가져오기
        TemplateVersion originTemplateVersion = templateVersionRepository.findMaxVersionTemplate(templateId);

        StringBuilder mainTitle = new StringBuilder(originTemplate.getMainTitle());
        // 템플릿 이름을 포함한 템플릿 가져오기
        List<Template> templateList = templateRepository.findByMainTitleContainingAndIsDeleted(mainTitle.toString(), false);
        int index = 1;
        while (templateList.size() != 0) {
            mainTitle.append("_").append(index);
            if (templateList.stream().anyMatch(template -> template.getMainTitle().equals(mainTitle.toString()))) {
                index++;
            } else {
                break;
            }
        }

        // 새로운 Template 엔티티 생성 및 저장
        Template newTemplate = new Template(user, mainTitle.toString());
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
        List<PriceCard> priceCards = priceCardRepository.findAllByTemplateVersionIdOrderByIndex(originTemplateVersion.getId());
        List<PriceCard> newPriceCards = new ArrayList<>();
        for (PriceCard priceCard: priceCards){
            PriceCard newPriceCard = priceCard.toEntity(newTemplateVersion);
            newPriceCards.add(newPriceCard);
        }
        List<Long> newPriceCardIds = priceCardRepository.saveAll(newPriceCards).stream().map(PriceCard::getId).collect(Collectors.toList());

        List<Chart> charts = chartRepository.findAllByTemplateVersionIdOrderByIndex(originTemplateVersion.getId());
        List<Chart> newCharts = new ArrayList<>();
        for (Chart chart: charts){
            Chart newChart = chart.toEntity(newTemplateVersion);
            newCharts.add(newChart);
        }
        List<Long> newChartds = chartRepository.saveAll(newCharts).stream().map(Chart::getId).collect(Collectors.toList());

        List<Faq> faqs = faqRepository.findAllByTemplateVersionIdOrderByIndex(originTemplateVersion.getId());
        List<Faq> newFaqs = new ArrayList<>();
        for (Faq faq: faqs){
            Faq newFaq = faq.toEntity(newTemplateVersion);
            newFaqs.add(newFaq);
        }
        List<Long> newFaqIds = faqRepository.saveAll(newFaqs).stream().map(Faq::getId).collect(Collectors.toList());


        // 새로운 Card Area, Chart Area, Faq Area 엔티티 생성 및 저장
        List<Field> cardAreas = fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(originTemplateVersion.getId(), 1);
        List<Field> newCardAreas = new ArrayList<>();
        for (Field cardArea: cardAreas){
            Field newCardArea = cardArea.toEntity(newTemplateVersion);
            newCardAreas.add(newCardArea);
        }
        List<Long> newCardAreaIds = fieldRepository.saveAll(newCardAreas).stream().map(Field::getId).collect(Collectors.toList());

        List<Field> chartAreas = fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(originTemplateVersion.getId(), 2);
        List<Field> newChartAreas = new ArrayList<>();
        for (Field chartArea: chartAreas){
            Field newChartArea = chartArea.toEntity(newTemplateVersion);
            newChartAreas.add(newChartArea);
        }
        List<Long> newChartAreaIds = fieldRepository.saveAll(newChartAreas).stream().map(Field::getId).collect(Collectors.toList());

        List<Field> faqAreas = fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(originTemplateVersion.getId(), 3);
        List<Field> newFaqAreas = new ArrayList<>();
        for (Field faqArea: faqAreas){
            Field newFaqArea = faqArea.toEntity(newTemplateVersion);
            newFaqAreas.add(newFaqArea);
        }
        List<Long> newFaqAreaIds = fieldRepository.saveAll(newFaqAreas).stream().map(Field::getId).collect(Collectors.toList());

        return "기존 템플릿 id, 버전 id = " + templateId + ", " + originTemplateVersion.getId() +
                "기존 카드, 차트, faq, 필드 id = " + priceCards.get(0).getId() + ", " + charts.get(0).getId() + ", " + faqs.get(0).getId() + ", " + cardAreas.get(0).getId() + ", " + chartAreas.get(0).getId() + ", " + faqAreas.get(0).getId() +
                "복제된 템플릿 id, 버전 id = " + newTemplate.getId() + ", " + newTemplateVersion.getId() +
                "복제된 카드, 차트, faq, 필드 id = " + newPriceCardIds.get(0) + ", " + newChartds.get(0) + ", " + newFaqIds.get(0) + ", " + newCardAreaIds.get(0) + ", " + newChartAreaIds.get(0) + ", " + newFaqAreaIds.get(0);
    }


    // 템플릿 퍼블리싱 (최신)
    @Transactional
    public String publishTemplate(Long templateId, User user) {
        // 버전이 가장 높은 templateVersion의 id
        Long maxVersionId = templateVersionRepository.findIdByTemplateIdMaxVersion(templateId);

        // 퍼블리싱된 templateVersion의 id 수정
        user.setPublishId(maxVersionId);

        return "퍼블리싱된 버전 id = " + maxVersionId;
    }


    // 템플릿 퍼블리싱 (버전 선택)
    @Transactional
    public String publishTemplateVS(Long versionId, User user) {
        // 퍼블리싱된 templateVersion의 id 수정
        user.setPublishId(versionId);

        return "퍼블리싱된 버전 id = " + versionId;
    }


    // 템플릿 불러오기 (퍼블리싱)
    public TemplateResponse.TemplateVSOutDTO getPublishedTemplateVS(Long userId) {
        // TODO: 로그인된 유저 정보를 사용할지?
        Long publishId = userId;

        TemplateVersion templateVersion = templateVersionRepository.findById(publishId).orElseThrow(
                () -> new Exception400("templateVersion", "존재하지 않는 TemplateVersion입니다"));
        Long versionId = templateVersion.getId();

        // 카드, 차트, faq area 정보 불러오기
        List<Field> cardArea = fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(versionId, 1);
        List<TemplateResponse.FieldResponse> cardAreaResponse = cardArea.stream().map(TemplateResponse.FieldResponse::of).collect(Collectors.toList());
        List<Field> chartArea = fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(versionId, 2);
        List<TemplateResponse.FieldResponse> chartAreaResponse = chartArea.stream().map(TemplateResponse.FieldResponse::of).collect(Collectors.toList());
        List<Field> faqArea = fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(versionId, 3);
        List<TemplateResponse.FieldResponse> faqAreaResponse = faqArea.stream().map(TemplateResponse.FieldResponse::of).collect(Collectors.toList());

        // 카드, 차트, faq 정보 불러오기
        List<PriceCard> priceCard = priceCardRepository.findAllByTemplateVersionIdOrderByIndex(versionId);
        List<TemplateResponse.PriceCardResponse> priceCardResponse = priceCard.stream().map(TemplateResponse.PriceCardResponse::of).collect(Collectors.toList());
        List<Chart> chart = chartRepository.findAllByTemplateVersionIdOrderByIndex(versionId);
        List<TemplateResponse.ChartResponse> chartResponse = chart.stream().map(TemplateResponse.ChartResponse::of).collect(Collectors.toList());
        List<Faq> faq = faqRepository.findAllByTemplateVersionIdOrderByIndex(versionId);
        List<TemplateResponse.FaqResponse> faqResponse = faq.stream().map(TemplateResponse.FaqResponse::of).collect(Collectors.toList());

        return new TemplateResponse.TemplateVSOutDTO(cardAreaResponse, chartAreaResponse, faqAreaResponse,
                priceCardResponse, chartResponse, faqResponse, templateVersion);
    }


    // 템플릿 불러오기 (버전 선택)
    public TemplateResponse.TemplateVSOutDTO getTemplateVS(Long versionId, User user) {
        TemplateVersion templateVersion = templateVersionRepository.findById(versionId).orElseThrow(
                () -> new Exception400("templateVersion", "존재하지 않는 TemplateVersion입니다"));

        // 카드, 차트, faq area 정보 불러오기
        List<Field> cardArea = fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(versionId, 1);
        List<TemplateResponse.FieldResponse> cardAreaResponse = cardArea.stream().map(TemplateResponse.FieldResponse::of).collect(Collectors.toList());
        List<Field> chartArea = fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(versionId, 2);
        List<TemplateResponse.FieldResponse> chartAreaResponse = chartArea.stream().map(TemplateResponse.FieldResponse::of).collect(Collectors.toList());
        List<Field> faqArea = fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(versionId, 3);
        List<TemplateResponse.FieldResponse> faqAreaResponse = faqArea.stream().map(TemplateResponse.FieldResponse::of).collect(Collectors.toList());

        // 카드, 차트, faq 정보 불러오기
        List<PriceCard> priceCard = priceCardRepository.findAllByTemplateVersionIdOrderByIndex(versionId);
        List<TemplateResponse.PriceCardResponse> priceCardResponse = priceCard.stream().map(TemplateResponse.PriceCardResponse::of).collect(Collectors.toList());
        List<Chart> chart = chartRepository.findAllByTemplateVersionIdOrderByIndex(versionId);
        List<TemplateResponse.ChartResponse> chartResponse = chart.stream().map(TemplateResponse.ChartResponse::of).collect(Collectors.toList());
        List<Faq> faq = faqRepository.findAllByTemplateVersionIdOrderByIndex(versionId);
        List<TemplateResponse.FaqResponse> faqResponse = faq.stream().map(TemplateResponse.FaqResponse::of).collect(Collectors.toList());

        return new TemplateResponse.TemplateVSOutDTO(cardAreaResponse, chartAreaResponse, faqAreaResponse, priceCardResponse, chartResponse, faqResponse, templateVersion);
    }


    // 템플릿 버전(히스토리) 삭제
    @Transactional
    public String deleteTemplateVS(TemplateRequest.DeleteInDTO deleteInDTO, User user) {

        for (Long id: deleteInDTO.getId()){
            TemplateVersion templateVersion = templateVersionRepository.findById(id).orElseThrow(
                    () -> new Exception400("templateVersion", "존재하지 않는 TemplateVersion입니다"));
            templateVersion.setDeleted(true);
        }
        return "삭제 버전 id = " + deleteInDTO.getId() + " 삭제 완료";
    }


    // 템플릿 삭제 (버전 포함 템플릿 자체 삭제)
    public String deleteTemplate(Long templateId, User user) {
        Template template = templateRepository.findById(templateId).orElseThrow(
                () -> new Exception400("template", "존재하지 않는 Template입니다"));
        template.setDeleted(true);

        List<TemplateVersion> templateVersions = templateVersionRepository.findAllByTemplateId(templateId);
        for (TemplateVersion templateVersion: templateVersions){
            templateVersion.setDeleted(true);
        }
        return "삭제 템플릿 id = " + templateId + " 삭제 완료";
    }


}