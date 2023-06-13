package repository;

import model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    @Query("select t from Template t where t.mainTitle=:name")
    Optional<Template> findByTemplateName(@Param("name") String name);
}
