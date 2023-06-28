package com.tag.prietag.repository;

import com.tag.prietag.model.Template;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {

    // templateName 있는지
    @Query("select t from Template t where t.mainTitle=:name and t.isDeleted=false")
    Optional<Template> findByTemplateName(@Param("name") String name);

    // 해당 유저의 template 조회
    @Query("select t from Template t where t.user.id=:id and t.isDeleted=false order by t.updatedAt desc")
    Page<Template> findByUserId(@Param("id") Long id, Pageable pageable);

    @Query("select t from Template t where t.id=:id and t.isDeleted=false")
    Optional<Template> findById(@Param("id") Long id);

    List<Template> findByMainTitleContainingAndIsDeleted(String mainTitle, boolean isDeleted);
}
