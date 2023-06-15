package repository;

import model.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {

    // templateVersionId로 Faq 가져오기 (인덱스 순서 대로)
    List<Faq> findAllByTemplateVersionIdOOrderByIndex(Long templateVersionId);
    // @Query("select f from Faq f where f.templateVersion.id=:templateVersionId order by f.index asc")
    // Faq findAllByTemplateVersionIdOOrderByIndex(Long templateVersionId);
}
