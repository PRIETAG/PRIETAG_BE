package com.tag.prietag.repository;

import com.tag.prietag.model.TemplateVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TemplateVersionRepository extends JpaRepository<TemplateVersion, Long> {

    // template중 가장 최신 Version
    @Query("select t from TemplateVersion t where t.template.id=:id and t.isDeleted=false order by t.updatedAt desc")
    Page<TemplateVersion> findByTemplateIdLimitOne(@Param("id") Long id, Pageable pageable);

    // 지정 template에 publish된 version 있는지 찾기
    @Query("select t from TemplateVersion t where t.template.id=:templateId and t.id=:versionId and t.isDeleted=false")
    Optional<TemplateVersion> findByPublishTemplateId(@Param("versionId") Long userPublishId, @Param("templateId") Long templateId);

    // template에 대한 Version들 조회
    @Query("select t from TemplateVersion t where t.template.id=:id and t.isDeleted=false order by t.updatedAt desc")
    Page<TemplateVersion> findByTemplateId(@Param("id") Long id, Pageable pageable);



    // 버전이 가장 높은 템플릿 조회
    @Query("select t from TemplateVersion t where t.template.id = :id and t.version = (select max (t2.version) from TemplateVersion t2 where t2.template.id = :id and t2.isDeleted = false)")
    TemplateVersion findByTemplateIdMaxVersion(@Param("id") Long templateId);
    // 성능 비교 (jpql 은 limit 이 없음)
    // @Query("select t from TemplateVersion t where t.template.id=:id and t.isDeleted=false order by t.version desc")
    // 이 것도 동작할까? -> 정렬은 nlog(n)
    // TemplateVersion findFirstByTemplateIdAndIsDeletedOrderByVersionDesc(Long templateId, boolean isDeleted);

    // 가장 높은 버전을 가진 템플릿 버전의 id 조회
    @Query("select t.id from TemplateVersion t where t.template.id = :id and t.version = (select max (t2.version) from TemplateVersion t2 where t2.template.id = :id and t2.isDeleted = false)")
    Long findIdByTemplateIdMaxVersion(@Param("id") Long templateId);

    // 가장 높은 버전
    // TODO: 다른 방법 있나 고민 해보기
    @Query("select max(t.version) from TemplateVersion t where t.template.id=:id and t.isDeleted=false")
    Integer findMaxVersionByTemplateId(@Param("id") Long id);

    // 버전이 가장 높은 템플릿
    @Query("select t from TemplateVersion t where t.template.id=:id and t.version=(select max(t.version) from TemplateVersion t where t.template.id=:id and t.isDeleted=false) and t.isDeleted=false")
    TemplateVersion findMaxVersionTemplate(@Param("id") Long id);

    // 템플릿 버전들 조회
    List<TemplateVersion> findAllByTemplateId(Long templateId);

    // 메인타이틀로 버전 조회
    @Query("select t from TemplateVersion t where t.template.mainTitle=:mainTitle and t.isDeleted=false")
    List<TemplateVersion> findAllByMainTitle(@Param("mainTitle") String mainTitle);
}
