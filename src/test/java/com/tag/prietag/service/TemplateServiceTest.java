package com.tag.prietag.service;

import com.tag.prietag.core.exception.Exception400;
import com.tag.prietag.dto.template.TemplateRequest;
import com.tag.prietag.dto.template.TemplateResponse;
import com.tag.prietag.model.*;
import com.tag.prietag.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class TemplateServiceTest {

    @InjectMocks
    private TemplateService templateService;

    @Mock
    private FieldRopository fieldRepository;
    @Mock
    private PriceCardRepository priceCardRepository;
    @Mock
    private ChartRepository chartRepository;
    @Mock
    private FaqRepository faqRepository;
    @Mock
    private TemplateRepository templateRepository;
    @Mock
    private TemplateVersionRepository templateVersionRepository;
    @Mock
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(TemplateServiceTest.class);


    User user;
    Template template;
    TemplateVersion templateVersion;
    @BeforeEach
    void setUp(){
        user = User.builder()
                .id(1L)
                .email("dasfd@naver.com")
                .username("Lee")
                .publishId(1L)
                .role(User.Role.USER)
                .build();
        template = Template.builder()
                .id(1L)
                .mainTitle("내가 만듬")
                .user(user)
                .build();

        templateVersion = TemplateVersion.builder()
                .id(1L)
                .template(template)
                .version(1)
                .versionTitle(template.getMainTitle())
                .mainColor("#3214214")
                .subColor(new ArrayList<>(List.of("#312f11", "#fdas2f")))
                .font("dsa")
                .logoImageUrl("dafdafa.jpg")
                .padding(new ArrayList<>(List.of(400, 300)))
                .isCheckPerPerson(true)
                .headCount(List.of(5, 8, 10))
                .headDiscountRate(List.of(10, 20, 30))
                .isCheckPerYear(true)
                .yearDiscountRate(30)
                .isCardSet(true)
                .priceCardAreaPadding(20)
                .template(template)
                .updateAt(ZonedDateTime.now())
                .priceCardAreaPadding(300)
                .priceCardDetailMaxHeight(400)
                .updateAt(ZonedDateTime.now())
                .build();
    }

    TemplateRequest.SaveInDTO getSaveInDTO(String mainTilte) {
        List<TemplateRequest.SaveInDTO.HeadDiscount> headDiscountList = new ArrayList<>();
        headDiscountList.add(new TemplateRequest.SaveInDTO.HeadDiscount(4, 30));
        headDiscountList.add(new TemplateRequest.SaveInDTO.HeadDiscount(7, 50));

        List<TemplateRequest.SaveInDTO.PriceCardRequest> priceCardRequests = new ArrayList<>();
        priceCardRequests.add(TemplateRequest.SaveInDTO.PriceCardRequest.builder()
                .price(19999)
                .title("FREE")
                .discountRate(0)
                .detail("공짜!")
                .feature("시작")
                .content(List.of("fafda", "fdafaf"))
                .build());
        List<TemplateRequest.SaveInDTO.AreaRequest> cardAreaRequests = new ArrayList<>();
        cardAreaRequests.add(TemplateRequest.SaveInDTO.AreaRequest.builder()
                .role(Field.Role.TITLE)
                .content("dafda")
                .build());
        cardAreaRequests.add(TemplateRequest.SaveInDTO.AreaRequest.builder()
                .role(Field.Role.PADDING)
                .content("19")
                .build());
        cardAreaRequests.add(TemplateRequest.SaveInDTO.AreaRequest.builder()
                .role(Field.Role.SUBTITLE)
                .content("ddddda")
                .build());

        return TemplateRequest.SaveInDTO.builder()
                .isCardSet(true)
                .font("dsa")
                .isCheckPerPerson(true)
                .logoImageUrl("dafdafa.jpg")
                .isCheckPerYear(true)
                .mainColor("#3214214")
                .subColor(new ArrayList<>(List.of("#312f11", "#fdas2f")))
                .yearDiscountRate(30)
                .templateName(mainTilte)
                .padding(new ArrayList<>(List.of(400, 300)))
                .headDiscount(headDiscountList)
                .priceCard(priceCardRequests)
                .priceCardArea(cardAreaRequests)
                .build();
    }


    @Nested
    @DisplayName("Template 생성")
    class CreateTemplate {
        @Test
        @DisplayName("중복되는 TemplateName이 있음")
        void fail() {
            createTemplateSetting();
            TemplateRequest.SaveInDTO saveInDTO = getSaveInDTO("내가 만듬");
            //when then
            Assertions.assertThrows(Exception400.class, () -> templateService.createTemplate(saveInDTO, user));
        }

        @Test
        @DisplayName("성공")
        void success(){
            createTemplateSetting();
            TemplateRequest.SaveInDTO saveInDTO = getSaveInDTO("내가 안만듬");
            List<PriceCard> priceCardRequests = saveInDTO.toPriceCardEntity();

            Assertions.assertDoesNotThrow(() -> templateService.createTemplate(saveInDTO, user));
        }

        void createTemplateSetting(){
            lenient().when(templateRepository.findByTemplateName(anyString()))
                    .thenAnswer(invocation -> {
                        String templateName = invocation.getArgument(0);
                        if (template.getMainTitle().equals(templateName)) throw new Exception400("templateName", "중복되는 TemplateTitle이 있습니다");
                        return Optional.empty();
                    });
        }
    }

    @Nested
    @DisplayName("Template 목록 조회")
    class GetTemplates {
        @Test
        @DisplayName("성공")
        void success() {
            //given
            Pageable pageable = PageRequest.of(0, 4);
            Pageable pageableLimitOne = PageRequest.of(0, 1);
            getTemplatesSetting(pageable);

            //when
            templateService.getTemplates(user ,pageable);

            //then
            verify(templateRepository, times(1)).findByUserId(user.getId(), pageable);
            verify(templateVersionRepository, times(1)).findByTemplateIdLimitOne(template.getId(), pageableLimitOne);
            verify(templateVersionRepository, times(1)).findByPublishTemplateId(user.getPublishId(), template.getId());

            Assertions.assertDoesNotThrow(() -> templateService.getTemplates(user, pageable));
        }

        void getTemplatesSetting(Pageable pageable){
            lenient().when(templateRepository.findByUserId(anyLong(), eq(pageable)))
                    .thenReturn( new PageImpl<>(List.of(template),pageable,1));

            Pageable pageableLimitOne = PageRequest.of(0, 1);
            lenient().when(templateVersionRepository.findByTemplateIdLimitOne(anyLong() ,eq(pageableLimitOne)))
                    .thenReturn( new PageImpl<>(List.of(templateVersion), pageableLimitOne,1));

            lenient().when(templateVersionRepository.findByPublishTemplateId(anyLong() ,anyLong()))
                    .thenAnswer(invocation -> {
                        Long publishId = invocation.getArgument(0);
                        Long templateId = invocation.getArgument(1);
                        if (user.getPublishId().equals(publishId) && templateVersion.getTemplate().getId().equals(templateId))
                            return Optional.of(templateVersion);
                        return Optional.empty();
                    });
        }
    }

    @Nested
    @DisplayName("Template 목록 조회")
    class GetTemplatesVS {

        @Test
        @DisplayName("존재하지 않는 Template")
        void fail() {
            Pageable pageable = PageRequest.of(0, 10);
            getTemplatesVSSetting(pageable);
            //when then
            Assertions.assertThrows(Exception400.class, () -> templateService.getTemplatesVS(2L, pageable));
        }

        @Test
        @DisplayName("성공")
        void success() {
            //given
            Pageable pageable = PageRequest.of(0, 10);
            getTemplatesVSSetting(pageable);

            //when
            templateService.getTemplatesVS(template.getId(), pageable);

            //then
            verify(templateRepository, times(1)).findById(template.getId());
            verify(templateVersionRepository, times(1)).findByTemplateId(template.getId(), pageable);

            Assertions.assertDoesNotThrow(() -> templateService.getTemplatesVS(template.getId(), pageable));
        }

        void getTemplatesVSSetting(Pageable pageable){
            lenient().when(templateRepository.findById(anyLong()))
                    .thenAnswer( invocation -> {
                        Long templateId = invocation.getArgument(0);
                        if(!template.getId().equals(templateId)) throw new Exception400("template", "존재하지 않는 Template입니다");
                        return Optional.of(template);
                    });

            lenient().when(templateVersionRepository.findByTemplateId(anyLong() ,eq(pageable)))
                    .thenReturn( new PageImpl<>(List.of(templateVersion), pageable,1));

        }
    }


    @Nested
    @DisplayName("TemplateVersion 생성")
    class CreateTemplateVersion {
        @Test
        @DisplayName("정상적으로 TemplateVersion 생성")
        void success() {
            // given
            Long templateId = template.getId();  // 존재하는 templateId 설정
            TemplateRequest.SaveInDTO saveInDTO = getSaveInDTO("그가 만듦");  // 적절한 saveInDTO 설정

            // mock repository 객체 생성 및 설정
            Mockito.when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));  // findById() 메서드 mock 설정
            Mockito.when(templateVersionRepository.findMaxVersionByTemplateId(templateId)).thenReturn(0);  // findMaxVersionByTemplateId() 메서드 mock 설정
            Mockito.when(templateVersionRepository.save(Mockito.any(TemplateVersion.class))).thenAnswer(invocation -> invocation.getArgument(0));  // save() 메서드 mock 설정
            Mockito.when(priceCardRepository.saveAll(Mockito.anyList())).thenAnswer(invocation -> invocation.getArgument(0));  // saveAll() 메서드 mock 설정
            Mockito.when(chartRepository.saveAll(Mockito.anyList())).thenAnswer(invocation -> invocation.getArgument(0));  // saveAll() 메서드 mock 설정
            Mockito.when(faqRepository.saveAll(Mockito.anyList())).thenAnswer(invocation -> invocation.getArgument(0));  // saveAll() 메서드 mock 설정
            Mockito.when(fieldRepository.saveAll(Mockito.anyList())).thenAnswer(invocation -> invocation.getArgument(0));  // saveAll() 메서드 mock 설정

            // when
            templateService.createTemplateVS(templateId, saveInDTO, user);

            // then
            // TemplateVersion, PriceCard, Chart, Faq, Card Area, Chart Area, Faq Area에 대한 추가적인 검증 수행
            Mockito.verify(templateVersionRepository, Mockito.times(1)).save(Mockito.any(TemplateVersion.class));
            Mockito.verify(priceCardRepository, Mockito.times(1)).saveAll(Mockito.anyList());
            Mockito.verify(chartRepository, Mockito.times(1)).saveAll(Mockito.anyList());
            Mockito.verify(faqRepository, Mockito.times(1)).saveAll(Mockito.anyList());
            Mockito.verify(fieldRepository, Mockito.times(3)).saveAll(Mockito.anyList());
        }

        @Test
        @DisplayName("Template 존재하지 않을 때")
        void templateNotFound() {
            // given
            Long templateId = 1L;  // 존재하지 않는 templateId 설정
            TemplateRequest.SaveInDTO saveInDTO = getSaveInDTO("그가 만듦");  // 적절한 saveInDTO 설정

            // when, then
            Assertions.assertThrows(Exception400.class, () -> templateService.createTemplateVS(templateId, saveInDTO, user));
        }

        @Test
        @DisplayName("해당 Template에 대한 권한이 없을 때")
        void insufficientPermission() {
            // given
            Long templateId = template.getId();  // 존재하는 templateId 설정
            TemplateRequest.SaveInDTO saveInDTO = getSaveInDTO("그가 만듦");  // 적절한 saveInDTO 설정
            User otherUser = User.builder()
                    .id(3L)
                    .email("abcdd@naver.com")
                    .username("see")
                    .publishId(1L)
                    .role(User.Role.USER)
                    .build();

            // when, then
            Assertions.assertThrows(Exception400.class, () -> templateService.createTemplateVS(templateId, saveInDTO, otherUser));
        }
    }

    @Nested
    @DisplayName("템플릿 복제")
    class CopyTemplate {
        @Test
        @DisplayName("복제할 Template이 존재하지 않을 때")
        void templateNotFound() {
            // given
            Long templateId = 3L;  // 존재하지 않는 templateId 설정

            // when, then
            Assertions.assertThrows(Exception400.class, () -> templateService.copyTemplate(templateId, user));
        }

        @Test
        @DisplayName("정상적으로 Template 복제")
        void success() {
            // given
            Long templateId = template.getId();  // 존재하는 templateId 설정

            // mock repository 객체 생성 및 설정
            Template originTemplate = new Template();  // mock으로 사용할 Origin Template 객체 생성
            Mockito.when(templateRepository.findById(templateId)).thenReturn(Optional.of(originTemplate));  // findById() 메서드 mock 설정

            TemplateVersion originTemplateVersion = templateVersion;  // mock으로 사용할 Origin TemplateVersion 객체 생성
            Mockito.when(templateVersionRepository.findMaxVersionTemplate(templateId)).thenReturn(originTemplateVersion);  // findMaxVersionTemplate() 메서드 mock 설정

            Template newTemplate = new Template();  // mock으로 사용할 New Template 객체 생성
            Mockito.when(templateRepository.save(Mockito.any(Template.class))).thenReturn(newTemplate);  // save() 메서드 mock 설정

            TemplateVersion newTemplateVersion = new TemplateVersion();  // mock으로 사용할 New TemplateVersion 객체 생성
            Mockito.when(templateVersionRepository.save(Mockito.any(TemplateVersion.class))).thenReturn(newTemplateVersion);  // save() 메서드 mock 설정

            Mockito.when(priceCardRepository.findAllByTemplateVersionIdOrderByIndex(originTemplateVersion.getId())).thenReturn(Collections.emptyList());  // findAllByTemplateVersionIdOrderByIndex() 메서드 mock 설정
            Mockito.when(chartRepository.findAllByTemplateVersionIdOrderByIndex(originTemplateVersion.getId())).thenReturn(Collections.emptyList());  // findAllByTemplateVersionIdOrderByIndex() 메서드 mock 설정
            Mockito.when(faqRepository.findAllByTemplateVersionIdOrderByIndex(originTemplateVersion.getId())).thenReturn(Collections.emptyList());  // findAllByTemplateVersionIdOrderByIndex() 메서드 mock 설정
            Mockito.when(fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(originTemplateVersion.getId(), 1)).thenReturn(Collections.emptyList());  // findAllByTemplateVersionIdAndAreaNumOrderByIndex() 메서드 mock 설정
            Mockito.when(fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(originTemplateVersion.getId(), 2)).thenReturn(Collections.emptyList());  // findAllByTemplateVersionIdAndAreaNumOrderByIndex() 메서드 mock 설정
            Mockito.when(fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(originTemplateVersion.getId(), 3)).thenReturn(Collections.emptyList());  // findAllByTemplateVersionIdAndAreaNumOrderByIndex() 메서드 mock 설정

            // when
            templateService.copyTemplate(templateId, user);

            // then
            // Template, TemplateVersion, PriceCard, Chart, Faq, Card Area, Chart Area, Faq Area에 대한 추가적인 검증이 이루어져야 합니다.
            Mockito.verify(templateRepository, Mockito.times(1)).save(Mockito.any(Template.class));
            Mockito.verify(templateVersionRepository, Mockito.times(1)).save(Mockito.any(TemplateVersion.class));
            Mockito.verify(priceCardRepository, Mockito.times(1)).saveAll(Mockito.anyList());
            Mockito.verify(chartRepository, Mockito.times(1)).saveAll(Mockito.anyList());
            Mockito.verify(faqRepository, Mockito.times(1)).saveAll(Mockito.anyList());
            Mockito.verify(fieldRepository, Mockito.times(3)).saveAll(Mockito.anyList());
        }
    }


    @Nested
    @DisplayName("템플릿 퍼블리싱")
    class PublishTemplate {
        @Test
        @DisplayName("정상적으로 Template 퍼블리싱")
        void success() {
            // given
            Long templateId = template.getId();  // 존재하는 templateId 설정

            Long maxVersionId = 2L;  // 가장 높은 버전의 templateVersionId 설정
            Mockito.when(templateVersionRepository.findIdByTemplateIdMaxVersion(templateId)).thenReturn(maxVersionId);  // findIdByTemplateIdMaxVersion() 메서드 mock 설정
            logger.debug("findIdByTemplateIdMaxVersion() : " + templateVersionRepository.findIdByTemplateIdMaxVersion(templateId));
            // when
            templateService.publishTemplate(templateId, user);

            // then
            Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
            Assertions.assertEquals(maxVersionId, user.getPublishId());
        }
    }


    @Nested
    @DisplayName("템플릿 퍼블리싱 (버전 선택)")
    class PublishTemplateVS {
        @Test
        @DisplayName("정상적으로 Template 버전 퍼블리싱")
        void success() {
            // given
            Long versionId = templateVersion.getId();  // 존재하는 versionId 설정

            // when
            templateService.publishTemplateVS(versionId, user);

            // then
            Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
            Assertions.assertEquals(versionId, user.getPublishId());
        }
    }


    @Test
    @DisplayName("퍼블리싱된 템플릿 불러오기 - 성공")
    void getPublishedTemplateVSSuccess() {
        // given
        Long userId = user.getId();  // 존재하는 userId 설정
        user.setPublishId(1L);  // 가장 높은 버전의 templateVersionId 설정

        Long versionId = 1L;  // 테스트에 사용할 versionId 설정
        Integer version = 1;  // 테스트에 사용할 version 설정

        // 템플릿 버전 mock 설정
        TemplateVersion templateVersionMock = getSaveInDTO("test").toTemplateVersionEntity(version);
        // templateVersionMock에서 필요한 속성들 설정

        // 카드, 차트, faq area 정보 mock 설정
        List<Field> cardAreaMock = getSaveInDTO("test").toCardAreaEntity();
        // cardAreaMock에 필요한 필드 객체들 추가

        // 카드, 차트, faq 정보 mock 설정
        List<PriceCard> priceCardMock = getSaveInDTO("test").toPriceCardEntity();
        // priceCardMock에 필요한 PriceCard 객체들 추가


        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));  // userRepository.findById() 메서드 mock 설정
        Mockito.when(templateVersionRepository.findById(versionId)).thenReturn(Optional.of(templateVersionMock));  // templateVersionRepository.findById() 메서드 mock 설정
        Mockito.when(fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex(versionId, 1)).thenReturn(cardAreaMock);  // fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex() 메서드 mock 설정
        Mockito.when(priceCardRepository.findAllByTemplateVersionIdOrderByIndex(versionId)).thenReturn(priceCardMock);  // priceCardRepository.findAllByTemplateVersionIdOrderByIndex() 메서드 mock 설정

        // when
        TemplateResponse.TemplateVSOutDTO result = templateService.getPublishedTemplateVS(userId, user);

        // then
        Assertions.assertNotNull(result);

        // TemplateResponse.TemplateVSOutDTO의 속성들을 검증
        Assertions.assertEquals(cardAreaMock, result.getCardArea());
        Assertions.assertEquals(priceCardMock, result.getPriceCard());
        //Assertions.assertEquals(templateVersionMock, result.getTemplateVersion());

        // fieldRepository.findAllByTemplateVersionIdAndAreaNumOrderByIndex() 메서드의 호출 횟수 및 인자 검증
        Mockito.verify(fieldRepository, Mockito.times(1)).findAllByTemplateVersionIdAndAreaNumOrderByIndex(versionId, 1);
        Mockito.verify(fieldRepository, Mockito.times(1)).findAllByTemplateVersionIdAndAreaNumOrderByIndex(versionId, 2);
        Mockito.verify(fieldRepository, Mockito.times(1)).findAllByTemplateVersionIdAndAreaNumOrderByIndex(versionId, 3);

        // priceCardRepository.findAllByTemplateVersionIdOrderByIndex() 메서드의 호출 횟수 및 인자 검증
        Mockito.verify(priceCardRepository, Mockito.times(1)).findAllByTemplateVersionIdOrderByIndex(versionId);

        // chartRepository.findAllByTemplateVersionIdOrderByIndex() 메서드의 호출 횟수 및 인자 검증
        Mockito.verify(chartRepository, Mockito.times(1)).findAllByTemplateVersionIdOrderByIndex(versionId);

        // faqRepository.findAllByTemplateVersionIdOrderByIndex() 메서드의 호출 횟수 및 인자 검증
        Mockito.verify(faqRepository, Mockito.times(1)).findAllByTemplateVersionIdOrderByIndex(versionId);
    }




}
