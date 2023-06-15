package repository;

import model.Field;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FieldRopository extends JpaRepository<Field, Long> {

    // templateVersionId랑 areaNum 으로 Field 가져오기 (인덱스 순서 대로)
    List<Field> findAllByTemplateVersionIdAndAreaNumOrderByIndex(Long templateVersionId, Integer areaNum);
    // @Query("select f from Field f where f.templateVersion.id=:templateVersionId and f.areaNum=:areaNum order by f.index asc")
    // Field findAllByTemplateVersionIdAndAreaNumOrderByIndex(Long templateVersionId, Integer areaNum);
}
