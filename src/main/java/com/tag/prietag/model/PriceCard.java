package com.tag.prietag.model;

import com.tag.prietag.core.util.StringListConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;

@NoArgsConstructor
@Getter
@Table(name = "pricecard_tb")
@Entity
public class PriceCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_title")
    private String cardTitle;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false, name = "discount_rate")
    private Integer discountRate;

    private String detail;

    private String feature;

    @Convert(converter = StringListConverter.class)
    private ArrayList<String> content;
}
