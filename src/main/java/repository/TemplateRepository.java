package repository;

import model.Template;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    @Query("select t from Template t where t.mainTitle=:name")
    Optional<Template> findByTemplateName(@Param("name") String name);

    @Query("select t from Template t join fetch TemplateVersion tv where t.user.id=:id order by t.updatedAt desc")
    Page<Template> findByUserId(@Param("id") Long id, Pageable pageable);
}
