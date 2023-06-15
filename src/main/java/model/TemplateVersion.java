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
import java.util.List;

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
    @Column(nullable = false)
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
    private boolean isDeleted;

    @Builder
    public TemplateVersion(Long id, Integer version, String versionTitle, String mainColor, List<String> subColor, String font, String logoImageUrl, String previewUrl, List<Integer> padding, boolean isCheckPerPerson, List<Integer> headCount, List<Integer> headDiscountRate, boolean isCheckPerYear, Integer yearDiscountRate, boolean isCardSet) {
        this.id = id;
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
        this.isDeleted = false;
    }

    public void setTemplate(Template template){
        this.template = template;
    }
}
