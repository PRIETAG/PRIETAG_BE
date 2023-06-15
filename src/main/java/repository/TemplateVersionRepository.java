package repository;

import model.TemplateVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TemplateVersionRepository extends JpaRepository<TemplateVersion, Long> {

    // template중 가장 최신 Version
    @Query("select t from TemplateVersion t where t.template.id=:id and t.isDeleted=false order by t.updatedAt desc limit 1")
    Optional<TemplateVersion> findByTemplateIdLimitOne(@Param("id") Long id);

    // 지정 template에 publish된 version찾기
    @Query("select t from TemplateVersion t where t.template.id=:templateId and t.id=:versionId and t.isDeleted=false")
    Optional<TemplateVersion> findByPublishTemplateId(@Param("versionId") Long versionId, @Param("templateId") Long templateId);

    @Query("select t from TemplateVersion t where t.template.id=:id and t.isDeleted=false order by t.updatedAt desc")
    Page<TemplateVersion> findByTemplateId(@Param("id") Long id, Pageable pageable);

    // 가장 높은 버전
    // TODO: 다른 방법 있나 고민해보기
    @Query("select max(t.version) from TemplateVersion t where t.template.id=:id and t.isDeleted=false")
    Integer findMaxVersionByTemplateId(@Param("id") Long id);

    // 버전이 가장 높은 템플릿
    @Query("select t from TemplateVersion t where t.template.id=:id and t.version=(select max(t.version) from TemplateVersion t where t.template.id=:id and t.isDeleted=false) and t.isDeleted=false")
    TemplateVersion findMaxVersionTemplate(@Param("id") Long id);
}
