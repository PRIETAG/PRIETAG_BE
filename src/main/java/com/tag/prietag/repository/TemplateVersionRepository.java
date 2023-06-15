package com.tag.prietag.repository;

import com.tag.prietag.model.TemplateVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TemplateVersionRepository extends JpaRepository<TemplateVersion, Long> {

    // template중 가장 최신 Version
    @Query("select t from TemplateVersion t where t.template.id=:id and t.isDeleted=false order by t.updatedAt desc")
    Page<TemplateVersion> findByTemplateIdLimitOne(@Param("id") Long id, Pageable pageable);

    // 지정 template에 publish된 version찾기
    @Query("select t from TemplateVersion t where t.template.id=:templateId and t.id=:versionId and t.isDeleted=false")
    Optional<TemplateVersion> findByPublishTemplateId(@Param("versionId") Long versionId, @Param("templateId") Long templateId);

    // template에 대한 Version들 조회
    @Query("select t from TemplateVersion t where t.template.id=:id and t.isDeleted=false order by t.updatedAt desc")
    Page<TemplateVersion> findByTemplateId(@Param("id") Long id, Pageable pageable);


}
