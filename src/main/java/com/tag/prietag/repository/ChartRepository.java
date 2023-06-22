package com.tag.prietag.repository;

import com.tag.prietag.model.Chart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChartRepository extends JpaRepository<Chart, Long> {

    // templateVersionId로 Chart 가져오기 (인덱스 순서 대로)
    List<Chart> findAllByTemplateVersionIdOrderByIndex(Long templateVersionId);
    // @Query("select c from Chart c where c.templateVersion.id=:templateVersionId order by c.index asc")
    // Chart findAllByTemplateVersionIdOOrderByIndex(Long templateVersionId);

}
