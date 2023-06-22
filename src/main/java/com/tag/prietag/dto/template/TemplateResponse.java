package com.tag.prietag.dto.template;

import com.tag.prietag.model.*;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TemplateResponse {

    //Templates 조회
    @Getter
    public static class getTemplatesOutDTO{

        private Long id;
        private String title;
        private String updated_at;
        private String image;
        private boolean isPublished;

        @Builder
        public getTemplatesOutDTO(Long id, String title, ZonedDateTime updated_at, String image, boolean isPublished) {
            this.id = id;
            this.title = title;
            this.updated_at = updated_at.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
            this.image = image;
            this.isPublished = isPublished;
        }
    }

    @Getter
    public static class getTemplatesVSOutDTO{

        private Long id;
        private String title;
        private String updated_at;

        @Builder
        public getTemplatesVSOutDTO(Long id, String title, ZonedDateTime updated_at) {
            this.id = id;
            this.title = title;
            this.updated_at = updated_at.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
        }
    }

    // 템플릿 버전 (카드, 차트, faq 등 포함)
    @Getter
    public static class TemplateVSOutDTO{

        private List<Field> cardArea;
        private List<Field> chartArea;
        private List<Field> faqArea;

        private List<PriceCard> priceCard;
        private List<Chart> chart;
        private List<Faq> faq;

        private String mainColor;
        private List<String> subColor;
        private String font;
        private String logoImageUrl;
        private String previewUrl;
        private List<Integer> padding;
        private String templateName;
        private boolean isCheckPerPerson;
        private List<TemplateRequest.SaveInDTO.HeadDiscount> headDiscount;
        private boolean isCheckPerYear;
        private Integer yearDiscountRate;
        private boolean isCardSet;
        private Integer priceCardAreaPadding;
        private Integer priceCardDetailMaxHeight;


        @Builder
        public TemplateVSOutDTO(List<Field> cardArea, List<Field> chartArea, List<Field> faqArea,
                                List<PriceCard> priceCard, List<Chart> chart, List<Faq> faq,
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
            this.templateName = templateVersion.getVersionTitle();
            this.isCheckPerPerson = templateVersion.isCheckPerPerson();
            List<TemplateRequest.SaveInDTO.HeadDiscount> headDiscount = null;
            for (int i = 0; i < templateVersion.getHeadCount().size(); i++) {
                TemplateRequest.SaveInDTO.HeadDiscount ins = new TemplateRequest.SaveInDTO.HeadDiscount(templateVersion.getHeadCount().get(i), templateVersion.getHeadDiscountRate().get(i));
                headDiscount.add(ins);
            }
            this.headDiscount = headDiscount;
            this.isCheckPerYear = templateVersion.isCheckPerYear();
            this.yearDiscountRate = templateVersion.getYearDiscountRate();
            this.isCardSet = templateVersion.isCardSet();
            this.priceCardAreaPadding = templateVersion.getPriceCardAreaPadding();
            this.priceCardDetailMaxHeight = templateVersion.getPriceCardDetailMaxHeight();
        }
    }
}
