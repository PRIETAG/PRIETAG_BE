package com.tag.prietag.repository;

import com.tag.prietag.model.PriceCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PriceCardRepository extends JpaRepository<PriceCard, Long> {

    // templateVersionId로 PriceCard 가져오기 (인덱스 순서 대로)
    List<PriceCard> findAllByTemplateVersionIdOrderByIndex(Long templateVersionId);
//    @Query("select p from PriceCard p where p.templateVersion.id=:templateVersionId order by p.index asc")
//    List<PriceCard> findAllByTemplateVersionIdOOrderByIndex(Long templateVersionId);
}
