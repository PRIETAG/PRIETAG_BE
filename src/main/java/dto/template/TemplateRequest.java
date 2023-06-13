package dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.Field;
import model.Template;
import model.TemplateVersion;
import model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class TemplateRequest {

    @Getter
    public static class SaveInDTO {
        private List<AreaRequest> priceCardArea;
        private List<AreaRequest> chartArea;
        private List<AreaRequest> faqArea;

        @NotNull
        private List<PriceCard> priceCard;
        private List<Chart> chart;
        private List<Faq> faq;

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

        @Getter @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class AreaRequest{
            @NotNull
            private Field.Role role;
            private String content;
        }

        @Getter @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class PriceCard{
            private String title;
            private Integer price;
            private Integer discountRate;
            private String detail;
            private String feature;
            private List<String> content;
        }

        @Getter @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Chart{
            @NotNull
            private String feature;
            @NotNull
            private List<String> desc;
        }

        @Getter @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Faq{
            @NotNull
            private String question;
            @NotNull
            private String desc;
        }

        @Getter @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class HeadDiscount{
            private Integer headCount;
            private Integer discountRate;
        }
    }

}
