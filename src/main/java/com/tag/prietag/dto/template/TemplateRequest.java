package com.tag.prietag.dto.template;

import com.tag.prietag.model.*;
import lombok.Getter;

import javax.validation.constraints.NotNull;
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

        private String mainColor;
        private List<String> subColor;
        private String font;

        private String logoImageUrl;

        private List<Integer> padding;
        private String templateName;

        private boolean isCheckPerPerson;
        private List<HeadDiscount> headDiscount;

        private boolean isCheckPerYear;
        private Integer yearDiscountRate;

        private boolean isCardSet;
        private Integer priceCardAreaPadding;

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
                    .logoImageUrl(this.logoImageUrl)
                    .padding(this.padding)
                    .isCheckPerPerson(this.isCheckPerPerson)
                    .headCount(this.headDiscount.stream().map(headCount -> headCount.getHeadCount()).collect(Collectors.toList()))
                    .headDiscountRate(this.headDiscount.stream().map(headDiscount -> headDiscount.getDiscountRate()).collect(Collectors.toList()))
                    .isCheckPerYear(this.isCheckPerYear)
                    .yearDiscountRate(this.yearDiscountRate)
                    .isCardSet(this.isCardSet)
                    .priceCardAreaPadding(this.priceCardAreaPadding)
                    .build();
        }

        public List<PriceCard> toPriceCardEntity() {
            return priceCard.stream()
                    .map(card -> PriceCard.builder()
                            .cardTitle(card.getTitle())
                            .price(card.getPrice())
                            .discountRate(card.getDiscountRate())
                            .detail(card.getDetail())
                            .feature(card.getFeature())
                            .content(card.getContent())
                            .build())
                    .collect(Collectors.toList());
        }

        public List<Chart> toChartEntity() {
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
            return faq.stream()
                    .map(faq -> Faq.builder()
                            .question(faq.getQuestion())
                            .answer(faq.getDesc())
                            .build())
                    .collect(Collectors.toList());
        }

        public List<Field> toCardAreaEntity() {
            AtomicInteger index = new AtomicInteger(1);
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
            AtomicInteger index = new AtomicInteger(1);
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
            AtomicInteger index = new AtomicInteger(1);
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
        public static class AreaRequest {
            @NotNull
            private Field.Role role;
            private String content;
        }

        @Getter
        public static class PriceCardRequest {
            private String title;
            private Integer price;
            private Integer discountRate;
            private String detail;
            private String feature;
            private List<String> content;
        }

        @Getter
        public static class ChartRequest {
            @NotNull
            private boolean haveHeader;
            private String featureName;
            private List<TableRequest> table;
        }

        @Getter
        public static class TableRequest {
            @NotNull
            private String feature;
            @NotNull
            private List<String> desc;
        }

        @Getter
        public static class FaqRequest {
            @NotNull
            private String question;
            @NotNull
            private String desc;
        }

        @Getter
        public static class HeadDiscount {
            private Integer headCount;
            private Integer discountRate;
        }
    }

}
