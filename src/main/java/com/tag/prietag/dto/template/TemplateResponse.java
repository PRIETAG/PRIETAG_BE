package com.tag.prietag.dto.template;

import com.tag.prietag.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.Hibernate;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TemplateResponse {

    //Templates 조회
    @Getter
    @AllArgsConstructor @Builder
    public static class getTemplatesOutDTO{

        Long totalCount;
        List<TemplateReq> template;
        @Getter
        public static class TemplateReq {
            private Long id;
            private String title;
            private String updated_at;
            private String image;
            private boolean isPublished;

            @Builder
            public TemplateReq(Long id, String title, ZonedDateTime updated_at, String image, boolean isPublished) {
                this.id = id;
                this.title = title;
                this.updated_at = updated_at.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
                this.image = image;
                this.isPublished = isPublished;
            }
        }
    }

    @Getter
    @AllArgsConstructor @Builder
    public static class getTemplatesVSOutDTO{

        Long totalCount;
        List<TemplateVsReq> template;

        @Getter
        public static class TemplateVsReq {
            private Long id;
            private String title;
            private String updated_at;

            @Builder
            public TemplateVsReq(Long id, String title, ZonedDateTime updated_at) {
                this.id = id;
                this.title = title;
                this.updated_at = updated_at.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
            }
        }

    }

    // 템플릿 버전 (카드, 차트, faq 등 포함)
    @Getter
    public static class TemplateVSOutDTO{

        private List<TemplateResponse.FieldResponse> cardArea;
        private List<TemplateResponse.FieldResponse> chartArea;
        private List<TemplateResponse.FieldResponse> faqArea;

        private List<PriceCardResponse> priceCard;
        private List<ChartResponse> chart;
        private List<FaqResponse> faq;

        private String mainColor;
        private List<String> subColor;
        private String font;
        private String logoImageUrl;
        private String previewUrl;
        private List<Integer> padding;
        private String versionTitle;
        private boolean isCheckPerPerson;
        private List<TemplateRequest.SaveInDTO.HeadDiscount> headDiscount;
        private boolean isCheckPerYear;
        private Integer yearDiscountRate;
        private boolean isCardSet;
        private Integer priceCardAreaPadding;
        private Integer priceCardDetailMaxHeight;

        private Integer highLightIndex;
        private String pricing;
        private boolean isCardHighLight;
        private Integer cardMaxHeight;


        @Builder
        public TemplateVSOutDTO(List<TemplateResponse.FieldResponse> cardArea, List<TemplateResponse.FieldResponse> chartArea, List<TemplateResponse.FieldResponse> faqArea,
                                List<PriceCardResponse> priceCard, List<ChartResponse> chart, List<FaqResponse> faq,
                                TemplateVersion templateVersion) {
            this.cardArea = cardArea;
            this.chartArea = chartArea;
            this.faqArea = faqArea;
            this.priceCard = priceCard;
            this.chart = chart;
            this.faq = faq;
            this.mainColor = templateVersion.getMainColor();
            this.subColor = List.of(templateVersion.getSubColor1(), templateVersion.getSubColor2());
            this.font = templateVersion.getFont();
            this.logoImageUrl = templateVersion.getLogoImageUrl();
            this.previewUrl = templateVersion.getPreviewUrl();
            this.padding = List.of(templateVersion.getPadding1(), templateVersion.getPadding2());
            // 버전 타이틀이 이름 맞나?
            this.versionTitle = templateVersion.getVersionTitle();
            this.isCheckPerPerson = templateVersion.isCheckPerPerson();

            // Lazy loading 해제
            Hibernate.initialize(templateVersion.getHeadCount());
            Hibernate.initialize(templateVersion.getHeadDiscountRate());

            List<TemplateRequest.SaveInDTO.HeadDiscount> headDiscount = new ArrayList<>();
            for (int i = 0; i < templateVersion.getHeadCount().size(); i++) {
                TemplateRequest.SaveInDTO.HeadDiscount ins = TemplateRequest.SaveInDTO.HeadDiscount
                        .builder()
                        .headCount(templateVersion.getHeadCount().get(i))
                        .discountRate(templateVersion.getHeadDiscountRate().get(i))
                        .build();
                headDiscount.add(ins);
            }
            this.headDiscount = headDiscount;
            this.isCheckPerYear = templateVersion.isCheckPerYear();
            this.yearDiscountRate = templateVersion.getYearDiscountRate();
            this.isCardSet = templateVersion.isCardSet();
            this.priceCardAreaPadding = templateVersion.getPriceCardAreaPadding();
            this.priceCardDetailMaxHeight = templateVersion.getPriceCardDetailMaxHeight();
            this.highLightIndex = templateVersion.getHighLightIndex();
            this.isCardHighLight = templateVersion.isCardHighLight();
            this.pricing = templateVersion.getPricing();
            this.cardMaxHeight = templateVersion.getCardMaxHeight();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class FieldResponse {
        private Long id;
        Long templateVersionId;
        private Integer index;
        private Integer areaNum;
        private Field.Role role;
        private String desc;
        public static FieldResponse of(Field field) {
            return FieldResponse.builder()
                    .id(field.getId())
                    .templateVersionId(field.getTemplateVersion().getId())
                    .index(field.getIndex())
                    .areaNum(field.getAreaNum())
                    .role(field.getRole())
                    .desc(field.getDescription())
                    .build();
        }
        public enum Role{
            TITLE,SUBTITLE,TEXT,PADDING
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class PriceCardResponse {
        private Long id;
        private Integer index;
        Long templateVersionId;
        private String cardTitle;
        private Integer price;
        private Integer discountRate;
        private String detail;
        private String feature;
        private List<String> content;
        public static PriceCardResponse of(PriceCard priceCard) {
            return PriceCardResponse.builder()
                    .id(priceCard.getId())
                    .templateVersionId(priceCard.getTemplateVersion().getId())
                    .index(priceCard.getIndex())
                    .cardTitle(priceCard.getCardTitle())
                    .price(priceCard.getPrice())
                    .discountRate(priceCard.getDiscountRate())
                    .detail(priceCard.getDetail())
                    .feature(priceCard.getFeature())
                    .content(priceCard.getContent())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ChartResponse {
        private Long id;
        Long templateVersionId;
        private boolean haveHeader;
        private String featureName;
        private Integer chartNum;
        private Integer index;
        private String feature;
        private List<String> desc;
        public static ChartResponse of(Chart chart) {
            return ChartResponse.builder()
                    .id(chart.getId())
                    .templateVersionId(chart.getTemplateVersion().getId())
                    .haveHeader(chart.isHaveHeader())
                    .featureName(chart.getFeatureName())
                    .chartNum(chart.getChartNum())
                    .index(chart.getIndex())
                    .feature(chart.getFeature())
                    .desc(chart.getDescription())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class FaqResponse {
        private Long id;
        Long templateVersionId;
        private Integer index;
        private String question;
        private String answer;
        public static FaqResponse of(Faq faq) {
            return FaqResponse.builder()
                    .id(faq.getId())
                    .templateVersionId(faq.getTemplateVersion().getId())
                    .index(faq.getIndex())
                    .question(faq.getQuestion())
                    .answer(faq.getAnswer())
                    .build();
        }
    }
}
