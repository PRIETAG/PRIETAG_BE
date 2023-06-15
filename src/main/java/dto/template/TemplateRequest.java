package dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.*;

import javax.validation.constraints.NotNull;
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

        public Template toEntity(User user){
            return Template.builder()
                    .user(user)
                    .mainTitle(this.templateName)
                    .build();
        }

        public TemplateVersion toTemplateVersionEntity(Integer version){
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
                    .build();
        }
        public List<PriceCard> toPriceCardEntity(){
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

        public List<Chart> toChartEntity(){
            return chart.stream()
                    .map(chart -> Chart.builder()
                            .chartNum(chart.getChartNum())
                            .feature(chart.getFeature())
                            .desc(chart.getDesc())
                            .build())
                    .collect(Collectors.toList());
        }
        public List<Faq> toFaqEntity(){
            return faq.stream()
                    .map(faq -> Faq.builder()
                            .question(faq.getQuestion())
                            .answer(faq.getDesc())
                            .build())
                    .collect(Collectors.toList());
        }

        public List<Field> toCardAreaEntity(){
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
        public List<Field> toChartAreaEntity(){
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
        public List<Field> toFaqAreaEntity(){
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
        public static class AreaRequest{
            @NotNull
            private Field.Role role;
            private String content;
        }

        @Getter
        public static class PriceCardRequest{
            private String title;
            private Integer price;
            private Integer discountRate;
            private String detail;
            private String feature;
            private List<String> content;
        }

        @Getter
        public static class ChartRequest{
            @NotNull
            private Integer chartNum;
            @NotNull
            private String feature;
            @NotNull
            private List<String> desc;
        }

        @Getter
        public static class FaqRequest{
            @NotNull
            private String question;
            @NotNull
            private String desc;
        }

        @Getter
        public static class HeadDiscount{
            private Integer headCount;
            private Integer discountRate;
        }
    }

}
