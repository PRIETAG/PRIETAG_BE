package com.tag.prietag.dto.template;

import com.tag.prietag.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TemplateRequest {

    // 템플릿 저장
    @Getter
    public static class SaveInDTO {
        private List<AreaRequest> priceCardArea;
        private List<AreaRequest> chartArea;
        private List<AreaRequest> faqArea;

        @NotNull
        private List<PriceCardRequest> priceCard;
        private List<ChartRequest> chart;
        private List<FaqRequest> faq;

        @NotNull
        private String mainColor;
        @NotNull
        private List<String> subColor;
        @NotNull
        private String font;

        @NotNull
        private List<Integer> padding;
        @NotNull
        private String templateName;

        @NotNull
        private boolean isCheckPerPerson;
        private List<HeadDiscount> headDiscount;

        @NotNull
        private boolean isCheckPerYear;
        private Integer yearDiscountRate;

        @NotNull
        private boolean isCardSet;
        @NotNull
        private Integer priceCardAreaPadding;
        @NotNull
        private Integer priceCardDetailMaxHeight;


        private Integer highLightIndex;
        private String pricing;

        private boolean isCardHighLight = false;
        private Integer cardMaxHeight;

        public Template toEntity(User user) {
            return Template.builder()
                    .user(user)
                    .mainTitle(this.templateName)
                    .build();
        }

        public TemplateVersion toTemplateVersionEntity(Integer version) {
            return TemplateVersion.builder()
                    .version(version)
                    .versionTitle(this.templateName)
                    .mainColor(this.mainColor)
                    .subColor(this.subColor)
                    .font(this.font)
                    .padding(this.padding)
                    .isCheckPerPerson(this.isCheckPerPerson)
                    .headCount(this.headDiscount.isEmpty()?null:this.headDiscount.stream().map(headCount -> headCount.getHeadCount()).collect(Collectors.toList()))
                    .headDiscountRate(this.headDiscount.isEmpty()?null:this.headDiscount.stream().map(headDiscount -> headDiscount.getDiscountRate()).collect(Collectors.toList()))
                    .isCheckPerYear(this.isCheckPerYear)
                    .yearDiscountRate(this.yearDiscountRate)
                    .isCardSet(this.isCardSet)
                    .priceCardAreaPadding(this.priceCardAreaPadding)
                    .priceCardDetailMaxHeight(this.priceCardDetailMaxHeight)
                    .highLightIndex(this.highLightIndex)
                    .isCardHighLight(this.isCardHighLight)
                    .pricing(this.pricing)
                    .cardMaxHeight(this.cardMaxHeight)
                    .build();
        }

        public List<PriceCard> toPriceCardEntity() {
            if (this.priceCard == null || this.priceCard.isEmpty()) {
                return new ArrayList<>();
            }
            return this.priceCard.stream()
                    .map(card -> PriceCard.builder()
                            .index(this.priceCard.indexOf(card))
                            .cardTitle(card.getTitle())
                            .price(card.getPrice())
                            .discountRate(card.getDiscountRate())
                            .detail(card.getDetail())
                            .feature(card.getFeature())
                            .content(card.getContent())
                            .index(this.priceCard.indexOf(card))
                            .build())
                    .collect(Collectors.toList());
        }

        public List<Chart> toChartEntity() {
            if (this.chart == null || this.chart.isEmpty()) {
                return new ArrayList<>();
            }
            List<Chart> chartList = new ArrayList<>();
            for (int i = 0; i < chart.size(); i++) {
                for (int j = 0; j < chart.get(i).table.size(); j++) {
                    chartList.add(Chart.builder()
                            .chartNum(i)
                            .haveHeader(chart.get(i).isHaveHeader())
                            .featureName(chart.get(i).getFeatureName())
                            .index(j)
                            .feature(chart.get(i).table.get(j).getFeature())
                            .desc(chart.get(i).table.get(j).getDesc())
                            .build());
                }
            }
            return chartList;
        }

        public List<Faq> toFaqEntity() {
            if (this.faq == null || this.faq.isEmpty()) {
                return new ArrayList<>();
            }
            return faq.stream()
                    .map(faq -> Faq.builder()
                            .index(this.faq.indexOf(faq))
                            .question(faq.getQuestion())
                            .answer(faq.getDesc())
                            .index(this.faq.indexOf(faq))
                            .build())
                    .collect(Collectors.toList());
        }

        public List<Field> toCardAreaEntity() {
            if (this.priceCardArea == null || this.priceCardArea.isEmpty()) {
                return new ArrayList<>();
            }
            AtomicInteger index = new AtomicInteger(0);
            return priceCardArea.stream()
                    .map(area -> Field.builder()
                            .index(index.getAndIncrement())
                            .areaNum(1)
                            .role(area.getRole())
                            .desc(area.getContent())
                            .build())
                    .collect(Collectors.toList());
        }

        public List<Field> toChartAreaEntity() {
            if (this.chartArea == null || this.chartArea.isEmpty()) {
                return new ArrayList<>();
            }
            AtomicInteger index = new AtomicInteger(0);
            return chartArea.stream()
                    .map(area -> Field.builder()
                            .index(index.getAndIncrement())
                            .areaNum(2)
                            .role(area.getRole())
                            .desc(area.getContent())
                            .build())
                    .collect(Collectors.toList());
        }

        public List<Field> toFaqAreaEntity() {
            if (this.faqArea == null || this.faqArea.isEmpty()) {
                return new ArrayList<>();
            }
            AtomicInteger index = new AtomicInteger(0);
            return faqArea.stream()
                    .map(area -> Field.builder()
                            .index(index.getAndIncrement())
                            .areaNum(3)
                            .role(area.getRole())
                            .desc(area.getContent())
                            .build())
                    .collect(Collectors.toList());
        }

        @Getter
        @AllArgsConstructor
        @Builder
        public static class AreaRequest {
            @NotNull
            private Field.Role role;
            private String content;
        }

        @Getter
        @AllArgsConstructor
        @Builder
        public static class PriceCardRequest {
            private String title;
            private Integer price;
            private Integer discountRate;
            private String detail;
            private String feature;
            private List<String> content;
        }

        @Getter
        @AllArgsConstructor
        @Builder
        public static class ChartRequest {
            @NotNull
            private boolean haveHeader;
            private String featureName;
            private List<TableRequest> table;
        }

        @Getter
        @AllArgsConstructor
        @Builder
        public static class TableRequest {
            @NotNull
            private String feature;
            @NotNull
            private List<String> desc;
        }

        @Getter
        @AllArgsConstructor
        @Builder
        public static class FaqRequest {
            @NotNull
            private String question;
            @NotNull
            private String desc;
        }

        @Getter
        @AllArgsConstructor
        @Builder
        public static class HeadDiscount {
            private Integer headCount;
            private Integer discountRate;
        }

        @Builder
        public SaveInDTO(List<AreaRequest> priceCardArea, List<AreaRequest> chartArea, List<AreaRequest> faqArea, List<PriceCardRequest> priceCard, List<ChartRequest> chart, List<FaqRequest> faq, String mainColor, List<String> subColor, String font, List<Integer> padding, String templateName, boolean isCheckPerPerson, List<HeadDiscount> headDiscount, boolean isCheckPerYear, Integer yearDiscountRate, boolean isCardSet, Integer priceCardAreaPadding, Integer priceCardDetailMaxHeight) {
            this.priceCardArea = priceCardArea;
            this.chartArea = chartArea;
            this.faqArea = faqArea;
            this.priceCard = priceCard;
            this.chart = chart;
            this.faq = faq;
            this.mainColor = mainColor;
            this.subColor = subColor;
            this.font = font;
            this.padding = padding;
            this.templateName = templateName;
            this.isCheckPerPerson = isCheckPerPerson;
            this.headDiscount = headDiscount;
            this.isCheckPerYear = isCheckPerYear;
            this.yearDiscountRate = yearDiscountRate;
            this.isCardSet = isCardSet;
            this.priceCardAreaPadding = priceCardAreaPadding;
            this.priceCardDetailMaxHeight = priceCardDetailMaxHeight;
        }
    }

    @Getter
    public static class DeleteInDTO {
        private List<Long> id;
    }

}
