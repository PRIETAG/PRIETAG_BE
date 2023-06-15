package repository;

import model.TemplateVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TemplateVersionRepository extends JpaRepository<TemplateVersion, Long> {

    // template중 가장 최신 Version
    @Query("select t from TemplateVersion t where t.template.id=:id order by t.updatedAt desc limit 1")
    Optional<TemplateVersion> findByTemplateId(@Param("id") Long id);

    // 지정 template에 publish된 version찾기
    @Query("select t from TemplateVersion t where t.template.id=:templateId and t.id=:versionId")
    Optional<TemplateVersion> findByPublishTemplateId(@Param("versionId") Long versionId, @Param("templateId") Long templateId);
}
