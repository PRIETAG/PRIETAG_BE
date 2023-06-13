package model;

import com.vladmihalcea.hibernate.type.json.JsonType;
import core.util.IntegerListConverter;
import core.util.StringListConverter;
import core.util.TimeStamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.ArrayList;

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

    @OneToMany(fetch = FetchType.LAZY)
    private ArrayList<Field> priceCardField;

    @OneToMany(fetch = FetchType.LAZY)
    private ArrayList<Field> chartField;

    @OneToMany(fetch = FetchType.LAZY)
    private ArrayList<Field> faqField;

    @Column(nullable = false)
    @OneToMany(fetch = FetchType.LAZY)
    private ArrayList<PriceCard> priceCard;

    @OneToMany(fetch = FetchType.LAZY)
    private ArrayList<Chart> chart;

    @OneToMany(fetch = FetchType.LAZY)
    private ArrayList<Faq> faq;

    @Column(nullable = false)
    private String mainColor;
    @Convert(converter = StringListConverter.class)
    private ArrayList<String> subColor;
    @Column(nullable = false)
    private String font;
    @Column(nullable = false)
    private String logoImageUrl;

    @Convert(converter = IntegerListConverter.class)
    private ArrayList<Integer> padding;

    @Column(nullable = false)
    private String templateName;

    @Column(nullable = false)
    private boolean isCheckPerPerson;

    @Convert(converter = IntegerListConverter.class)
    private ArrayList<Integer> headCount;
    @Convert(converter = IntegerListConverter.class)
    private ArrayList<Integer> headDiscountRate;

    @Column(nullable = false)
    private boolean isCheckPerYear;
    private Integer yearDiscountRate;

    @Builder
    public TemplateVersion(Long id, Integer version, String versionTitle, ArrayList<Field> priceCardField, ArrayList<Field> chartField, ArrayList<Field> faqField, ArrayList<PriceCard> priceCard, ArrayList<Chart> chart, ArrayList<Faq> faq, String mainColor, ArrayList<String> subColor, String font, String logoImageUrl, ArrayList<Integer> padding, String templateName, boolean isCheckPerPerson, ArrayList<Integer> headCount, ArrayList<Integer> headDiscountRate, boolean isCheckPerYear, Integer yearDiscountRate) {
        this.id = id;
        this.version = version;
        this.versionTitle = versionTitle;
        this.priceCardField = priceCardField;
        this.chartField = chartField;
        this.faqField = faqField;
        this.priceCard = priceCard;
        this.chart = chart;
        this.faq = faq;
        this.mainColor = mainColor;
        this.subColor = subColor;
        this.font = font;
        this.logoImageUrl = logoImageUrl;
        this.padding = padding;
        this.templateName = templateName;
        this.isCheckPerPerson = isCheckPerPerson;
        this.headCount = headCount;
        this.headDiscountRate = headDiscountRate;
        this.isCheckPerYear = isCheckPerYear;
        this.yearDiscountRate = yearDiscountRate;
    }
}
