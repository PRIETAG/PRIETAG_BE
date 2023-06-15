package model;

import core.util.StringListConverter;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@NoArgsConstructor
@Getter
@Table(name = "pricecard_tb")
@Entity
public class PriceCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer index;

    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    TemplateVersion templateVersion;

    @Column(name = "card_title")
    private String cardTitle;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false, name = "discount_rate")
    private Integer discountRate;

    private String detail;

    private String feature;

    @Convert(converter = StringListConverter.class)
    private List<String> content;

    @Builder
    public PriceCard(Long id, Integer index, TemplateVersion templateVersion, String cardTitle, Integer price, Integer discountRate, String detail, String feature, List<String> content) {
        this.id = id;
        this.index= index;
        this.templateVersion = templateVersion;
        this.cardTitle = cardTitle;
        this.price = price;
        this.discountRate = discountRate;
        this.detail = detail;
        this.feature = feature;
        this.content = content;
    }

    public void setTemplateVersion(TemplateVersion templateVersion){
        this.templateVersion = templateVersion;
    }
}
