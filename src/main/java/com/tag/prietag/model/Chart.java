package com.tag.prietag.model;

import com.tag.prietag.core.util.StringListConverter;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Getter
@Table(name = "chart_tb")
@Entity
public class Chart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    TemplateVersion templateVersion;

    @Column(nullable = false)
    private boolean haveHeader;
    @Column(nullable = false)
    private String featureName;
    // 몇번째 차트인지
    @Column(nullable = false)
    private Integer chartNum;
    //순서 저장
    @Column(nullable = false)
    private Integer index;

    private String feature;

    @Convert(converter = StringListConverter.class)
    private List<String> desc;

    @Builder
    public Chart(Long id, TemplateVersion templateVersion, boolean haveHeader, String featureName, Integer chartNum, Integer index, String feature, List<String> desc) {
        this.id = id;
        this.templateVersion = templateVersion;
        this.haveHeader = haveHeader;
        this.featureName= featureName;
        this.chartNum = chartNum;
        this.index = index;
        this.feature = feature;
        this.desc = desc;
    }

    public Chart toEntity(TemplateVersion templateVersion){
        return Chart.builder()
                .templateVersion(templateVersion)
                .haveHeader(haveHeader)
                .featureName(featureName)
                .chartNum(chartNum)
                .index(index)
                .feature(feature)
                .desc(desc)
                .build();
    }

    public void setTemplateVersion(TemplateVersion templateVersion){
        this.templateVersion = templateVersion;
    }
}
