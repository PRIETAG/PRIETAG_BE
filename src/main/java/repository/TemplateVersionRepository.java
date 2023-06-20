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

    // template에 대한 Version들 조회
    @Query("select t from TemplateVersion t where t.template.id=:id and t.isDeleted=false order by t.updatedAt desc")
    Page<TemplateVersion> findByTemplateId(@Param("id") Long id, Pageable pageable);


    // 가장 높은 버전을 가진 템플릿버전의 id 조회
    @Query("select t.id from TemplateVersion t where t.template.id = :id and t.version = (select max (t2.version) from TemplateVersion t2 where t2.template.id = :id and t2.isDeleted = false)")
    Long findIdByTemplateIdMaxVersion(@Param("id") Long templateId);

}
