package com.tag.prietag.model;

import com.tag.prietag.core.util.IntegerListConverter;
import com.tag.prietag.core.util.TimeStamped;
import com.tag.prietag.dto.template.TemplateRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Table(name = "templatevs_tb")
@Entity
public class TemplateVersion extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer version;
    private String versionTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    private Template template;

    @Column(nullable = false)
    private String mainColor;
    @Column(nullable = false)
    private String subColor1;
    @Column(nullable = false)
    private String subColor2;

    @Column(nullable = false)
    private String font;

    private String logoImageUrl;
    @Column(nullable = false)
    private String previewUrl;

    @Column(nullable = false)
    private Integer padding1;
    @Column(nullable = false)
    private Integer padding2;

    @Column(nullable = false)
    private boolean isCheckPerPerson;

    @Convert(converter = IntegerListConverter.class)
    private List<Integer> headCount;
    @Convert(converter = IntegerListConverter.class)
    private List<Integer> headDiscountRate;

    @Column(nullable = false)
    private boolean isCheckPerYear;
    private Integer yearDiscountRate;

    @Column(nullable = false)
    private boolean isCardSet;

    @Column(nullable = false)
    private Integer priceCardAreaPadding;

    @Column(nullable = false)
    private Integer priceCardDetailMaxHeight;

    @Column(nullable = false)
    private boolean isDeleted;

    @Builder
    public TemplateVersion(Long id, Template template, ZonedDateTime updateAt, Integer version, String versionTitle, String mainColor, List<String> subColor, String font, String logoImageUrl, String previewUrl, List<Integer> padding, boolean isCheckPerPerson, List<Integer> headCount, List<Integer> headDiscountRate, boolean isCheckPerYear, Integer yearDiscountRate, boolean isCardSet, Integer priceCardAreaPadding, Integer priceCardDetailMaxHeight) {
        this.id = id;
        this.template = template;
        this.version = version;
        this.versionTitle = versionTitle;
        this.mainColor = mainColor;
        this.subColor1 = subColor.get(0);
        this.subColor2 = subColor.get(1);
        this.font = font;
        this.logoImageUrl = logoImageUrl;
        this.previewUrl = previewUrl;
        this.padding1 = padding.get(0);
        this.padding2 = padding.get(1);
        this.isCheckPerPerson = isCheckPerPerson;
        this.headCount = headCount;
        this.headDiscountRate = headDiscountRate;
        this.isCheckPerYear = isCheckPerYear;
        this.yearDiscountRate = yearDiscountRate;
        this.isCardSet = isCardSet;
        this.priceCardAreaPadding = priceCardAreaPadding;
        this.priceCardDetailMaxHeight = priceCardDetailMaxHeight;
        this.isDeleted = false;
        this.setUpdatedAt(updateAt);
    }

    public void setTemplate(Template template){
        this.template = template;
    }

    public void setHeadDiscount(List<TemplateRequest.SaveInDTO.HeadDiscount> headDiscount){
        this.headCount = headDiscount.stream().map(headCount -> headCount.getHeadCount()).collect(Collectors.toList());
        this.headDiscountRate = headDiscount.stream().map(discount -> discount.getDiscountRate()).collect(Collectors.toList());
    }

    public void setYearDiscountRate(Integer discountRate){ this.yearDiscountRate = discountRate;}
    @Override
    public String toString() {
        return "TemplateVersion{" +
                "id=" + id +
                ", version=" + version +
                ", versionTitle='" + versionTitle + '\'' +
                ", template=" + template +
                ", mainColor='" + mainColor + '\'' +
                ", subColor1='" + subColor1 + '\'' +
                ", subColor2='" + subColor2 + '\'' +
                ", font='" + font + '\'' +
                ", logoImageUrl='" + logoImageUrl + '\'' +
                ", previewUrl='" + previewUrl + '\'' +
                ", padding1=" + padding1 +
                ", padding2=" + padding2 +
                ", isCheckPerPerson=" + isCheckPerPerson +
                ", headCount=" + headCount +
                ", headDiscountRate=" + headDiscountRate +
                ", isCheckPerYear=" + isCheckPerYear +
                ", yearDiscountRate=" + yearDiscountRate +
                ", isCardSet=" + isCardSet +
                ", priceCardAreaPadding=" + priceCardAreaPadding +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
