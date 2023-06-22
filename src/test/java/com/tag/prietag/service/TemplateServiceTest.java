package com.tag.prietag.service;

import com.tag.prietag.core.exception.Exception400;
import com.tag.prietag.dto.template.TemplateRequest;
import com.tag.prietag.model.PriceCard;
import com.tag.prietag.model.Template;
import com.tag.prietag.model.TemplateVersion;
import com.tag.prietag.model.User;
import com.tag.prietag.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                .isCardSet(true)
                .font("dsa")
                .isCheckPerPerson(true)
                .logoImageUrl("dafdafa.jpg")
                .isCheckPerYear(true)
                .mainColor("#3214214")
                .subColor(new ArrayList<>(List.of("#312f11", "#fdas2f")))
                .yearDiscountRate(30)
                .versionTitle(template.getMainTitle())
                .padding(new ArrayList<>(List.of(400, 300)))
                .headCount(List.of(5, 8, 10))
                .headDiscountRate(List.of(10, 20, 30))
                .template(template)
                .updateAt(ZonedDateTime.now())
                .priceCardAreaPadding(300)
                .priceCardDetailMaxHeight(400)
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

}
